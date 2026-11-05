package com.runnersHi.presentation.notification

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.notification.usecase.GetNotificationsUseCase
import com.runnersHi.domain.notification.usecase.GetPushEnabledUseCase
import com.runnersHi.domain.notification.usecase.MarkNotificationsReadUseCase
import com.runnersHi.domain.notification.usecase.SetPushEnabledUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotifications: GetNotificationsUseCase,
    private val markNotificationsRead: MarkNotificationsReadUseCase,
    private val getPushEnabled: GetPushEnabledUseCase,
    private val setPushEnabled: SetPushEnabledUseCase
) : MviViewModel<NotificationContract.State, NotificationContract.Event, NotificationContract.Effect>(
    NotificationContract.State()
) {

    init {
        load()
    }

    override fun handleEvent(event: NotificationContract.Event) {
        when (event) {
            NotificationContract.Event.Retry -> load()
            is NotificationContract.Event.PushToggled -> togglePush(event.enabled)
        }
    }

    private fun load() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            getPushEnabled().onSuccess { enabled -> updateState { copy(pushEnabled = enabled) } }
            getNotifications()
                .onSuccess { items ->
                    updateState { copy(isLoading = false, notifications = items) }
                    // 화면에는 안 읽은 표시를 남기고, 서버에는 읽음으로 저장한다.
                    if (items.any { !it.isRead }) markNotificationsRead()
                }
                .onFailure { error ->
                    updateState {
                        copy(isLoading = false, errorMessage = error.message ?: "알림을 불러오지 못했어요")
                    }
                }
        }
    }

    private fun togglePush(enabled: Boolean) {
        val previous = currentState.pushEnabled
        updateState { copy(pushEnabled = enabled) }
        viewModelScope.launch {
            setPushEnabled(enabled).onFailure { error ->
                updateState { copy(pushEnabled = previous) }
                sendEffect(
                    NotificationContract.Effect.ShowToast(error.message ?: "설정을 바꾸지 못했어요")
                )
            }
        }
    }
}
