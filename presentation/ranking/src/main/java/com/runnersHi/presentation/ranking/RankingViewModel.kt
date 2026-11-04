package com.runnersHi.presentation.ranking

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.ranking.usecase.GetRankingBoardUseCase
import com.runnersHi.domain.region.usecase.GetSelectedRegionUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val getRankingBoardUseCase: GetRankingBoardUseCase,
    private val getSelectedRegionUseCase: GetSelectedRegionUseCase
) : MviViewModel<RankingContract.State, RankingContract.Event, RankingContract.Effect>(
    RankingContract.State()
) {

    private var boardRegionName = ""

    init {
        load()
        // 마이페이지에서 지역을 바꾸면 제목에 바로 반영한다.
        viewModelScope.launch {
            getSelectedRegionUseCase().collect { region ->
                updateState { copy(regionName = region?.name ?: boardRegionName) }
            }
        }
    }

    override fun handleEvent(event: RankingContract.Event) {
        when (event) {
            RankingContract.Event.Retry -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            getRankingBoardUseCase()
                .onSuccess { board ->
                    boardRegionName = board.regionName
                    val selected = getSelectedRegionUseCase().first()
                    updateState {
                        copy(
                            isLoading = false,
                            regionName = selected?.name ?: board.regionName,
                            topEntries = board.topEntries,
                            myEntry = board.myEntry.takeUnless { board.isMyEntryInTop }
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "랭킹을 불러오는데 실패했습니다."
                        )
                    }
                }
        }
    }
}
