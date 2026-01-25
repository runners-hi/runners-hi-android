package com.runnersHi.presentation.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * MVI 패턴을 구현하는 기본 ViewModel
 *
 * @param S UiState - 화면 상태
 * @param E UiEvent - 사용자 이벤트/인텐트
 * @param F UiEffect - 일회성 사이드 이펙트
 *
 * 사용법:
 * ```
 * @HiltViewModel
 * class SplashViewModel @Inject constructor(
 *     private val useCase: SomeUseCase
 * ) : MviViewModel<SplashContract.State, SplashContract.Event, SplashContract.Effect>(
 *     initialState = SplashContract.State()
 * ) {
 *     override fun handleEvent(event: SplashContract.Event) {
 *         when (event) {
 *             is SplashContract.Event.OnStart -> checkAppStatus()
 *         }
 *     }
 * }
 * ```
 */
abstract class MviViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _event = MutableSharedFlow<E>()

    private val _effect = Channel<F>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * 현재 상태값
     */
    protected val currentState: S
        get() = _state.value

    init {
        viewModelScope.launch {
            _event.collect { event ->
                handleEvent(event)
            }
        }
    }

    /**
     * 이벤트 처리 - 하위 클래스에서 구현
     */
    protected abstract fun handleEvent(event: E)

    /**
     * UI에서 이벤트 전송
     */
    fun sendEvent(event: E) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    /**
     * 상태 업데이트
     */
    protected fun updateState(reducer: S.() -> S) {
        _state.value = currentState.reducer()
    }

    /**
     * 상태 직접 설정
     */
    protected fun setState(newState: S) {
        _state.value = newState
    }

    /**
     * 사이드 이펙트 발생
     */
    protected fun sendEffect(effect: F) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
