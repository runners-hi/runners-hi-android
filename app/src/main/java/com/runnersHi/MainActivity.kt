package com.runnersHi

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.runnersHi.navigation.RunnersHiNavHost
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import com.runnersHi.push.PushTokenSync
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var pushTokenSync: PushTokenSync

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private var pushPrepared = false

    /** 하위 화면에서 돌아올 때도 불리므로 한 번만 처리한다. */
    private fun onMainEntered() {
        if (pushPrepared || !pushTokenSync.isAvailable) return
        pushPrepared = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        pushTokenSync.syncCurrentToken()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 시스템 스플래시 화면 즉시 종료
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RunnersHiTheme {
                RunnersHiNavHost(onMainEntered = ::onMainEntered)
            }
        }
    }
}
