package com.runnersHi.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/** 발급받은 주소로 이미지를 올린다. 서명이 깨지므로 앱 인증 헤더를 붙이지 않는다. */
class ImageUploader(private val client: OkHttpClient = OkHttpClient()) {

    suspend fun put(url: String, bytes: ByteArray, contentType: String) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .put(bytes.toRequestBody(contentType.toMediaType()))
            .header("Content-Type", contentType)
            // 서버가 서명에 넣은 값과 같아야 한다
            .header("Cache-Control", "public, max-age=300")
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw ApiException("UPLOAD_FAILED", response.code, "사진을 올리지 못했어요")
                }
            }
        } catch (e: IOException) {
            throw NetworkException(e)
        }
    }
}
