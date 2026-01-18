package com.runnersHi.presentation.screenshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.runnersHi.presentation.model.RankChangeUiModel
import com.runnersHi.presentation.model.RankingItemUiModel
import com.runnersHi.presentation.model.UserUiModel
import com.runnersHi.presentation.ui.component.Avatar
import com.runnersHi.presentation.ui.component.AvatarSize
import com.runnersHi.presentation.ui.component.RankBadge
import com.runnersHi.presentation.ui.component.UserRankingItem
import com.runnersHi.presentation.ui.navigation.RunnersHiBottomNavigation
import com.runnersHi.presentation.ui.theme.RunnersHiTheme
import org.junit.Rule
import org.junit.Test

class ComponentScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.NORMAL,
        showSystemUi = false
    )

    @Test
    fun avatar_small() {
        paparazzi.snapshot {
            RunnersHiTheme {
                Avatar(
                    imageUrl = null,
                    name = "Runner",
                    size = AvatarSize.Small
                )
            }
        }
    }

    @Test
    fun avatar_medium() {
        paparazzi.snapshot {
            RunnersHiTheme {
                Avatar(
                    imageUrl = null,
                    name = "Runner",
                    size = AvatarSize.Medium
                )
            }
        }
    }

    @Test
    fun avatar_large_with_border() {
        paparazzi.snapshot {
            RunnersHiTheme {
                Avatar(
                    imageUrl = null,
                    name = "Runner",
                    size = AvatarSize.Large,
                    showBorder = true
                )
            }
        }
    }

    @Test
    fun rankBadge_first() {
        paparazzi.snapshot {
            RunnersHiTheme {
                RankBadge(rank = 1)
            }
        }
    }

    @Test
    fun rankBadge_second() {
        paparazzi.snapshot {
            RunnersHiTheme {
                RankBadge(rank = 2)
            }
        }
    }

    @Test
    fun rankBadge_third() {
        paparazzi.snapshot {
            RunnersHiTheme {
                RankBadge(rank = 3)
            }
        }
    }

    @Test
    fun rankBadge_other() {
        paparazzi.snapshot {
            RunnersHiTheme {
                RankBadge(rank = 10)
            }
        }
    }

    @Test
    fun userRankingItem() {
        paparazzi.snapshot {
            RunnersHiTheme {
                UserRankingItem(
                    rankingItem = RankingItemUiModel(
                        rank = 1,
                        user = UserUiModel(
                            id = "1",
                            name = "김러너",
                            profileImageUrl = null,
                            totalDistance = 156.5,
                            totalRuns = 42
                        ),
                        score = 156.5,
                        change = RankChangeUiModel.UP
                    )
                )
            }
        }
    }

    @Test
    fun bottomNavigation_home() {
        paparazzi.snapshot {
            RunnersHiTheme {
                RunnersHiBottomNavigation(
                    currentRoute = "home",
                    onNavigate = {}
                )
            }
        }
    }

    @Test
    fun bottomNavigation_run() {
        paparazzi.snapshot {
            RunnersHiTheme {
                RunnersHiBottomNavigation(
                    currentRoute = "run",
                    onNavigate = {}
                )
            }
        }
    }
}
