package com.runnersHi.presentation.region

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.region.usecase.SearchRegionsUseCase
import com.runnersHi.domain.region.usecase.SelectRegionUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class RegionSelectionViewModel @Inject constructor(
    private val searchRegionsUseCase: SearchRegionsUseCase,
    private val selectRegionUseCase: SelectRegionUseCase
) : MviViewModel<RegionSelectionContract.State, RegionSelectionContract.Event, RegionSelectionContract.Effect>(
    initialState = RegionSelectionContract.State()
) {

    private val searchQueryFlow = MutableStateFlow("")

    init {
        // Debounce 300ms 적용
        viewModelScope.launch {
            searchQueryFlow
                .debounce(300)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .collect { query ->
                    searchRegions(query)
                }
        }
    }

    override fun handleEvent(event: RegionSelectionContract.Event) {
        when (event) {
            is RegionSelectionContract.Event.SearchQueryChanged -> onSearchQueryChanged(event.query)
            is RegionSelectionContract.Event.RegionSelected -> selectRegion(event.region)
            is RegionSelectionContract.Event.ClearSearchQuery -> clearSearchQuery()
            is RegionSelectionContract.Event.RetrySearch -> retrySearch()
            is RegionSelectionContract.Event.BackClicked -> {
                sendEffect(RegionSelectionContract.Effect.NavigateBack)
            }
            is RegionSelectionContract.Event.ErrorDismissed -> {
                updateState { copy(errorMessage = null, isNetworkError = false) }
            }
        }
    }

    private fun onSearchQueryChanged(query: String) {
        updateState { copy(searchQuery = query) }
        searchQueryFlow.value = query

        // 검색어가 비어있으면 결과 초기화
        if (query.isBlank()) {
            updateState {
                copy(
                    searchResults = emptyList(),
                    hasSearched = false,
                    isSearching = false,
                    isNetworkError = false
                )
            }
        }
    }

    private fun clearSearchQuery() {
        updateState {
            copy(
                searchQuery = "",
                searchResults = emptyList(),
                hasSearched = false,
                isSearching = false,
                isNetworkError = false
            )
        }
        searchQueryFlow.value = ""
    }

    private fun retrySearch() {
        val query = state.value.searchQuery
        if (query.isNotBlank()) {
            searchRegions(query)
        }
    }

    private fun searchRegions(query: String) {
        viewModelScope.launch {
            updateState { copy(isSearching = true, isNetworkError = false) }

            searchRegionsUseCase(query)
                .onSuccess { regions ->
                    val uiModels = regions.map { region ->
                        // 검색어 하이라이팅 범위 계산
                        val highlightRange = findHighlightRange(region.name, query)
                        RegionUiModel(
                            id = region.id,
                            name = region.name,
                            highlightRange = highlightRange
                        )
                    }
                    updateState {
                        copy(
                            isSearching = false,
                            searchResults = uiModels,
                            hasSearched = true
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isSearching = false,
                            hasSearched = true,
                            isNetworkError = true,
                            errorMessage = error.message ?: "검색 중 오류가 발생했습니다."
                        )
                    }
                }
        }
    }

    private fun findHighlightRange(text: String, query: String): IntRange? {
        if (query.isBlank()) return null
        val startIndex = text.indexOf(query, ignoreCase = true)
        return if (startIndex >= 0) {
            startIndex until (startIndex + query.length)
        } else {
            null
        }
    }

    private fun selectRegion(region: RegionUiModel) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            selectRegionUseCase(region.id, region.name)
                .onSuccess {
                    updateState { copy(isLoading = false) }
                    sendEffect(RegionSelectionContract.Effect.NavigateToMain)
                }
                .onFailure { error ->
                    updateState { copy(isLoading = false) }
                    sendEffect(
                        RegionSelectionContract.Effect.ShowToast(
                            error.message ?: "지역 선택에 실패했습니다."
                        )
                    )
                }
        }
    }
}
