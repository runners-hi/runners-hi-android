package com.runnersHi.presentation.mypage

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.user.usecase.SendFeedbackUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val sendFeedback: SendFeedbackUseCase
) : MviViewModel<FeedbackContract.State, FeedbackContract.Event, FeedbackContract.Effect>(
    FeedbackContract.State()
) {

    override fun handleEvent(event: FeedbackContract.Event) {
        when (event) {
            is FeedbackContract.Event.ContentChanged -> updateState {
                copy(content = event.content.take(SendFeedbackUseCase.MAX_LENGTH))
            }
            FeedbackContract.Event.SendClicked -> send()
        }
    }

    private fun send() {
        if (!currentState.canSend) return
        updateState { copy(isSending = true) }
        viewModelScope.launch {
            sendFeedback(currentState.content)
                .onSuccess {
                    sendEffect(FeedbackContract.Effect.ShowToast("보냈어요. 고마워요!"))
                    sendEffect(FeedbackContract.Effect.Close)
                }
                .onFailure { error ->
                    updateState { copy(isSending = false) }
                    sendEffect(FeedbackContract.Effect.ShowToast(error.message ?: "보내지 못했어요"))
                }
        }
    }
}
