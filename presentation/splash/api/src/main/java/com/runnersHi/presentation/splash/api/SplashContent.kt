package com.runnersHi.presentation.splash.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Splash UI 컴포넌트 타입
 * - impl 모듈에서 실제 구현
 * - launcher에서 조합하여 사용
 */
typealias SplashContent = @Composable (
    state: SplashContract.State,
    modifier: Modifier
) -> Unit
