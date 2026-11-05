package com.runnersHi.presentation.mission

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.mission.model.MissionAccent
import com.runnersHi.domain.mission.model.MissionEvent
import com.runnersHi.domain.mission.usecase.GetMissionBoardUseCase
import com.runnersHi.presentation.common.component.MissionItemModel
import com.runnersHi.presentation.common.mvi.MviViewModel
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.PrimaryAmber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val getMissionBoardUseCase: GetMissionBoardUseCase
) : MviViewModel<MissionContract.State, MissionContract.Event, MissionContract.Effect>(
    MissionContract.State()
) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    init {
        load()
    }

    override fun handleEvent(event: MissionContract.Event) {
        when (event) {
            MissionContract.Event.Retry -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            getMissionBoardUseCase()
                .onSuccess { board ->
                    updateState {
                        copy(
                            isLoading = false,
                            sections = board.sections.map { section ->
                                MissionSectionUiModel(
                                    id = section.id,
                                    title = section.title,
                                    bannerTitle = section.seasonBanner?.title,
                                    bannerPeriod = section.seasonBanner?.let {
                                        "${it.startDate.format(dateFormatter)} - ${it.endDate.format(dateFormatter)}"
                                    },
                                    items = section.events.map { it.toItem() }
                                )
                            }
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "미션을 불러오는데 실패했습니다."
                        )
                    }
                }
        }
    }

    private fun MissionEvent.toItem() = MissionItemModel(
        id = id,
        title = title,
        subtitle = subtitle,
        iconName = iconName,
        isCompleted = isCompleted,
        imageUrl = imageUrl,
        accentColor = when (accent) {
            MissionAccent.PRIMARY -> Primary
            MissionAccent.AMBER -> PrimaryAmber
        }
    )
}
