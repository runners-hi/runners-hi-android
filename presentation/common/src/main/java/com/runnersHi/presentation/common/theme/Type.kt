package com.runnersHi.presentation.common.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.R

/** Figma 전 화면이 쓰는 글꼴. SIL OFL 1.1 (docs/licenses/Pretendard-OFL.txt) */
val Pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold)
)

/** 아이폰 UIFont.systemFont 기본 줄 높이 ÷ 글자 크기 */
const val SF_LINE_HEIGHT = 1.193359f

// 커닝을 끈다. Pretendard는 "1."·"Ru" 같은 짝을 크게 좁혀 아이폰 SF보다 글줄이 짧아진다.
// 앱 글줄 12개 실측 폭 차이 합(SF 대비): 커닝 켬 29.2pt → 끔 18.8pt. 예) "2026.11.01 - 2026.11.30" 14pt: SF 152.37 / 켬 144.86 / 끔 150.04
// 글자 위치를 정수 픽셀로 맞추지 않는다(TextMotion.Animated = 힌팅 끔·서브픽셀 위치). 정적 모드는 한글 한 자 폭 41.49px(16pt, 3배 화면)을 41px로 내려
// "약관에 모두 동의합니다." 같은 줄이 아이폰보다 1.7pt 짧아졌다.
private fun TextStyle.pretendard() = copy(fontFamily = Pretendard, letterSpacing = 0.sp, fontFeatureSettings = "'kern' 0", textMotion = TextMotion.Animated)

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
    // Text의 기본 스타일. 줄 높이는 아이폰 SF 기본 줄 높이(글자 크기 × 1.1934, 예: 14pt → 16.71)와 같게 둔다.
    // Pretendard 글꼴 지표도 같은 비율(2444/2048)이지만, 비워 두면 안드로이드가 올림·내림을 정수 픽셀로 해서 줄마다 0.1~0.3pt씩 달라진다.
    bodyLarge = base.bodyLarge.pretendard().copy(lineHeight = SF_LINE_HEIGHT.em),
    bodyMedium = base.bodyMedium.pretendard(),
    bodySmall = base.bodySmall.pretendard(),
    labelLarge = base.labelLarge.pretendard(),
    labelMedium = base.labelMedium.pretendard(),
    labelSmall = base.labelSmall.pretendard()
)
