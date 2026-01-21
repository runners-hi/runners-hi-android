package com.runnersHi.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import com.runnersHi.presentation.common.theme.Background
import com.runnersHi.presentation.common.theme.OnBackground
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import com.runnersHi.presentation.common.theme.Surface

@Composable
fun SplashScreen(
    progress: Float = 0f,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RunnersHi",
                color = Primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "달리기의 즐거움을 함께",
                color = OnBackground.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 48.dp)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Primary,
                trackColor = Surface,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                color = OnBackground.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SplashScreenPreview() {
    RunnersHiTheme {
        SplashScreen(progress = 0.5f)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SplashScreenLoadingPreview() {
    RunnersHiTheme {
        SplashScreen(progress = 0f)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SplashScreenCompletePreview() {
    RunnersHiTheme {
        SplashScreen(progress = 1f)
    }
}
