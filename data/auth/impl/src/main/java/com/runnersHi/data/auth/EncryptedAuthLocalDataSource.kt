package com.runnersHi.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.runnersHi.domain.auth.model.AuthToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 인증 토큰을 EncryptedSharedPreferences에 저장한다.
 */
@Suppress("DEPRECATION")
class EncryptedAuthLocalDataSource(
    private val context: Context
) : AuthLocalDataSource {

    private val prefs: SharedPreferences by lazy { openPrefs() }

    override suspend fun getToken(): AuthToken? = withContext(Dispatchers.IO) {
        val accessToken = prefs.getString(KEY_ACCESS, null) ?: return@withContext null
        val refreshToken = prefs.getString(KEY_REFRESH, null) ?: return@withContext null
        AuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = prefs.getLong(KEY_EXPIRES_IN, 0L)
        )
    }

    override suspend fun saveToken(token: AuthToken) = withContext(Dispatchers.IO) {
        prefs.edit {
            putString(KEY_ACCESS, token.accessToken)
            putString(KEY_REFRESH, token.refreshToken)
            putLong(KEY_EXPIRES_IN, token.expiresIn)
        }
    }

    override suspend fun clearToken() = withContext(Dispatchers.IO) {
        prefs.edit { clear() }
    }

    override fun hasToken(): Boolean = prefs.contains(KEY_ACCESS)

    private fun openPrefs(): SharedPreferences = try {
        createEncryptedPrefs()
    } catch (e: Exception) {
        // 키스토어 키가 사라져 복호화할 수 없으면 비우고 다시 만든다 (재로그인으로 복구).
        context.deleteSharedPreferences(FILE_NAME)
        createEncryptedPrefs()
    }

    private fun createEncryptedPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private companion object {
        const val FILE_NAME = "runnershi_auth"
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_EXPIRES_IN = "expires_in"
    }
}
