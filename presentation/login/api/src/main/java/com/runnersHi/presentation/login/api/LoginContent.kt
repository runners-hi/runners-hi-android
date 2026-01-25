package com.runnersHi.presentation.login.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Login UI 컴포넌트 타입
 * - impl 모듈에서 실제 구현
 * - launcher에서 조합하여 사용
 */
typealias LoginContent = @Composable (
    state: LoginContract.State,
    onEvent: (LoginContract.Event) -> Unit,
    modifier: Modifier
) -> Unit
