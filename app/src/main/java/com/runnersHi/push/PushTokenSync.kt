package com.runnersHi.push

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.runnersHi.domain.user.usecase.UpdatePushTokenUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/** Firebase가 설정된 빌드에서만 푸시 토큰을 서버에 올린다. */
@Singleton
class PushTokenSync @Inject constructor(
    @ApplicationContext private val context: Context,
    private val updatePushToken: UpdatePushTokenUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val isAvailable: Boolean
        get() = FirebaseApp.getApps(context).isNotEmpty()

    fun syncCurrentToken() {
        if (!isAvailable) return
        FirebaseMessaging.getInstance().token.addOnSuccessListener(::send)
    }

    /** 로그인 전이면 서버가 거절한다. 메인 화면에 들어올 때 다시 올린다. */
    fun send(token: String) {
        scope.launch { updatePushToken(token) }
    }
}
