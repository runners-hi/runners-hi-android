package com.runnersHi.presentation.region

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

class RegionSelectionContract {

    data class State(
        val isLoading: Boolean = false,           // 지역 선택 API 로딩
        val isSearching: Boolean = false,         // 검색 API 로딩
        val searchQuery: String = "",
        val searchResults: List<RegionUiModel> = emptyList(),
        val hasSearched: Boolean = false,         // 검색 수행 여부 (결과 없음 구분용)
        val errorMessage: String? = null,
        val isNetworkError: Boolean = false       // 재시도 버튼 표시용
    ) : UiState

    sealed interface Event : UiEvent {
        data class SearchQueryChanged(val query: String) : Event
        data class RegionSelected(val region: RegionUiModel) : Event
        data object ClearSearchQuery : Event      // 검색어 초기화 (X 버튼)
        data object RetrySearch : Event           // 재시도 버튼
        data object BackClicked : Event
        data object ErrorDismissed : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToMain : Effect
        data object NavigateBack : Effect
        data class ShowToast(val message: String) : Effect
    }
}

/**
 * 지역 UI 모델
 */
data class RegionUiModel(
    val id: String,
    val name: String,
    val highlightRange: IntRange? = null  // 검색어 하이라이팅 범위
)
