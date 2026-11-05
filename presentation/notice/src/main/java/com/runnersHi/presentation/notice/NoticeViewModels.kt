package com.runnersHi.presentation.notice

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.notice.usecase.GetNoticeDetailUseCase
import com.runnersHi.domain.notice.usecase.GetNoticesUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeListViewModel @Inject constructor(
    private val getNotices: GetNoticesUseCase
) : MviViewModel<NoticeListContract.State, NoticeListContract.Event, NoticeListContract.Effect>(
    NoticeListContract.State()
) {

    init {
        loadFirstPage()
    }

    override fun handleEvent(event: NoticeListContract.Event) {
        when (event) {
            NoticeListContract.Event.Retry -> loadFirstPage()
            NoticeListContract.Event.LoadMore -> loadMore()
        }
    }

    private fun loadFirstPage() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            getNotices(null)
                .onSuccess { page ->
                    updateState {
                        copy(isLoading = false, notices = page.notices, nextCursor = page.nextCursor)
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(isLoading = false, errorMessage = error.message ?: "공지사항을 불러오지 못했어요")
                    }
                }
        }
    }

    private fun loadMore() {
        val cursor = state.value.nextCursor ?: return
        if (state.value.isLoadingMore) return
        viewModelScope.launch {
            updateState { copy(isLoadingMore = true) }
            getNotices(cursor)
                .onSuccess { page ->
                    updateState {
                        copy(
                            isLoadingMore = false,
                            notices = notices + page.notices,
                            nextCursor = page.nextCursor
                        )
                    }
                }
                // 다음 페이지 실패는 목록을 유지하고, 다시 스크롤하면 재시도한다.
                .onFailure { updateState { copy(isLoadingMore = false) } }
        }
    }
}

@HiltViewModel
class NoticeDetailViewModel @Inject constructor(
    private val getNoticeDetail: GetNoticeDetailUseCase
) : MviViewModel<NoticeDetailContract.State, NoticeDetailContract.Event, NoticeDetailContract.Effect>(
    NoticeDetailContract.State()
) {

    override fun handleEvent(event: NoticeDetailContract.Event) {
        when (event) {
            is NoticeDetailContract.Event.Load -> load(event.id)
        }
    }

    private fun load(id: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            getNoticeDetail(id)
                .onSuccess { notice -> updateState { copy(isLoading = false, notice = notice) } }
                .onFailure { error ->
                    updateState {
                        copy(isLoading = false, errorMessage = error.message ?: "공지사항을 불러오지 못했어요")
                    }
                }
        }
    }
}
