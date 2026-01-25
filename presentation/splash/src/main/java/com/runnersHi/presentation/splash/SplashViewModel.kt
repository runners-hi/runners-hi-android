package com.runnersHi.presentation.splash

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.auth.usecase.CheckLoginStatusUseCase
import com.runnersHi.domain.auth.usecase.LoginCheckResult
import com.runnersHi.domain.splash.usecase.CheckAppVersionUseCase
import com.runnersHi.domain.splash.usecase.VersionCheckResult
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkAppVersionUseCase: CheckAppVersionUseCase,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase
) : MviViewModel<SplashContract.State, SplashContract.Event, SplashContract.Effect>(
    initialState = SplashContract.State()
) {

    override fun handleEvent(event: SplashContract.Event) {
        when (event) {
            is SplashContract.Event.CheckAppStatus -> checkAppStatus(event.currentVersion)
            is SplashContract.Event.RetryClicked -> {
                // 에러 상태 초기화 후 재시도
                updateState { copy(errorMessage = null, isLoading = true, progress = 0f) }
            }
            is SplashContract.Event.ForceUpdateConfirmed -> {
                sendEffect(SplashContract.Effect.OpenPlayStore)
            }
        }
    }

    private fun checkAppStatus(currentVersion: String) {
        viewModelScope.launch {
            // 시작: 0%
            updateState { copy(progress = 0f, isLoading = true) }

            // 1. 버전 체크 (0% -> 50%)
            updateState { copy(progress = 0.2f) }

            when (val versionResult = checkAppVersionUseCase(currentVersion)) {
                is VersionCheckResult.NeedsUpdate -> {
                    updateState {
                        copy(
                            isLoading = false,
                            progress = 1f,
                            forceUpdate = SplashContract.ForceUpdateInfo(
                                currentVersion = currentVersion,
                                minVersion = versionResult.minVersion
                            )
                        )
                    }
                    return@launch
                }
                is VersionCheckResult.Maintenance -> {
                    updateState {
                        copy(
                            isLoading = false,
                            progress = 1f,
                            errorMessage = versionResult.message
                        )
                    }
                    return@launch
                }
                is VersionCheckResult.Error -> {
                    updateState {
                        copy(
                            isLoading = false,
                            progress = 1f,
                            errorMessage = versionResult.exception.message ?: "알 수 없는 오류가 발생했습니다."
                        )
                    }
                    return@launch
                }
                VersionCheckResult.UpToDate -> {
                    // 버전 체크 통과: 50%
                    updateState { copy(progress = 0.5f) }
                }
            }

            // 2. 로그인 상태 확인 (50% -> 100%)
            updateState { copy(progress = 0.7f) }

            when (checkLoginStatusUseCase()) {
                LoginCheckResult.NotLoggedIn,
                LoginCheckResult.TokenRefreshFailed -> {
                    updateState { copy(progress = 1f) }
                    delay(300) // 애니메이션 완료 대기
                    sendEffect(SplashContract.Effect.NavigateToLogin)
                }
                LoginCheckResult.LoggedIn -> {
                    updateState { copy(progress = 1f) }
                    delay(300) // 애니메이션 완료 대기
                    sendEffect(SplashContract.Effect.NavigateToHome)
                }
            }
        }
    }
}
