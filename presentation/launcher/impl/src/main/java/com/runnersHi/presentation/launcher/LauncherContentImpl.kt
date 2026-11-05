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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.R as CommonR
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.domain.auth.model.SocialLoginCancelledException
import kotlinx.coroutines.CancellationException
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
    onNavigateToRegionSelection: () -> Unit,
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
            is LauncherContract.Effect.NavigateToRegionSelection -> onNavigateToRegionSelection()
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
                        } catch (e: SocialLoginCancelledException) {
                            viewModel.sendEvent(
                                LauncherContract.Event.LoginEvent(LoginContract.Event.LoginCancelled)
                            )
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            viewModel.sendEvent(
                                LauncherContract.Event.LoginEvent(
                                    LoginContract.Event.LoginFailed("카카오 로그인에 실패했어요")
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
                        } catch (e: SocialLoginCancelledException) {
                            viewModel.sendEvent(
                                LauncherContract.Event.LoginEvent(LoginContract.Event.LoginCancelled)
                            )
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            viewModel.sendEvent(
                                LauncherContract.Event.LoginEvent(
                                    LoginContract.Event.LoginFailed("Apple 로그인에 실패했어요")
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

    // 버전 확인에 실패하거나 점검 중이면 이유를 보여주고 다시 시도하게 한다.
    state.splashState.errorMessage?.let { message ->
        AppStartErrorDialog(
            message = message,
            onRetryClick = {
                viewModel.sendEvent(LauncherContract.Event.CheckAppStatus(currentVersion))
            }
        )
    }

    LauncherScreen(
        state = state.copy(
            loginState = state.loginState.copy(showAppleLogin = onAppleLogin != null)
        ),
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
            val targetOffset = -(screenHeightDp * 0.0935f)

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
            if (state.phase == LauncherContract.Phase.SPLASH) {
                AnimatedSplashLogo(modifier = Modifier.size(80.dp))
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_runnershi),
                    contentDescription = "RunnersHi Logo",
                    modifier = Modifier.size(80.dp)
                )
            }

            if (showLoginUI) {
                Image(
                    painter = painterResource(id = CommonR.drawable.img_wordmark),
                    contentDescription = "Runners HI",
                    colorFilter = ColorFilter.tint(Primary),
                    modifier = Modifier
                        .size(width = 136.dp, height = 40.dp)
                        .alpha(appNameAlpha.value)
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
                    .padding(bottom = 68.dp)
                    .alpha(buttonsAlpha.value)
                    .offset { IntOffset(0, buttonsOffsetY.value.dp.roundToPx()) }
            )
        }
    }
}

private const val SPLASH_FRAME_COUNT = 61
private const val SPLASH_SPRITE_COLUMNS = 11
private const val SPLASH_FRAME_PX = 220
private const val SPLASH_FRAME_MS = 40L

/**
 * iOS loading.apng와 같은 프레임의 로고 애니메이션.
 * 프레임에 여백이 있어 90dp로 그려야 로그인 화면의 80dp 로고와 크기가 맞는다.
 */
@Composable
private fun AnimatedSplashLogo(modifier: Modifier = Modifier) {
    val sprite = ImageBitmap.imageResource(R.drawable.splash_loading_sprite)
    var frame by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(SPLASH_FRAME_MS)
            frame = (frame + 1) % SPLASH_FRAME_COUNT
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = BitmapPainter(
                image = sprite,
                srcOffset = IntOffset(
                    x = frame % SPLASH_SPRITE_COLUMNS * SPLASH_FRAME_PX,
                    y = frame / SPLASH_SPRITE_COLUMNS * SPLASH_FRAME_PX
                ),
                srcSize = IntSize(SPLASH_FRAME_PX, SPLASH_FRAME_PX)
            ),
            contentDescription = "RunnersHi Logo",
            modifier = Modifier.requiredSize(90.dp)
        )
    }
}
