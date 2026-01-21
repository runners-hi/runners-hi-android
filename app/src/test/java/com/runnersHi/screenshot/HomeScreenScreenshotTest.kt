package com.runnersHi.screenshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.runnersHi.model.RankChange
import com.runnersHi.model.RankingItem
import com.runnersHi.model.User
import com.runnersHi.ui.screen.home.HomeScreen
import com.runnersHi.ui.screen.home.HomeUiState
import com.runnersHi.ui.theme.RunnersHiTheme
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
        RankingItem(1, User("1", "김러너", null, 156.5, 42), 156.5, RankChange.NONE),
        RankingItem(2, User("2", "박조깅", null, 142.3, 38), 142.3, RankChange.UP),
        RankingItem(3, User("3", "이마라톤", null, 128.7, 35), 128.7, RankChange.DOWN),
        RankingItem(4, User("4", "최달리기", null, 115.2, 30), 115.2, RankChange.UP),
        RankingItem(5, User("5", "정스프린트", null, 98.4, 28), 98.4, RankChange.NONE)
    )

    @Test
    fun homeScreen() {
        paparazzi.snapshot {
            RunnersHiTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        currentUser = User("0", "테스트유저", null, 45.2, 12),
                        weeklyRanking = mockRankings,
                        myRank = 8,
                        myWeeklyDistance = 45.2
                    )
                )
            }
        }
    }
}
