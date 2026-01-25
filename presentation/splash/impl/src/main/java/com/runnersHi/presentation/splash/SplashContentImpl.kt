package com.runnersHi.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import com.runnersHi.presentation.common.theme.SurfaceVariant
import com.runnersHi.presentation.splash.api.SplashContent
import com.runnersHi.presentation.splash.api.SplashContract
import com.runnersHi.presentation.splash.impl.R

/**
 * Splash Content 구현체
 */
val SplashContentImpl: SplashContent = { state, modifier ->
    SplashScreen(state = state, modifier = modifier)
}

@Composable
private fun SplashScreen(
    state: SplashContract.State,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueGray90)
    ) {
        // 중앙 로고
        Image(
            painter = painterResource(id = R.drawable.ic_logo_runnershi),
            contentDescription = "RunnersHi Logo",
            modifier = Modifier
                .align(Alignment.Center)
                .size(80.dp)
        )

        // 하단 Loading + Progress Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Loading",
                color = Primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Custom Progress Bar
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
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    RunnersHiTheme {
        SplashScreen(state = SplashContract.State(progress = 0.5f))
    }
}
