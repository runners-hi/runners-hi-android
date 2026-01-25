package com.runnersHi.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme

// Figma 색상
private val BackgroundColor = Color(0xFF17191C)  // BlueGray/90
private val ProgressTrackColor = Color(0xFF2E3238)  // BlueGray/80

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
            .background(BackgroundColor)
    ) {
        // 중앙 로고
        RunnersHiLogo(
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
                    .background(ProgressTrackColor)
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

@Composable
private fun RunnersHiLogo(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val strokeWidth = width * 0.035f

        // 로고 영역 (상하 16% 패딩)
        val logoTop = height * 0.16f
        val logoBottom = height * 0.84f
        val logoHeight = logoBottom - logoTop

        // 바깥쪽 트랙 (스타디움/캡슐 형태)
        val outerCornerRadius = logoHeight * 0.5f
        drawRoundRect(
            color = Primary,
            topLeft = Offset(0f, logoTop),
            size = Size(width, logoHeight),
            cornerRadius = CornerRadius(outerCornerRadius, outerCornerRadius),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // 중간 트랙
        val middlePadding = width * 0.1f
        val middleTopPadding = logoHeight * 0.15f
        val middleHeight = logoHeight - middleTopPadding * 2
        val middleCornerRadius = middleHeight * 0.5f
        drawRoundRect(
            color = Primary,
            topLeft = Offset(middlePadding, logoTop + middleTopPadding),
            size = Size(width - middlePadding * 2, middleHeight),
            cornerRadius = CornerRadius(middleCornerRadius, middleCornerRadius),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // 안쪽 채워진 캡슐
        val innerPadding = width * 0.22f
        val innerTopPadding = logoHeight * 0.32f
        val innerHeight = logoHeight - innerTopPadding * 2
        val innerCornerRadius = innerHeight * 0.5f
        drawRoundRect(
            color = Primary,
            topLeft = Offset(innerPadding, logoTop + innerTopPadding),
            size = Size(width - innerPadding * 2, innerHeight),
            cornerRadius = CornerRadius(innerCornerRadius, innerCornerRadius)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    RunnersHiTheme {
        SplashScreen(progress = 0.27f)
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenLoadingPreview() {
    RunnersHiTheme {
        SplashScreen(progress = 0f)
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenCompletePreview() {
    RunnersHiTheme {
        SplashScreen(progress = 1f)
    }
}
