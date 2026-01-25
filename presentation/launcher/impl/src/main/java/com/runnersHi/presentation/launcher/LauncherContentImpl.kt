package com.runnersHi.presentation.launcher

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.launcher.api.LauncherContract
import com.runnersHi.presentation.launcher.impl.R
import com.runnersHi.presentation.login.api.LoginContent
import com.runnersHi.presentation.login.api.LoginContract
import com.runnersHi.presentation.splash.api.SplashContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Launcher 화면 Route (ViewModel 연결)
 *
 * @param splashContent 스플래시 컨텐츠 (app에서 impl 주입)
 * @param loginContent 로그인 컨텐츠 (app에서 impl 주입)
 */
@Composable
fun LauncherRoute(
    viewModel: LauncherViewModel = hiltViewModel(),
    splashContent: SplashContent,
    loginContent: LoginContent,
    currentVersion: String,
    onNavigateToHome: () -> Unit,
    onNavigateToTermsAgreement: () -> Unit,
    onKakaoLogin: (suspend (android.content.Context) -> String)? = null,
    onAppleLogin: (suspend (android.content.Context) -> String)? = null
) {
    val state by viewModel.collectState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Effect 처리
    viewModel.collectEffect { effect ->
        when (effect) {
            is LauncherContract.Effect.NavigateToHome -> onNavigateToHome()
            is LauncherContract.Effect.NavigateToTermsAgreement -> onNavigateToTermsAgreement()
            is LauncherContract.Effect.OpenPlayStore -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${context.packageName}")
                )
                context.startActivity(intent)
            }
            is LauncherContract.Effect.RequestKakaoLogin -> {
                onKakaoLogin?.let { login ->
                    scope.launch {
                        try {
                            val token = login(context)
                            viewModel.sendEvent(
                                LauncherContract.Event.LoginEvent(
                                    LoginContract.Event.KakaoTokenReceived(token)
                                )
                            )
                        } catch (e: Exception) {
                            viewModel.sendEvent(
                                LauncherContract.Event.LoginEvent(
                                    LoginContract.Event.LoginFailed(e.message ?: "카카오 로그인 실패")
                                )
                            )
                        }
                    }
                }
            }
            is LauncherContract.Effect.RequestAppleLogin -> {
                onAppleLogin?.let { login ->
                    scope.launch {
                        try {
                            val token = login(context)
                            viewModel.sendEvent(
                                LauncherContract.Event.LoginEvent(
                                    LoginContract.Event.AppleTokenReceived(token)
                                )
                            )
                        } catch (e: Exception) {
                            viewModel.sendEvent(
                                LauncherContract.Event.LoginEvent(
                                    LoginContract.Event.LoginFailed(e.message ?: "Apple 로그인 실패")
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // 화면 시작 시 앱 상태 체크
    LaunchedEffect(Unit) {
        viewModel.sendEvent(LauncherContract.Event.CheckAppStatus(currentVersion))
    }

    // ForceUpdate 다이얼로그
    state.splashState.forceUpdate?.let {
        ForceUpdateDialog(
            onUpdateClick = {
                viewModel.sendEvent(LauncherContract.Event.ForceUpdateConfirmed)
            }
        )
    }

    LauncherScreen(
        state = state,
        splashContent = splashContent,
        loginContent = loginContent,
        onEvent = viewModel::sendEvent
    )
}

/**
 * Launcher 화면 (Stateless)
 * - 로고 + 애니메이션 관리
 * - splashContent, loginContent 조합
 *
 * @param splashContent 스플래시 컨텐츠 (외부에서 주입)
 * @param loginContent 로그인 컨텐츠 (외부에서 주입)
 */
@Composable
fun LauncherScreen(
    state: LauncherContract.State,
    splashContent: SplashContent,
    loginContent: LoginContent,
    onEvent: (LauncherContract.Event) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp

    // 로그인 UI 표시 여부
    val showLoginUI = state.phase == LauncherContract.Phase.LOGIN ||
                      state.phase == LauncherContract.Phase.LOGGING_IN

    // 애니메이션 상태
    val logoOffsetY = remember { Animatable(0f) }
    val appNameAlpha = remember { Animatable(0f) }
    val loadingAlpha = remember { Animatable(1f) }
    val buttonsAlpha = remember { Animatable(0f) }
    val buttonsOffsetY = remember { Animatable(100f) }

    // 로그인 UI 전환 애니메이션
    LaunchedEffect(showLoginUI) {
        if (showLoginUI) {
            val targetOffset = -(screenHeightDp * 0.12f)

            // 동시에 실행
            launch {
                logoOffsetY.animateTo(
                    targetValue = targetOffset,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )
            }
            launch {
                loadingAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 200)
                )
            }
            launch {
                delay(200)
                appNameAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 300)
                )
            }
            // 버튼 페이드인 + 슬라이드업 동시 실행
            launch {
                delay(300)
                buttonsAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 300)
                )
            }
            launch {
                delay(300)
                buttonsOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueGray90)
    ) {
        // 로고 + 앱 이름 영역 (Launcher에서 관리)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset { IntOffset(0, logoOffsetY.value.dp.roundToPx()) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_runnershi),
                contentDescription = "RunnersHi Logo",
                modifier = Modifier.size(80.dp)
            )

            if (showLoginUI) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Runners HI",
                    color = Primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alpha(appNameAlpha.value)
                )
            }
        }

        // 스플래시 컨텐츠 (외부에서 주입받음)
        if (state.phase == LauncherContract.Phase.SPLASH) {
            splashContent(
                state.splashState,
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 60.dp)
                    .alpha(loadingAlpha.value)
            )
        }

        // 로그인 컨텐츠 (외부에서 주입받음)
        if (showLoginUI) {
            loginContent(
                state.loginState,
                { loginEvent -> onEvent(LauncherContract.Event.LoginEvent(loginEvent)) },
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 104.dp)
                    .alpha(buttonsAlpha.value)
                    .offset { IntOffset(0, buttonsOffsetY.value.dp.roundToPx()) }
            )
        }
    }
}
