package com.runnersHi.core.network

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import com.runnersHi.core.common.analytics.AnalyticsHub
import com.runnersHi.core.common.analytics.ApiErrorKind
import okhttp3.Interceptor
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import java.io.IOException
import java.util.concurrent.TimeUnit

/** 네트워크 계층이 쓰는 토큰 저장소. OkHttp 스레드에서 동기로 불린다. */
interface TokenStore {
    fun accessToken(): String?
    fun refreshToken(): String?
    fun save(accessToken: String, refreshToken: String)
    fun clear()
}

/** 갱신 토큰까지 만료돼 다시 로그인해야 할 때 알린다. */
object SessionEvents {
    private val _expired = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val expired: SharedFlow<Unit> = _expired.asSharedFlow()

    internal fun notifyExpired() {
        _expired.tryEmit(Unit)
    }
}

object NetworkFactory {

    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    /**
     * @param endpoint 요청마다 읽는 서버 주소. 디버그 빌드의 서버 선택이 쓴다. null이면 [baseUrl] 고정.
     */
    fun createApi(baseUrl: String, tokenStore: TokenStore, endpoint: (() -> String)? = null): RunnersHiApi {
        val root = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val builder = OkHttpClient.Builder()
            // 무한 로딩 방지: 모든 구간 15초
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (endpoint != null) builder.addInterceptor(DynamicBaseUrlInterceptor(endpoint))
        builder.addInterceptor(ApiErrorLogInterceptor())
        val plainClient = builder.build()
        val authClient = plainClient.newBuilder()
            .addInterceptor(AuthInterceptor(tokenStore))
            .authenticator(TokenAuthenticator(tokenStore, retrofit(root, plainClient).create(TokenApi::class.java)))
            .build()
        return retrofit(root, authClient).create(RunnersHiApi::class.java)
    }

    private const val TIMEOUT_SECONDS = 15L

    private fun retrofit(baseUrl: String, client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}

private const val AUTH_PATH = "/api/auth/"
private const val LOGOUT_PATH = "/api/auth/logout"
private const val APP_VERSION_PATH = "/api/app/version/"

/**
 * 로그인·토큰 갱신·버전 확인을 뺀 요청에 액세스 토큰을 붙인다.
 * 버전 확인은 로그인과 무관하다. 만료된 토큰을 붙이면 서버가 401로 거절해 앱을 시작할 수 없다.
 */
internal class AuthInterceptor(private val tokenStore: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val isPublic = (path.contains(AUTH_PATH) && !path.endsWith(LOGOUT_PATH)) ||
            path.contains(APP_VERSION_PATH)
        val token = tokenStore.accessToken()
        if (isPublic || token == null) return chain.proceed(request)
        return chain.proceed(request.withBearer(token))
    }
}

/** 401을 받으면 토큰을 한 번 갱신해 다시 보낸다. 갱신이 거절되면 세션을 끝낸다. */
internal class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val tokenApi: TokenApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.priorResponse != null) return null
        if (response.request.url.encodedPath.contains(AUTH_PATH) &&
            !response.request.url.encodedPath.endsWith(LOGOUT_PATH)
        ) return null

        val failedToken = response.request.header(AUTHORIZATION)?.removePrefix(BEARER)
        synchronized(this) {
            val current = tokenStore.accessToken() ?: return null
            // 다른 요청이 먼저 갱신했으면 새 토큰으로 다시 보낸다.
            if (current != failedToken) return response.request.withBearer(current)

            val refreshToken = tokenStore.refreshToken() ?: return null
            val result = try {
                tokenApi.refresh(RefreshTokenRequest(refreshToken)).execute()
            } catch (e: IOException) {
                return null // 일시적인 네트워크 오류로는 로그아웃시키지 않는다.
            }
            val tokens = result.body()?.data
            if (result.isSuccessful && tokens != null) {
                tokenStore.save(tokens.accessToken, tokens.refreshToken)
                return response.request.withBearer(tokens.accessToken)
            }
            if (result.code() in 400..499) {
                tokenStore.clear()
                SessionEvents.notifyExpired()
            }
            return null
        }
    }
}

private const val AUTHORIZATION = "Authorization"
private const val BEARER = "Bearer "

private fun Request.withBearer(token: String): Request =
    newBuilder().header(AUTHORIZATION, BEARER + token).build()

/** 요청의 scheme·host·port를 현재 선택된 서버로 바꾼다. 경로는 그대로 둔다. */
internal class DynamicBaseUrlInterceptor(private val endpoint: () -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val target = endpoint().toHttpUrlOrNull() ?: return chain.proceed(request)
        val url = request.url.newBuilder()
            .scheme(target.scheme)
            .host(target.host)
            .port(target.port)
            .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}

/** 지금 쓰는 서버 주소. 릴리스는 앱 시작 때 상용으로 정해지고 바뀌지 않는다. */
object ServerEndpoint {
    const val PROD_URL = "https://runnershi-api.oguriworks.com"

    @Volatile
    var baseUrl: String = PROD_URL
}

/** 행동 로그 api_error를 네트워크 계층 한 곳에서 남긴다. 경로의 숫자 id는 :id로 바꾼다. */
internal class ApiErrorLogInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val path = chain.request().url.encodedPath
        val response = try {
            chain.proceed(chain.request())
        } catch (e: java.io.IOException) {
            val kind = if (e is java.net.SocketTimeoutException) ApiErrorKind.TIMEOUT else ApiErrorKind.OFFLINE
            AnalyticsHub.apiError(path, 0, kind)
            AnalyticsHub.recordError(e)
            throw e
        }
        if (response.code >= 500) AnalyticsHub.recordError(RuntimeException("HTTP ${response.code} ${AnalyticsHub.normalizePath(path)}"))
        if (!response.isSuccessful) {
            AnalyticsHub.apiError(path, response.code, if (response.code >= 500) ApiErrorKind.SERVER else ApiErrorKind.CLIENT)
        }
        return response
    }
}
