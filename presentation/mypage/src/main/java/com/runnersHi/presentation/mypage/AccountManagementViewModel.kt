package com.runnersHi.presentation.mypage

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.auth.usecase.LogoutUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountManagementViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : MviViewModel<AccountManagementContract.State, AccountManagementContract.Event, AccountManagementContract.Effect>(
    AccountManagementContract.State()
) {

    override fun handleEvent(event: AccountManagementContract.Event) {
        when (event) {
            AccountManagementContract.Event.LogoutClicked -> logout()
        }
    }

    private fun logout() {
        if (state.value.isLoggingOut) return
        viewModelScope.launch {
            updateState { copy(isLoggingOut = true) }
            logoutUseCase()
            sendEffect(AccountManagementContract.Effect.LoggedOut)
        }
    }
}
