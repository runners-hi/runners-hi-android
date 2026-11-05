package com.runnersHi.auth

import com.runnersHi.domain.auth.model.AppleCredential
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Apple 웹 로그인 주소 생성과 결과 해석.
 * 이름·이메일 scope를 요청하지 않아 response_mode=fragment를 쓸 수 있고, 결과를 앱이 직접 읽는다.
 * https://developer.apple.com/documentation/signinwithapplerestapi/request-an-authorization-to-the-sign-in-with-apple-server.
 */
internal object AppleAuthUrl {

    sealed interface Result {
        data class Success(val credential: AppleCredential) : Result
        data object Cancelled : Result
        data class Failure(val reason: String) : Result
    }

    fun authorize(baseUrl: String, serviceId: String, redirectUri: String, state: String): String =
        baseUrl +
            "?client_id=" + encode(serviceId) +
            "&redirect_uri=" + encode(redirectUri) +
            "&response_type=code%20id_token" +
            "&response_mode=fragment" +
            "&state=" + encode(state)

    /** 로그인 결과 주소가 아니면 null */
    fun parseRedirect(url: String, redirectUri: String, expectedState: String): Result? {
        if (url != redirectUri && !url.startsWith("$redirectUri#") && !url.startsWith("$redirectUri?")) return null

        val params = url.substringAfter(redirectUri).trimStart('#', '?')
            .split("&")
            .filter { it.contains("=") }
            .associate { decode(it.substringBefore("=")) to decode(it.substringAfter("=")) }

        if (params["state"] != expectedState) return Result.Failure("state 불일치")
        params["error"]?.let { error ->
            return if (error == "user_cancelled_authorize") Result.Cancelled else Result.Failure(error)
        }
        val idToken = params["id_token"]
        val code = params["code"]
        if (idToken.isNullOrBlank() || code.isNullOrBlank()) return Result.Failure("토큰 없음")
        return Result.Success(AppleCredential(idToken, code))
    }

    private fun encode(value: String) = URLEncoder.encode(value, "UTF-8").replace("+", "%20")
    private fun decode(value: String) = URLDecoder.decode(value, "UTF-8")
}
