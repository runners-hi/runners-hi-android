package com.runnersHi.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.auth.usecase.CheckLoginStatusUseCase
import com.runnersHi.domain.auth.usecase.LoginCheckResult
import com.runnersHi.domain.splash.usecase.CheckAppVersionUseCase
import com.runnersHi.domain.splash.usecase.VersionCheckResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkAppVersionUseCase: CheckAppVersionUseCase,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading(0f))
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun checkAppStatus(currentVersion: String) {
        viewModelScope.launch {
            // 시작: 0%
            _uiState.value = SplashUiState.Loading(0f)

            // 1. 버전 체크 (0% -> 50%)
            _uiState.value = SplashUiState.Loading(0.2f)

            when (val versionResult = checkAppVersionUseCase(currentVersion)) {
                is VersionCheckResult.NeedsUpdate -> {
                    _uiState.value = SplashUiState.ForceUpdate(
                        currentVersion = currentVersion,
                        minVersion = versionResult.minVersion
                    )
                    return@launch
                }
                is VersionCheckResult.Maintenance -> {
                    _uiState.value = SplashUiState.Error(versionResult.message)
                    return@launch
                }
                is VersionCheckResult.Error -> {
                    _uiState.value = SplashUiState.Error(
                        versionResult.exception.message ?: "알 수 없는 오류가 발생했습니다."
                    )
                    return@launch
                }
                VersionCheckResult.UpToDate -> {
                    // 버전 체크 통과: 50%
                    _uiState.value = SplashUiState.Loading(0.5f)
                }
            }

            // 2. 로그인 상태 확인 (50% -> 100%)
            _uiState.value = SplashUiState.Loading(0.7f)

            when (checkLoginStatusUseCase()) {
                LoginCheckResult.NotLoggedIn,
                LoginCheckResult.TokenRefreshFailed -> {
                    _uiState.value = SplashUiState.Loading(1f)
                    delay(300) // 애니메이션 완료 대기
                    _uiState.value = SplashUiState.NavigateToLogin()
                }
                LoginCheckResult.LoggedIn -> {
                    _uiState.value = SplashUiState.Loading(1f)
                    delay(300) // 애니메이션 완료 대기
                    _uiState.value = SplashUiState.NavigateToHome()
                }
            }
        }
    }
}
