package com.runnersHi.navigation

import androidx.lifecycle.ViewModel
import com.runnersHi.core.network.SessionEvents
import com.runnersHi.domain.auth.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/** 갱신 토큰까지 만료되면 기기 데이터를 지운다. */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    val expired: Flow<Unit> = SessionEvents.expired.onEach { logoutUseCase() }
}
