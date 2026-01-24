package com.runnersHi.presentation.login

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.runnersHi.presentation.common.theme.Background
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme

private val KakaoYellow = Color(0xFFFEE500)
private val KakaoBrown = Color(0xFF3C1E1E)

@Composable
fun LoginScreen(
    onKakaoLoginClick: () -> Unit = {},
    onAppleLoginClick: () -> Unit = {},
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
            RunnersHiLogo()

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
                Button(
                    onClick = onKakaoLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KakaoYellow
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        KakaoIcon()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "카카오로 시작하기",
                            color = KakaoBrown,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Apple Login Button
                Button(
                    onClick = onAppleLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AppleIcon()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Apple ID로 시작하기",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RunnersHiLogo(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.size(80.dp, 60.dp)
    ) {
        val strokeWidth = 3.dp.toPx()
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Outer track (oval outline)
        drawRoundRect(
            color = Primary,
            topLeft = Offset(strokeWidth, strokeWidth + 8.dp.toPx()),
            size = Size(size.width - strokeWidth * 2, size.height - strokeWidth * 2 - 16.dp.toPx()),
            cornerRadius = CornerRadius(size.height / 2, size.height / 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Inner filled oval
        val innerPadding = 12.dp.toPx()
        drawRoundRect(
            color = Primary,
            topLeft = Offset(innerPadding, innerPadding + 4.dp.toPx()),
            size = Size(size.width - innerPadding * 2, size.height - innerPadding * 2 - 8.dp.toPx()),
            cornerRadius = CornerRadius(size.height / 2, size.height / 2)
        )
    }
}

@Composable
private fun KakaoIcon(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.size(20.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.width * 0.4f

        // Speech bubble circle
        drawCircle(
            color = KakaoBrown,
            radius = radius,
            center = Offset(centerX, centerY - 1.dp.toPx())
        )

        // Speech bubble tail
        val tailPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(centerX - 4.dp.toPx(), centerY + radius * 0.6f)
            lineTo(centerX - 2.dp.toPx(), centerY + radius + 3.dp.toPx())
            lineTo(centerX + 2.dp.toPx(), centerY + radius * 0.6f)
            close()
        }
        drawPath(tailPath, color = KakaoBrown)
    }
}

@Composable
private fun AppleIcon(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.size(20.dp)
    ) {
        val width = size.width
        val height = size.height

        // Apple shape (simplified)
        val applePath = androidx.compose.ui.graphics.Path().apply {
            // Main body
            moveTo(width * 0.5f, height * 0.2f)
            cubicTo(
                width * 0.7f, height * 0.2f,
                width * 0.9f, height * 0.4f,
                width * 0.9f, height * 0.6f
            )
            cubicTo(
                width * 0.9f, height * 0.85f,
                width * 0.65f, height,
                width * 0.5f, height
            )
            cubicTo(
                width * 0.35f, height,
                width * 0.1f, height * 0.85f,
                width * 0.1f, height * 0.6f
            )
            cubicTo(
                width * 0.1f, height * 0.4f,
                width * 0.3f, height * 0.2f,
                width * 0.5f, height * 0.2f
            )
            close()
        }
        drawPath(applePath, color = Color.Black)

        // Stem
        drawLine(
            color = Color.Black,
            start = Offset(width * 0.5f, height * 0.05f),
            end = Offset(width * 0.55f, height * 0.2f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LoginScreenPreview() {
    RunnersHiTheme {
        LoginScreen()
    }
}
