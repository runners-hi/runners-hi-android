package com.runnersHi.presentation.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray95
import com.runnersHi.presentation.common.theme.BlueGrayWhite
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import com.runnersHi.presentation.login.api.LoginContent
import com.runnersHi.presentation.login.api.LoginContract
import com.runnersHi.presentation.login.impl.R

// Figma Colors
private val KakaoYellow = Color(0xFFFEE500)

/**
 * Login Content 구현체
 * - 하단 로그인 버튼 영역만 담당
 * - 로고는 Launcher에서 관리
 */
val LoginContentImpl: LoginContent = { state, onEvent, modifier ->
    LoginButtons(state = state, onEvent = onEvent, modifier = modifier)
}

/**
 * 로그인 버튼 영역
 */
@Composable
private fun LoginButtons(
    state: LoginContract.State,
    onEvent: (LoginContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 카카오 로그인 버튼
        KakaoLoginButton(
            isLoading = state.isLoading,
            onClick = { onEvent(LoginContract.Event.KakaoLoginClicked) }
        )

        // Apple 로그인 버튼
        AppleLoginButton(
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
private fun LoginButtonsPreview() {
    RunnersHiTheme {
        LoginButtons(
            state = LoginContract.State(),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun LoginButtonsLoadingPreview() {
    RunnersHiTheme {
        LoginButtons(
            state = LoginContract.State(isLoading = true),
            onEvent = {}
        )
    }
}
