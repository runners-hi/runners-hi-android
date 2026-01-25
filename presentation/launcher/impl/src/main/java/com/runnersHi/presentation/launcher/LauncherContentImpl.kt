package com.runnersHi.presentation.launcher

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.BlueGray95
import com.runnersHi.presentation.common.theme.BlueGrayWhite
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import com.runnersHi.presentation.common.theme.SurfaceVariant
import com.runnersHi.presentation.launcher.api.LauncherContent
import com.runnersHi.presentation.launcher.api.LauncherContract
import com.runnersHi.presentation.launcher.impl.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Figma Colors
private val KakaoYellow = Color(0xFFFEE500)

/**
 * Launcher Content 구현체
 */
val LauncherContentImpl: LauncherContent = { state, onEvent, modifier ->
    LauncherScreen(state = state, onEvent = onEvent, modifier = modifier)
}

/**
 * Launcher 화면 Route (ViewModel 연결)
 */
@Composable
fun LauncherRoute(
    viewModel: LauncherViewModel = hiltViewModel(),
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
                            viewModel.sendEvent(LauncherContract.Event.KakaoTokenReceived(token))
                        } catch (e: Exception) {
                            viewModel.sendEvent(LauncherContract.Event.LoginFailed(e.message ?: "카카오 로그인 실패"))
                        }
                    }
                }
            }
            is LauncherContract.Effect.RequestAppleLogin -> {
                onAppleLogin?.let { login ->
                    scope.launch {
                        try {
                            val token = login(context)
                            viewModel.sendEvent(LauncherContract.Event.AppleTokenReceived(token))
                        } catch (e: Exception) {
                            viewModel.sendEvent(LauncherContract.Event.LoginFailed(e.message ?: "Apple 로그인 실패"))
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
    state.forceUpdateInfo?.let {
        ForceUpdateDialog(
            onUpdateClick = {
                viewModel.sendEvent(LauncherContract.Event.ForceUpdateConfirmed)
            }
        )
    }

    LauncherScreen(
        state = state,
        onEvent = viewModel::sendEvent
    )
}

/**
 * Launcher 화면 (Stateless) - Splash + Login 통합
 */
@Composable
fun LauncherScreen(
    state: LauncherContract.State,
    onEvent: (LauncherContract.Event) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp

    val animatedProgress by animateFloatAsState(
        targetValue = state.splashProgress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

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
        // 로고 + 앱 이름 영역
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

        // 하단 Loading + Progress Bar (스플래시 중에만)
        if (state.phase == LauncherContract.Phase.SPLASH) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 60.dp)
                    .alpha(loadingAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Loading",
                    color = Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(63.dp))
                        .background(SurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(12.dp)
                            .clip(RoundedCornerShape(63.dp))
                            .background(Primary)
                    )
                }
            }
        }

        // 로그인 버튼 영역 (로그인 UI에서만)
        if (showLoginUI) {
            val isLoggingIn = state.phase == LauncherContract.Phase.LOGGING_IN

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 104.dp)
                    .alpha(buttonsAlpha.value)
                    .offset { IntOffset(0, buttonsOffsetY.value.dp.roundToPx()) },
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                KakaoLoginButton(
                    isLoading = isLoggingIn,
                    onClick = { onEvent(LauncherContract.Event.KakaoLoginClicked) }
                )

                AppleLoginButton(
                    isLoading = isLoggingIn,
                    onClick = { onEvent(LauncherContract.Event.AppleLoginClicked) }
                )

                state.loginError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun KakaoLoginButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(71.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = KakaoYellow,
            disabledContainerColor = KakaoYellow.copy(alpha = 0.5f)
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = BlueGray95,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_kakao),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "카카오로 시작하기",
                    color = BlueGray95,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AppleLoginButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(71.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = BlueGray80,
            disabledContainerColor = BlueGray80.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, BlueGray70),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = BlueGrayWhite,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_apple),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Apple ID로 시작하기",
                    color = BlueGrayWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LauncherScreenSplashPreview() {
    RunnersHiTheme {
        LauncherScreen(
            state = LauncherContract.State(
                phase = LauncherContract.Phase.SPLASH,
                splashProgress = 0.5f
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun LauncherScreenLoginPreview() {
    RunnersHiTheme {
        LauncherScreen(
            state = LauncherContract.State(
                phase = LauncherContract.Phase.LOGIN
            )
        )
    }
}
