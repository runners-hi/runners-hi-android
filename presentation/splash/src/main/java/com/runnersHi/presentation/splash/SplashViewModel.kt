package com.runnersHi.presentation.splash

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.usecase.CheckLoginStatusUseCase
import com.runnersHi.domain.auth.usecase.LoginCheckResult
import com.runnersHi.domain.auth.usecase.LoginResult
import com.runnersHi.domain.auth.usecase.LoginWithSocialUseCase
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
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
    private val loginWithSocialUseCase: LoginWithSocialUseCase
) : MviViewModel<SplashContract.State, SplashContract.Event, SplashContract.Effect>(
    initialState = SplashContract.State()
) {

    override fun handleEvent(event: SplashContract.Event) {
        when (event) {
            // 스플래시 이벤트
            is SplashContract.Event.CheckAppStatus -> checkAppStatus(event.currentVersion)
            is SplashContract.Event.RetryClicked -> {
                updateState { copy(errorMessage = null, isLoading = true, progress = 0f, phase = SplashContract.Phase.LOADING) }
            }
            is SplashContract.Event.ForceUpdateConfirmed -> {
                sendEffect(SplashContract.Effect.OpenPlayStore)
            }

            // 로그인 이벤트
            is SplashContract.Event.KakaoLoginClicked -> {
                sendEffect(SplashContract.Effect.RequestKakaoLogin)
            }
            is SplashContract.Event.AppleLoginClicked -> {
                sendEffect(SplashContract.Effect.RequestAppleLogin)
            }
            is SplashContract.Event.KakaoTokenReceived -> {
                loginWithSocial(SocialLoginType.KAKAO, event.token)
            }
            is SplashContract.Event.AppleTokenReceived -> {
                loginWithSocial(SocialLoginType.APPLE, event.token)
            }
            is SplashContract.Event.LoginFailed -> {
                updateState { copy(phase = SplashContract.Phase.LOGIN, errorMessage = event.message) }
            }
        }
    }

    private fun checkAppStatus(currentVersion: String) {
        viewModelScope.launch {
            // 시작: 0%
            updateState { copy(progress = 0f, isLoading = true, phase = SplashContract.Phase.LOADING) }

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
                    // 로그인 화면으로 "전환" (같은 화면 내에서)
                    updateState { copy(phase = SplashContract.Phase.LOGIN, isLoading = false) }
                }
                LoginCheckResult.LoggedIn -> {
                    updateState { copy(progress = 1f) }
                    delay(300) // 애니메이션 완료 대기
                    sendEffect(SplashContract.Effect.NavigateToHome)
                }
            }
        }
    }

    private fun loginWithSocial(type: SocialLoginType, token: String) {
        viewModelScope.launch {
            updateState { copy(phase = SplashContract.Phase.LOGGING_IN, errorMessage = null) }

            loginWithSocialUseCase(type, token)
                .onSuccess { result ->
                    when (result) {
                        is LoginResult.Success -> {
                            sendEffect(SplashContract.Effect.NavigateToHome)
                        }
                        is LoginResult.NewUser -> {
                            sendEffect(SplashContract.Effect.NavigateToTermsAgreement)
                        }
                        is LoginResult.Error -> {
                            updateState { copy(phase = SplashContract.Phase.LOGIN, errorMessage = result.message) }
                        }
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            phase = SplashContract.Phase.LOGIN,
                            errorMessage = error.message ?: "로그인에 실패했습니다."
                        )
                    }
                }
        }
    }
}
