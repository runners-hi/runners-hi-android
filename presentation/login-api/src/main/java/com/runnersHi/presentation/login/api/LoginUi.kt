package com.runnersHi.presentation.login.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Login UI 컴포넌트
 * - Launcher 모듈에서 사용
 * - impl 모듈에서 구현
 */
typealias LoginContent = @Composable (
    state: LoginContract.State,
    onEvent: (LoginContract.Event) -> Unit,
    modifier: Modifier
) -> Unit
