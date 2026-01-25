package com.runnersHi.presentation.launcher.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Launcher UI 컴포넌트 타입
 * - impl 모듈에서 실제 구현
 * - Splash와 Login을 조합한 통합 화면
 */
typealias LauncherContent = @Composable (
    state: LauncherContract.State,
    onEvent: (LauncherContract.Event) -> Unit,
    modifier: Modifier
) -> Unit
