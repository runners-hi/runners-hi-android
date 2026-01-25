package com.runnersHi.presentation.common.theme

import androidx.compose.ui.graphics.Color

// ============================================
// Figma Semantic Colors
// https://www.figma.com/design/eaE2AUdQNsllPXAKuHeJrp/RunnersHi?node-id=2-98
// ============================================

// Blue Gray Palette
val BlueGray95 = Color(0xFF0C0D0E)
val BlueGray90 = Color(0xFF17191C)
val BlueGray80 = Color(0xFF2E3238)
val BlueGray70 = Color(0xFF454B54)
val BlueGray60 = Color(0xFF5C6470)
val BlueGray50 = Color(0xFF75808B)
val BlueGray40 = Color(0xFF8F97A3)
val BlueGray30 = Color(0xFFABB1BA)
val BlueGray20 = Color(0xFFC7CBD1)
val BlueGray10 = Color(0xFFE3E5E8)
val BlueGray5 = Color(0xFFF1F2F4)
val BlueGrayWhite = Color(0xFFFFFFFF)

// Primary Palette
val Primary = Color(0xFF00EEFF)         // 메인 컬러 (Cyan)
val PrimaryYellow = Color(0xFFF5FF66)   // 노랑 컬러 (Gold tier 등)
val PrimaryRed = Color(0xFFFF6363)      // 빨강 컬러
val PrimarySecondary = Color(0xFF255860)

// Deprecated alias
@Deprecated("Use Primary instead", ReplaceWith("Primary"))
val PrimaryCyan = Primary               // 기존 PrimaryCyan 사용처 호환

// ============================================
// Semantic Aliases (용도별 컬러)
// ============================================

// Background
val Background = BlueGray95          // 앱 기본 배경
val BackgroundElevated = BlueGray90  // 상승된 배경 (카드, 바텀시트 등)
val Surface = BlueGray90             // 표면 컬러
val SurfaceVariant = BlueGray80      // 표면 변형 (프로그레스 바 트랙 등)

// Text / On-colors
val OnBackground = BlueGrayWhite     // 배경 위 텍스트 (기본)
val OnBackgroundSecondary = BlueGray40  // 배경 위 텍스트 (보조)
val OnBackgroundTertiary = BlueGray60   // 배경 위 텍스트 (3차)
val OnPrimary = BlueGray95           // Primary 위 텍스트
val OnSurface = BlueGrayWhite        // Surface 위 텍스트

// Divider & Border
val Divider = BlueGray80             // 구분선
val Border = BlueGray70              // 테두리

// Disabled
val Disabled = BlueGray70            // 비활성 상태
val DisabledContent = BlueGray50     // 비활성 콘텐츠

// ============================================
// Status Colors
// ============================================
val Success = Color(0xFF4CAF50)
val Error = Color(0xFFE53935)
val Warning = Color(0xFFFFB300)

// ============================================
// Rank Colors
// ============================================
val RankGold = Color(0xFFFFD700)
val RankSilver = Color(0xFFC0C0C0)
val RankBronze = Color(0xFFCD7F32)

// ============================================
// Legacy (Deprecated - 점진적 제거 예정)
// ============================================
@Deprecated("Use Primary instead", ReplaceWith("Primary"))
val PrimaryVariant = Color(0xFF00B8D4)

@Deprecated("Use PrimarySecondary instead", ReplaceWith("PrimarySecondary"))
val Secondary = Color(0xFFFFD54F)

@Deprecated("Use BackgroundElevated instead", ReplaceWith("BackgroundElevated"))
val CardBackground = Color(0xFF252525)

@Deprecated("Use Primary instead")
val GradientStart = Color(0xFF00E5FF)

@Deprecated("Use PrimarySecondary instead")
val GradientEnd = Color(0xFF00B8D4)
