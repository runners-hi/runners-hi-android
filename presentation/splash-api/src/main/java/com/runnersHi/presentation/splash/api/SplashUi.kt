package com.runnersHi.presentation.splash.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Splash UI 컴포넌트
 * - Launcher 모듈에서 사용
 * - impl 모듈에서 구현
 */
typealias SplashContent = @Composable (
    state: SplashContract.State,
    modifier: Modifier
) -> Unit
