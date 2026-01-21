package com.runnersHi.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.usecase.CheckAppVersionUseCase
import com.runnersHi.domain.usecase.CheckLoginStatusUseCase
import com.runnersHi.domain.usecase.LoginCheckResult
import com.runnersHi.domain.usecase.VersionCheckResult
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun checkAppStatus(currentVersion: String) {
        viewModelScope.launch {
            _uiState.value = SplashUiState.Loading

            // 1. 버전 체크
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
                    // 버전 체크 통과, 로그인 상태 확인으로 진행
                }
            }

            // 2. 로그인 상태 확인
            when (checkLoginStatusUseCase()) {
                LoginCheckResult.NotLoggedIn,
                LoginCheckResult.TokenRefreshFailed -> {
                    _uiState.value = SplashUiState.NavigateToLogin
                }
                LoginCheckResult.LoggedIn -> {
                    _uiState.value = SplashUiState.NavigateToHome
                }
            }
        }
    }
}
