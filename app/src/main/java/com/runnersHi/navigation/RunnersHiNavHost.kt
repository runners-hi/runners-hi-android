package com.runnersHi.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.auth.AppleLoginHandler
import com.runnersHi.auth.KakaoLoginHandler
import com.runnersHi.presentation.main.MainScreen
import com.runnersHi.presentation.splash.SplashRoute
import com.runnersHi.presentation.splash.SplashViewModel
import com.runnersHi.presentation.terms.TermsAgreementRoute

/**
 * 앱 전체 네비게이션 호스트
 */
@Composable
fun RunnersHiNavHost() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            // 모든 화면 전환은 페이드로 통일 (Splash → Login은 내부 애니메이션 사용)
            fadeIn(animationSpec = tween(300)).togetherWith(
                fadeOut(animationSpec = tween(300))
            )
        },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            is Screen.Splash -> {
                val viewModel: SplashViewModel = hiltViewModel()

                SplashRoute(
                    viewModel = viewModel,
                    currentVersion = "1.0.0", // TODO: BuildConfig.VERSION_NAME 사용
                    onNavigateToHome = {
                        currentScreen = Screen.Main
                    },
                    onNavigateToTermsAgreement = {
                        currentScreen = Screen.TermsAgreement
                    },
                    onKakaoLogin = { context ->
                        KakaoLoginHandler.login(context)
                    },
                    onAppleLogin = { context ->
                        AppleLoginHandler.login(context)
                    }
                )
            }
            is Screen.TermsAgreement -> {
                TermsAgreementRoute(
                    onNavigateToMain = { currentScreen = Screen.Main }
                )
            }
            is Screen.Main -> {
                MainScreen()
            }
        }
    }
}

/**
 * 앱 최상위 화면
 */
sealed class Screen {
    data object Splash : Screen()       // 스플래시 + 로그인 (통합)
    data object TermsAgreement : Screen()  // 신규 유저 이용약관 동의
    data object Main : Screen()
}
