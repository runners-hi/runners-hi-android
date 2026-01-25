# MVI 아키텍처 스킬

## 개요
모든 presentation 모듈의 화면은 MVI (Model-View-Intent) 패턴을 적용합니다.

---

## 베이스 컴포넌트

위치: `presentation/common/src/main/java/com/runnersHi/presentation/common/mvi/`

### UiContract.kt
```kotlin
interface UiState      // 화면 상태
interface UiEvent      // 사용자 액션
interface UiEffect     // 일회성 이벤트 (네비게이션, 토스트 등)
```

### MviViewModel.kt
```kotlin
abstract class MviViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    initialState: S
) : ViewModel() {
    val state: StateFlow<S>           // 상태 구독
    val effect: Flow<F>               // 이펙트 구독

    fun sendEvent(event: E)           // 이벤트 발송
    protected fun updateState(reducer: S.() -> S)  // 상태 업데이트
    protected fun sendEffect(effect: F)            // 이펙트 발송
    protected abstract fun handleEvent(event: E)   // 이벤트 처리 (구현 필수)
}
```

### MviExtensions.kt
```kotlin
@Composable
fun <S, E, F> MviViewModel<S, E, F>.collectState(): State<S>

@Composable
fun <S, E, F> MviViewModel<S, E, F>.collectEffect(onEffect: suspend (F) -> Unit)
```

---

## 화면 구현 규칙

### 1. Contract 정의
파일명: `<Screen>Contract.kt`

```kotlin
class SplashContract {
    data class State(
        val isLoading: Boolean = true,
        val progress: Float = 0f,
        // ... 화면에 필요한 모든 상태
    ) : UiState

    sealed interface Event : UiEvent {
        data object StartLoading : Event
        data class OnButtonClick(val id: String) : Event
        // ... 사용자 액션
    }

    sealed interface Effect : UiEffect {
        data object NavigateToHome : Effect
        data class ShowToast(val message: String) : Effect
        // ... 일회성 이벤트
    }
}
```

### 2. ViewModel 구현
파일명: `<Screen>ViewModel.kt`

```kotlin
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val useCase: SomeUseCase
) : MviViewModel<SplashContract.State, SplashContract.Event, SplashContract.Effect>(
    initialState = SplashContract.State()
) {
    override fun handleEvent(event: SplashContract.Event) {
        when (event) {
            is SplashContract.Event.StartLoading -> loadData()
            // ...
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            // ... 비즈니스 로직
            sendEffect(SplashContract.Effect.NavigateToHome)
        }
    }
}
```

### 3. Screen 구현
파일명: `<Screen>Screen.kt`

```kotlin
/**
 * Route: 상태 수집 및 이펙트 처리 컨테이너
 */
@Composable
fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.collectState()

    viewModel.collectEffect { effect ->
        when (effect) {
            is SplashContract.Effect.NavigateToHome -> onNavigateToHome()
            is SplashContract.Effect.ShowToast -> { /* 토스트 표시 */ }
        }
    }

    SplashScreen(
        state = state,
        onEvent = viewModel::sendEvent
    )
}

/**
 * Screen: 순수 UI (상태 기반, 프리뷰 가능)
 */
@Composable
fun SplashScreen(
    state: SplashContract.State,
    onEvent: (SplashContract.Event) -> Unit
) {
    // UI 구현
    Button(onClick = { onEvent(SplashContract.Event.StartLoading) }) {
        Text("시작")
    }
}
```

---

## 파일 구조

```
presentation/<module>/src/main/java/com/runnersHi/presentation/<module>/
├── <Screen>Contract.kt      # State, Event, Effect 정의
├── <Screen>ViewModel.kt     # MviViewModel 구현
├── <Screen>Screen.kt        # Route + Screen Composable
└── component/               # 화면 전용 컴포넌트
    └── <Component>.kt
```

---

## 체크리스트

새 화면 생성 시:
- [ ] Contract 생성 (State, Event, Effect)
- [ ] ViewModel 생성 (MviViewModel 상속)
- [ ] Route 생성 (상태 수집, 이펙트 처리)
- [ ] Screen 생성 (순수 UI, 프리뷰 가능)
- [ ] Hilt @HiltViewModel 어노테이션
- [ ] 네비게이션에 Route 연결

---

## 주의사항

1. **State는 불변**: `copy()`로만 업데이트
2. **Effect는 일회성**: 네비게이션, 토스트 등 한 번만 처리되어야 하는 것
3. **Screen은 순수 함수**: 상태만 받아서 UI 렌더링, 프리뷰 가능해야 함
4. **Route에서 Context 접근**: LocalContext 등 플랫폼 의존성은 Route에서 처리
