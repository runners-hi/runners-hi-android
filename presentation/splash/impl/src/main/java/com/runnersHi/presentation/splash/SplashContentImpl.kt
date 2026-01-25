package com.runnersHi.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import com.runnersHi.presentation.common.theme.SurfaceVariant
import com.runnersHi.presentation.splash.api.SplashContent
import com.runnersHi.presentation.splash.api.SplashContract

/**
 * Splash Content 구현체
 * - 하단 프로그레스바 영역만 담당
 * - 로고는 Launcher에서 관리
 */
val SplashContentImpl: SplashContent = { state, modifier ->
    SplashProgressBar(state = state, modifier = modifier)
}

/**
 * 스플래시 하단 프로그레스바 영역
 */
@Composable
private fun SplashProgressBar(
    state: SplashContract.State,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Loading",
            color = Primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Bar
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

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun SplashProgressBarPreview() {
    RunnersHiTheme {
        SplashProgressBar(state = SplashContract.State(progress = 0.5f))
    }
}
