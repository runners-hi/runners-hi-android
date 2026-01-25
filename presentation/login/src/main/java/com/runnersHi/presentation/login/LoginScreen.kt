package com.runnersHi.presentation.login

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.Background
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme

private val KakaoYellow = Color(0xFFFEE500)
private val KakaoBrown = Color(0xFF3C1E1E)

/**
 * Login Route: 상태 수집 및 이펙트 처리
 */
@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onKakaoLoginRequest: () -> Unit,
    onAppleLoginRequest: () -> Unit
) {
    val state by viewModel.collectState()

    viewModel.collectEffect { effect ->
        when (effect) {
            is LoginContract.Effect.RequestKakaoLogin -> onKakaoLoginRequest()
            is LoginContract.Effect.RequestAppleLogin -> onAppleLoginRequest()
            is LoginContract.Effect.NavigateToHome -> onNavigateToHome()
            is LoginContract.Effect.NavigateToOnboarding -> onNavigateToOnboarding()
            is LoginContract.Effect.ShowToast -> {
                // TODO: Show toast
            }
        }
    }

    LoginScreen(
        state = state,
        onEvent = viewModel::sendEvent
    )
}

/**
 * Login Screen: 순수 UI
 */
@Composable
fun LoginScreen(
    state: LoginContract.State,
    onEvent: (LoginContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_logo_runnershi),
                contentDescription = "RunnersHi Logo",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App Name
            Text(
                text = "Runners HI",
                color = Primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            // Social Login Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Kakao Login Button
                SocialLoginButton(
                    text = "카카오로 시작하기",
                    backgroundColor = KakaoYellow,
                    contentColor = KakaoBrown,
                    isLoading = state.isLoading,
                    onClick = { onEvent(LoginContract.Event.KakaoLoginClicked) }
                )

                // Apple Login Button
                SocialLoginButton(
                    text = "Apple로 시작하기",
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    isLoading = state.isLoading,
                    onClick = { onEvent(LoginContract.Event.AppleLoginClicked) }
                )

                // Error Message
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
}

@Composable
private fun SocialLoginButton(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f)
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
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
