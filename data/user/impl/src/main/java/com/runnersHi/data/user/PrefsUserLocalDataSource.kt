package com.runnersHi.data.user

import android.content.Context
import android.content.SharedPreferences
import com.runnersHi.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** 앱을 다시 켜도 프로필이 남도록 SharedPreferences에 저장한다. */
class PrefsUserLocalDataSource(context: Context) : UserLocalDataSource {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("runnershi_user", Context.MODE_PRIVATE)

    private val currentUser = MutableStateFlow(read())

    override fun getCurrentUser(): Flow<User?> = currentUser

    override suspend fun saveUser(user: User) {
        prefs.edit()
            .putString(KEY_ID, user.id)
            .putString(KEY_NAME, user.name)
            .putString(KEY_IMAGE, user.profileImageUrl)
            .apply()
        currentUser.value = user
    }

    override suspend fun clearUser() {
        prefs.edit().clear().apply()
        currentUser.value = null
    }

    private fun read(): User? {
        val id = prefs.getString(KEY_ID, null) ?: return null
        val name = prefs.getString(KEY_NAME, null) ?: return null
        return User(
            id = id,
            name = name,
            profileImageUrl = prefs.getString(KEY_IMAGE, null),
            totalDistance = 0.0,
            totalRuns = 0
        )
    }

    private companion object {
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_IMAGE = "image"
    }
}
