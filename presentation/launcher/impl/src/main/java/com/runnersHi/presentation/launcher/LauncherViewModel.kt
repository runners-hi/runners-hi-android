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
import com.runnersHi.presentation.login.api.LoginContract
import com.runnersHi.presentation.splash.api.SplashContract
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

            // Login 이벤트 (래핑된 이벤트 처리)
            is LauncherContract.Event.LoginEvent -> handleLoginEvent(event.event)
        }
    }

    private fun handleLoginEvent(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.KakaoLoginClicked -> {
                updateState {
                    copy(
                        phase = LauncherContract.Phase.LOGGING_IN,
                        loginState = loginState.copy(loadingType = LoginContract.LoadingType.KAKAO)
                    )
                }
                sendEffect(LauncherContract.Effect.RequestKakaoLogin)
            }
            is LoginContract.Event.AppleLoginClicked -> {
                updateState {
                    copy(
                        phase = LauncherContract.Phase.LOGGING_IN,
                        loginState = loginState.copy(loadingType = LoginContract.LoadingType.APPLE)
                    )
                }
                sendEffect(LauncherContract.Effect.RequestAppleLogin)
            }
            is LoginContract.Event.KakaoTokenReceived -> {
                loginWithSocial(SocialLoginType.KAKAO, event.token)
            }
            is LoginContract.Event.AppleTokenReceived -> {
                loginWithSocial(SocialLoginType.APPLE, event.token)
            }
            is LoginContract.Event.LoginFailed -> {
                updateState {
                    copy(
                        phase = LauncherContract.Phase.LOGIN,
                        loginState = loginState.copy(
                            loadingType = LoginContract.LoadingType.NONE,
                            errorMessage = event.message
                        )
                    )
                }
            }
            is LoginContract.Event.ErrorDismissed -> {
                updateState {
                    copy(loginState = loginState.copy(errorMessage = null))
                }
            }
        }
    }

    private fun checkAppStatus(currentVersion: String) {
        viewModelScope.launch {
            // 시작: 0%
            updateState {
                copy(
                    phase = LauncherContract.Phase.SPLASH,
                    splashState = splashState.copy(progress = 0f)
                )
            }

            // 1. 버전 체크 (0% -> 50%)
            updateState {
                copy(splashState = splashState.copy(progress = 0.2f))
            }

            when (val versionResult = checkAppVersionUseCase(currentVersion)) {
                is VersionCheckResult.NeedsUpdate -> {
                    updateState {
                        copy(
                            splashState = splashState.copy(
                                progress = 1f,
                                forceUpdate = SplashContract.ForceUpdateInfo(
                                    currentVersion = currentVersion,
                                    minVersion = versionResult.minVersion
                                )
                            )
                        )
                    }
                    return@launch
                }
                is VersionCheckResult.Maintenance -> {
                    updateState {
                        copy(
                            splashState = splashState.copy(
                                progress = 1f,
                                errorMessage = versionResult.message
                            )
                        )
                    }
                    return@launch
                }
                is VersionCheckResult.Error -> {
                    updateState {
                        copy(
                            splashState = splashState.copy(
                                progress = 1f,
                                errorMessage = versionResult.exception.message ?: "알 수 없는 오류가 발생했습니다."
                            )
                        )
                    }
                    return@launch
                }
                VersionCheckResult.UpToDate -> {
                    updateState {
                        copy(splashState = splashState.copy(progress = 0.5f))
                    }
                }
            }

            // 2. 로그인 상태 확인 (50% -> 100%)
            updateState {
                copy(splashState = splashState.copy(progress = 0.7f))
            }

            when (checkLoginStatusUseCase()) {
                LoginCheckResult.NotLoggedIn,
                LoginCheckResult.TokenRefreshFailed -> {
                    updateState {
                        copy(splashState = splashState.copy(progress = 1f))
                    }
                    delay(300) // 애니메이션 완료 대기
                    // 로그인 화면으로 전환 (같은 화면 내에서)
                    updateState { copy(phase = LauncherContract.Phase.LOGIN) }
                }
                LoginCheckResult.LoggedIn -> {
                    updateState {
                        copy(splashState = splashState.copy(progress = 1f))
                    }
                    delay(300) // 애니메이션 완료 대기
                    sendEffect(LauncherContract.Effect.NavigateToHome)
                }
            }
        }
    }

    private fun loginWithSocial(type: SocialLoginType, token: String) {
        viewModelScope.launch {
            // 로딩 상태는 이미 버튼 클릭 시 설정됨
            updateState {
                copy(loginState = loginState.copy(errorMessage = null))
            }

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
                            updateState {
                                copy(
                                    phase = LauncherContract.Phase.LOGIN,
                                    loginState = loginState.copy(
                                        loadingType = LoginContract.LoadingType.NONE,
                                        errorMessage = result.message
                                    )
                                )
                            }
                        }
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            phase = LauncherContract.Phase.LOGIN,
                            loginState = loginState.copy(
                                loadingType = LoginContract.LoadingType.NONE,
                                errorMessage = error.message ?: "로그인에 실패했습니다."
                            )
                        )
                    }
                }
        }
    }
}
