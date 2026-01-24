package com.runnersHi.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.usecase.LoginResult
import com.runnersHi.domain.auth.usecase.LoginWithSocialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithSocialUseCase: LoginWithSocialUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun loginWithKakao(kakaoAccessToken: String) {
        login(SocialLoginType.KAKAO, kakaoAccessToken)
    }

    fun loginWithApple(appleIdToken: String) {
        login(SocialLoginType.APPLE, appleIdToken)
    }

    private fun login(type: SocialLoginType, socialToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            loginWithSocialUseCase(type, socialToken)
                .onSuccess { result ->
                    _uiState.value = when (result) {
                        is LoginResult.Success -> LoginUiState.Success
                        is LoginResult.NewUser -> LoginUiState.NewUser(result.userId)
                        is LoginResult.Error -> LoginUiState.Error(result.message)
                    }
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Error(
                        error.message ?: "로그인에 실패했습니다."
                    )
                }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
