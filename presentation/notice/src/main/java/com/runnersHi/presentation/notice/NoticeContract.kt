package com.runnersHi.presentation.notice

import com.runnersHi.domain.notice.model.Notice
import com.runnersHi.domain.notice.model.NoticeDetail
import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object NoticeListContract {

    data class State(
        val isLoading: Boolean = true,
        val isLoadingMore: Boolean = false,
        val notices: List<Notice> = emptyList(),
        val nextCursor: String? = null,
        val errorMessage: String? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data object Retry : Event
        data object LoadMore : Event
    }

    sealed interface Effect : UiEffect
}

object NoticeDetailContract {

    data class State(
        val isLoading: Boolean = true,
        val notice: NoticeDetail? = null,
        val errorMessage: String? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data class Load(val id: String) : Event
    }

    sealed interface Effect : UiEffect
}
