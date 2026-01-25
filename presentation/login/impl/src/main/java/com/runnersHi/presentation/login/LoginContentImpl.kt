package com.runnersHi.presentation.login

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.BlueGray95
import com.runnersHi.presentation.common.theme.BlueGrayWhite
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import com.runnersHi.presentation.login.api.LoginContent
import com.runnersHi.presentation.login.api.LoginContract
import com.runnersHi.presentation.login.impl.R

// Figma Colors
private val KakaoYellow = Color(0xFFFEE500)

/**
 * Login Content 구현체
 */
val LoginContentImpl: LoginContent = { state, onEvent, modifier ->
    LoginScreen(state = state, onEvent = onEvent, modifier = modifier)
}

/**
 * Login Screen: 순수 UI with entry animation
 */
@Composable
private fun LoginScreen(
    state: LoginContract.State,
    onEvent: (LoginContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp

    // Animation states
    val logoOffsetY = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val buttonsAlpha = remember { Animatable(0f) }
    val buttonsOffsetY = remember { Animatable(100f) }

    // Entry animation
    LaunchedEffect(Unit) {
        val targetOffset = -(screenHeightDp * 0.12f)
        logoOffsetY.animateTo(
            targetValue = targetOffset,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )

        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 200)
        )

        buttonsAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300)
        )
        buttonsOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueGray90)
    ) {
        // 로고 영역 (애니메이션 적용)
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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Runners HI",
                color = Primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(textAlpha.value)
            )
        }

        // 소셜 로그인 버튼 영역
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
                isLoading = state.isLoading,
                onClick = { onEvent(LoginContract.Event.KakaoLoginClicked) }
            )

            AppleLoginButton(
                isLoading = state.isLoading,
                onClick = { onEvent(LoginContract.Event.AppleLoginClicked) }
            )

            state.errorMessage?.let { error ->
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

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun LoginScreenPreview() {
    RunnersHiTheme {
        LoginScreen(
            state = LoginContract.State(),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun LoginScreenLoadingPreview() {
    RunnersHiTheme {
        LoginScreen(
            state = LoginContract.State(isLoading = true),
            onEvent = {}
        )
    }
}
