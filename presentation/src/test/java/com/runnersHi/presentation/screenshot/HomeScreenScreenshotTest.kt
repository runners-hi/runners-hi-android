package com.runnersHi.presentation.screenshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.runnersHi.presentation.model.RankChangeUiModel
import com.runnersHi.presentation.model.RankingItemUiModel
import com.runnersHi.presentation.model.UserUiModel
import com.runnersHi.presentation.ui.screen.home.HomeScreen
import com.runnersHi.presentation.ui.screen.home.HomeUiState
import com.runnersHi.presentation.ui.theme.RunnersHiTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.NORMAL,
        showSystemUi = false
    )

    private val mockRankings = listOf(
        RankingItemUiModel(1, UserUiModel("1", "김러너", null, 156.5, 42), 156.5, RankChangeUiModel.NONE),
        RankingItemUiModel(2, UserUiModel("2", "박조깅", null, 142.3, 38), 142.3, RankChangeUiModel.UP),
        RankingItemUiModel(3, UserUiModel("3", "이마라톤", null, 128.7, 35), 128.7, RankChangeUiModel.DOWN),
        RankingItemUiModel(4, UserUiModel("4", "최달리기", null, 115.2, 30), 115.2, RankChangeUiModel.UP),
        RankingItemUiModel(5, UserUiModel("5", "정스프린트", null, 98.4, 28), 98.4, RankChangeUiModel.NONE)
    )

    @Test
    fun homeScreen() {
        paparazzi.snapshot {
            RunnersHiTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        currentUser = UserUiModel("0", "테스트유저", null, 45.2, 12),
                        weeklyRanking = mockRankings,
                        myRank = 8,
                        myWeeklyDistance = 45.2
                    )
                )
            }
        }
    }
}
