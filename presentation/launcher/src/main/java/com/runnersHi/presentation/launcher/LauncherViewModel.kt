package com.runnersHi.presentation.launcher

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.usecase.CheckLoginStatusUseCase
import com.runnersHi.domain.auth.usecase.LoginCheckResult
import com.runnersHi.domain.auth.usecase.LoginResult
import com.runnersHi.domain.auth.usecase.LoginWithSocialUseCase
import com.runnersHi.domain.splash.usecase.CheckAppVersionUseCase
import com.runnersHi.domain.splash.usecase.VersionCheckResult
import com.runnersHi.presentation.common.mvi.MviViewModel
import com.runnersHi.presentation.launcher.api.LauncherContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val checkAppVersionUseCase: CheckAppVersionUseCase,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
    private val loginWithSocialUseCase: LoginWithSocialUseCase
) : MviViewModel<LauncherContract.State, LauncherContract.Event, LauncherContract.Effect>(
    initialState = LauncherContract.State()
) {

    override fun handleEvent(event: LauncherContract.Event) {
        when (event) {
            // Splash 이벤트
            is LauncherContract.Event.CheckAppStatus -> checkAppStatus(event.currentVersion)
            is LauncherContract.Event.ForceUpdateConfirmed -> {
                sendEffect(LauncherContract.Effect.OpenPlayStore)
            }

            // Login 이벤트
            is LauncherContract.Event.KakaoLoginClicked -> {
                sendEffect(LauncherContract.Effect.RequestKakaoLogin)
            }
            is LauncherContract.Event.AppleLoginClicked -> {
                sendEffect(LauncherContract.Effect.RequestAppleLogin)
            }
            is LauncherContract.Event.KakaoTokenReceived -> {
                loginWithSocial(SocialLoginType.KAKAO, event.token)
            }
            is LauncherContract.Event.AppleTokenReceived -> {
                loginWithSocial(SocialLoginType.APPLE, event.token)
            }
            is LauncherContract.Event.LoginFailed -> {
                updateState { copy(phase = LauncherContract.Phase.LOGIN, loginError = event.message) }
            }
        }
    }

    private fun checkAppStatus(currentVersion: String) {
        viewModelScope.launch {
            // 시작: 0%
            updateState { copy(splashProgress = 0f, phase = LauncherContract.Phase.SPLASH) }

            // 1. 버전 체크 (0% -> 50%)
            updateState { copy(splashProgress = 0.2f) }

            when (val versionResult = checkAppVersionUseCase(currentVersion)) {
                is VersionCheckResult.NeedsUpdate -> {
                    updateState {
                        copy(
                            splashProgress = 1f,
                            forceUpdateInfo = LauncherContract.ForceUpdateInfo(
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
                            splashProgress = 1f,
                            loginError = versionResult.message
                        )
                    }
                    return@launch
                }
                is VersionCheckResult.Error -> {
                    updateState {
                        copy(
                            splashProgress = 1f,
                            loginError = versionResult.exception.message ?: "알 수 없는 오류가 발생했습니다."
                        )
                    }
                    return@launch
                }
                VersionCheckResult.UpToDate -> {
                    updateState { copy(splashProgress = 0.5f) }
                }
            }

            // 2. 로그인 상태 확인 (50% -> 100%)
            updateState { copy(splashProgress = 0.7f) }

            when (checkLoginStatusUseCase()) {
                LoginCheckResult.NotLoggedIn,
                LoginCheckResult.TokenRefreshFailed -> {
                    updateState { copy(splashProgress = 1f) }
                    delay(300) // 애니메이션 완료 대기
                    // 로그인 화면으로 전환 (같은 화면 내에서)
                    updateState { copy(phase = LauncherContract.Phase.LOGIN) }
                }
                LoginCheckResult.LoggedIn -> {
                    updateState { copy(splashProgress = 1f) }
                    delay(300) // 애니메이션 완료 대기
                    sendEffect(LauncherContract.Effect.NavigateToHome)
                }
            }
        }
    }

    private fun loginWithSocial(type: SocialLoginType, token: String) {
        viewModelScope.launch {
            updateState { copy(phase = LauncherContract.Phase.LOGGING_IN, loginError = null) }

            loginWithSocialUseCase(type, token)
                .onSuccess { result ->
                    when (result) {
                        is LoginResult.Success -> {
                            sendEffect(LauncherContract.Effect.NavigateToHome)
                        }
                        is LoginResult.NewUser -> {
                            sendEffect(LauncherContract.Effect.NavigateToTermsAgreement)
                        }
                        is LoginResult.Error -> {
                            updateState { copy(phase = LauncherContract.Phase.LOGIN, loginError = result.message) }
                        }
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            phase = LauncherContract.Phase.LOGIN,
                            loginError = error.message ?: "로그인에 실패했습니다."
                        )
                    }
                }
        }
    }
}
