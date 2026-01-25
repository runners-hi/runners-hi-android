package com.runnersHi.presentation.common.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * MVI ViewModel의 State를 Lifecycle-aware하게 수집
 *
 * 사용법:
 * ```
 * @Composable
 * fun SplashScreen(viewModel: SplashViewModel) {
 *     val state by viewModel.collectState()
 *     // state 사용
 * }
 * ```
 */
@Composable
fun <S : UiState, E : UiEvent, F : UiEffect> MviViewModel<S, E, F>.collectState() =
    state.collectAsStateWithLifecycle()

/**
 * MVI ViewModel의 Effect를 수집하고 처리
 *
 * 사용법:
 * ```
 * @Composable
 * fun SplashScreen(viewModel: SplashViewModel) {
 *     viewModel.collectEffect { effect ->
 *         when (effect) {
 *             is SplashContract.Effect.NavigateToHome -> navController.navigate("home")
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun <S : UiState, E : UiEvent, F : UiEffect> MviViewModel<S, E, F>.collectEffect(
    onEffect: suspend (F) -> Unit
) {
    LaunchedEffect(Unit) {
        effect.collect { effect ->
            onEffect(effect)
        }
    }
}

/**
 * Flow의 Effect를 수집하고 처리하는 범용 확장 함수
 */
@Composable
fun <T> Flow<T>.collectEffect(onEffect: suspend (T) -> Unit) {
    LaunchedEffect(Unit) {
        collect { effect ->
            onEffect(effect)
        }
    }
}
