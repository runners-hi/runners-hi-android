package com.runnersHi.core.network

import kotlinx.serialization.json.JsonElement
import retrofit2.HttpException
import java.io.IOException

/** 서버가 오류로 응답했다. message는 서버가 준 사용자용 문구다. */
class ApiException(
    val code: String,
    val httpStatus: Int,
    message: String
) : Exception(message)

/** 서버에 닿지 못했다. */
class NetworkException(cause: Throwable) : Exception("서버에 연결할 수 없어요", cause)

/** 호출 결과를 data로 풀고, 실패는 [ApiException]/[NetworkException]으로 바꾼다. */
suspend fun <T> apiCall(block: suspend () -> ApiEnvelope<T>): T =
    apiCallNullable(block) ?: throw ApiException("EMPTY", 200, "서버 응답이 비어 있어요")

suspend fun <T> apiCallNullable(block: suspend () -> ApiEnvelope<T>): T? {
    val envelope = try {
        block()
    } catch (e: HttpException) {
        throw e.toApiException()
    } catch (e: IOException) {
        throw NetworkException(e)
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
    return ApiException(error?.code.orEmpty(), code(), error?.message?.ifBlank { null } ?: DEFAULT_MESSAGE)
}

private const val DEFAULT_MESSAGE = "요청을 처리하지 못했어요"
