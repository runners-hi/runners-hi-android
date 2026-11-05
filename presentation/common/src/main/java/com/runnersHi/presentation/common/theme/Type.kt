package com.runnersHi.presentation.common.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.R

/** Figma 전 화면이 쓰는 글꼴. SIL OFL 1.1 (docs/licenses/Pretendard-OFL.txt) */
val Pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold)
)

private fun TextStyle.pretendard() = copy(fontFamily = Pretendard, letterSpacing = 0.sp)

private val base = Typography()

val Typography = Typography(
    displayLarge = base.displayLarge.pretendard(),
    displayMedium = base.displayMedium.pretendard(),
    displaySmall = base.displaySmall.pretendard(),
    headlineLarge = base.headlineLarge.pretendard(),
    headlineMedium = base.headlineMedium.pretendard(),
    headlineSmall = base.headlineSmall.pretendard(),
    titleLarge = base.titleLarge.pretendard(),
    titleMedium = base.titleMedium.pretendard(),
    titleSmall = base.titleSmall.pretendard(),
    // Text의 기본 스타일. 줄 높이를 비워 두어 글자 크기대로 잡히게 한다.
    bodyLarge = base.bodyLarge.pretendard().copy(lineHeight = TextUnit.Unspecified),
    bodyMedium = base.bodyMedium.pretendard(),
    bodySmall = base.bodySmall.pretendard(),
    labelLarge = base.labelLarge.pretendard(),
    labelMedium = base.labelMedium.pretendard(),
    labelSmall = base.labelSmall.pretendard()
)
