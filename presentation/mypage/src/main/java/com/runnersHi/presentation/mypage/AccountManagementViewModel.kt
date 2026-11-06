package com.runnersHi.presentation.mypage

import com.runnersHi.core.common.analytics.AnalyticsEvent
import com.runnersHi.core.common.analytics.AnalyticsHub
import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.auth.usecase.LogoutUseCase
import com.runnersHi.domain.auth.usecase.WithdrawUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountManagementViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val withdrawUseCase: WithdrawUseCase
) : MviViewModel<AccountManagementContract.State, AccountManagementContract.Event, AccountManagementContract.Effect>(
    AccountManagementContract.State()
) {

    override fun handleEvent(event: AccountManagementContract.Event) {
        when (event) {
            AccountManagementContract.Event.LogoutClicked -> logout()
            AccountManagementContract.Event.WithdrawClicked ->
                updateState { copy(showWithdrawConfirm = true) }
            AccountManagementContract.Event.WithdrawDismissed ->
                updateState { copy(showWithdrawConfirm = false) }
            AccountManagementContract.Event.WithdrawConfirmed -> withdraw()
        }
    }

    private fun logout() {
        if (state.value.isLoggingOut) return
        viewModelScope.launch {
            updateState { copy(isLoggingOut = true) }
            logoutUseCase()
            AnalyticsHub.log(AnalyticsEvent.LOGOUT)
            AnalyticsHub.setUserId(null)
            sendEffect(AccountManagementContract.Effect.LoggedOut)
        }
    }

    private fun withdraw() {
        if (state.value.isWithdrawing) return
        viewModelScope.launch {
            updateState { copy(isWithdrawing = true, showWithdrawConfirm = false) }
            withdrawUseCase()
                .onSuccess {
                    AnalyticsHub.log(AnalyticsEvent.WITHDRAW)
                    AnalyticsHub.setUserId(null)
                    sendEffect(AccountManagementContract.Effect.Withdrawn)
                }
                .onFailure { error ->
                    updateState { copy(isWithdrawing = false) }
                    sendEffect(
                        AccountManagementContract.Effect.ShowToast(error.message ?: "탈퇴하지 못했어요")
                    )
                }
        }
    }
}
