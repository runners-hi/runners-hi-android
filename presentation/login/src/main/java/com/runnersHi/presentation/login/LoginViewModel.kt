package com.runnersHi.presentation.login

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.usecase.LoginResult
import com.runnersHi.domain.auth.usecase.LoginWithSocialUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithSocialUseCase: LoginWithSocialUseCase
) : MviViewModel<LoginContract.State, LoginContract.Event, LoginContract.Effect>(
    initialState = LoginContract.State()
) {

    override fun handleEvent(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.KakaoLoginClicked -> {
                sendEffect(LoginContract.Effect.RequestKakaoLogin)
            }
            is LoginContract.Event.AppleLoginClicked -> {
                sendEffect(LoginContract.Effect.RequestAppleLogin)
            }
            is LoginContract.Event.KakaoTokenReceived -> {
                loginWithSocial(SocialLoginType.KAKAO, event.token)
            }
            is LoginContract.Event.AppleTokenReceived -> {
                loginWithSocial(SocialLoginType.APPLE, event.token)
            }
            is LoginContract.Event.LoginFailed -> {
                updateState { copy(isLoading = false, errorMessage = event.message) }
            }
            is LoginContract.Event.ErrorDismissed -> {
                updateState { copy(errorMessage = null) }
            }
        }
    }

    private fun loginWithSocial(type: SocialLoginType, token: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            loginWithSocialUseCase(type, token)
                .onSuccess { result ->
                    updateState { copy(isLoading = false) }
                    when (result) {
                        is LoginResult.Success -> {
                            sendEffect(LoginContract.Effect.NavigateToHome)
                        }
                        is LoginResult.NewUser -> {
                            sendEffect(LoginContract.Effect.NavigateToTermsAgreement)
                        }
                        is LoginResult.Error -> {
                            updateState { copy(errorMessage = result.message) }
                        }
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "로그인에 실패했습니다."
                        )
                    }
                }
        }
    }
}
