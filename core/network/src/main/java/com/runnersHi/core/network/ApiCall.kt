package com.runnersHi.core.network

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

/** 서버가 오류로 응답했다. message는 서버가 준 사용자용 문구다. */
class ApiException(
    val code: String,
    val httpStatus: Int,
    message: String
) : Exception(message)

/** 서버에 닿지 못했다. */
class NetworkException(cause: Throwable) : Exception(OFFLINE_MESSAGE, cause)

const val OFFLINE_MESSAGE = "인터넷 연결을 확인해 주세요"
const val SERVER_ERROR_MESSAGE = "잠시 후 다시 시도해 주세요"

/** 호출 결과를 data로 풀고, 실패는 [ApiException]/[NetworkException]으로 바꾼다. */
suspend fun <T> apiCall(block: suspend () -> ApiEnvelope<T>): T =
    apiCallNullable(block) ?: throw ApiException("EMPTY", 200, "서버 응답이 비어 있어요")

suspend fun <T> apiCallNullable(block: suspend () -> ApiEnvelope<T>): T? {
    val envelope = try {
        block()
    } catch (e: HttpException) {
        throw e.toApiException()
    } catch (e: IOException) {
        // UnknownHost·ConnectException·SocketTimeout·연결 끊김 모두 오프라인으로 본다.
        throw NetworkException(e)
    } catch (e: SerializationException) {
        throw ApiException("BAD_RESPONSE", 200, SERVER_ERROR_MESSAGE)
    }
    if (!envelope.success) {
        val error = envelope.error
        throw ApiException(error?.code.orEmpty(), 200, error?.message ?: DEFAULT_MESSAGE)
    }
    return envelope.data
}

private fun HttpException.toApiException(): ApiException {
    val body = runCatching { response()?.errorBody()?.string() }.getOrNull()
    val error = body?.let {
        runCatching {
            NetworkFactory.json.decodeFromString(
                ApiEnvelope.serializer(JsonElement.serializer()),
                it
            ).error
        }.getOrNull()
    }
    val status = code()
    // 5xx는 서버 문구 대신 공통 안내를 쓴다. 4xx는 서버가 준 message를 보여 준다.
    val message = if (status >= 500) SERVER_ERROR_MESSAGE
    else error?.message?.ifBlank { null } ?: DEFAULT_MESSAGE
    return ApiException(error?.code.orEmpty(), status, message)
}

private const val DEFAULT_MESSAGE = "요청을 처리하지 못했어요"
