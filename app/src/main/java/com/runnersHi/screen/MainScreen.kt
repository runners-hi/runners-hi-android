package com.runnersHi.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.runnersHi.presentation.common.model.RankChangeUiModel
import com.runnersHi.presentation.common.model.RankingItemUiModel
import com.runnersHi.presentation.common.model.UserUiModel
import com.runnersHi.presentation.common.navigation.RunnersHiBottomNavigation
import com.runnersHi.presentation.common.theme.Background
import com.runnersHi.presentation.home.HomeScreen
import com.runnersHi.presentation.home.HomeUiState

/**
 * 메인 화면 (바텀 네비게이션 포함)
 */
@Composable
fun MainScreen() {
    var currentRoute by remember { mutableStateOf("home") }

    // TODO: ViewModel에서 데이터 가져오기
    val homeUiState = rememberMockHomeUiState()

    Scaffold(
        containerColor = Background,
        bottomBar = {
            RunnersHiBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route }
            )
        }
    ) { innerPadding ->
        when (currentRoute) {
            "home" -> HomeScreen(
                uiState = homeUiState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            // TODO: 다른 탭 화면 구현
            else -> HomeScreen(
                uiState = homeUiState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

/**
 * Mock 데이터 (개발용)
 * TODO: 실제 구현 시 제거
 */
@Composable
private fun rememberMockHomeUiState(): HomeUiState {
    val mockRankings = remember {
        listOf(
            RankingItemUiModel(1, UserUiModel("1", "김러너", null, 156.5, 42), 156.5, RankChangeUiModel.NONE),
            RankingItemUiModel(2, UserUiModel("2", "박조깅", null, 142.3, 38), 142.3, RankChangeUiModel.UP),
            RankingItemUiModel(3, UserUiModel("3", "이마라톤", null, 128.7, 35), 128.7, RankChangeUiModel.DOWN),
            RankingItemUiModel(4, UserUiModel("4", "최달리기", null, 115.2, 30), 115.2, RankChangeUiModel.UP),
            RankingItemUiModel(5, UserUiModel("5", "정스프린트", null, 98.4, 28), 98.4, RankChangeUiModel.NONE)
        )
    }

    return remember {
        HomeUiState(
            currentUser = UserUiModel("0", "테스트유저", null, 45.2, 12),
            weeklyRanking = mockRankings,
            myRank = 8,
            myWeeklyDistance = 45.2
        )
    }
}
