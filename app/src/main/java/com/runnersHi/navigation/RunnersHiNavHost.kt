package com.runnersHi.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.login.LoginRoute
import com.runnersHi.presentation.main.MainScreen
import com.runnersHi.presentation.splash.SplashRoute
import com.runnersHi.presentation.splash.SplashViewModel

/**
 * 앱 전체 네비게이션 호스트
 */
@Composable
fun RunnersHiNavHost() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            when {
                // Splash -> 다른 화면: fade out splash, slide in next screen
                initialState == Screen.Splash -> {
                    (fadeIn(animationSpec = tween(300)) +
                            slideInHorizontally(
                                animationSpec = tween(300),
                                initialOffsetX = { fullWidth -> fullWidth }
                            )).togetherWith(
                        fadeOut(animationSpec = tween(300))
                    )
                }
                // 기본 전환
                else -> {
                    fadeIn(animationSpec = tween(300)).togetherWith(
                        fadeOut(animationSpec = tween(300))
                    )
                }
            }
        },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            is Screen.Splash -> {
                val viewModel: SplashViewModel = hiltViewModel()

                SplashRoute(
                    viewModel = viewModel,
                    currentVersion = "1.0.0", // TODO: BuildConfig.VERSION_NAME 사용
                    onNavigateToLogin = {
                        currentScreen = Screen.Login
                    },
                    onNavigateToHome = {
                        currentScreen = Screen.Main
                    }
                )
            }
            is Screen.Login -> {
                LoginRoute(
                    onNavigateToHome = {
                        currentScreen = Screen.Main
                    },
                    onNavigateToOnboarding = {
                        // TODO: 온보딩 화면 구현 후 연결
                        currentScreen = Screen.Main
                    },
                    onKakaoLoginRequest = {
                        // TODO: 카카오 SDK 연동
                    },
                    onAppleLoginRequest = {
                        // TODO: Apple Sign In 연동
                    }
                )
            }
            is Screen.Main -> {
                MainScreen()
            }
        }
    }
}

/**
 * 앱 최상위 화면 (Splash 이후 분기점)
 */
sealed class Screen {
    data object Splash : Screen()
    data object Login : Screen()
    data object Main : Screen()
}
