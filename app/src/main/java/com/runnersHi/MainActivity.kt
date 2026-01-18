package com.runnersHi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.runnersHi.model.RankChange
import com.runnersHi.model.RankingItem
import com.runnersHi.model.User
import com.runnersHi.ui.navigation.RunnersHiBottomNavigation
import com.runnersHi.ui.screen.home.HomeScreen
import com.runnersHi.ui.screen.home.HomeUiState
import com.runnersHi.ui.theme.Background
import com.runnersHi.ui.theme.RunnersHiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RunnersHiTheme {
                RunnersHiApp()
            }
        }
    }
}

@Composable
fun RunnersHiApp() {
    var currentRoute by remember { mutableStateOf("home") }

    val mockRankings = remember {
        listOf(
            RankingItem(1, User("1", "김러너", null, 156.5, 42), 156.5, RankChange.NONE),
            RankingItem(2, User("2", "박조깅", null, 142.3, 38), 142.3, RankChange.UP),
            RankingItem(3, User("3", "이마라톤", null, 128.7, 35), 128.7, RankChange.DOWN),
            RankingItem(4, User("4", "최달리기", null, 115.2, 30), 115.2, RankChange.UP),
            RankingItem(5, User("5", "정스프린트", null, 98.4, 28), 98.4, RankChange.NONE)
        )
    }

    val homeUiState = remember {
        HomeUiState(
            currentUser = User("0", "테스트유저", null, 45.2, 12),
            weeklyRanking = mockRankings,
            myRank = 8,
            myWeeklyDistance = 45.2
        )
    }

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
            else -> HomeScreen(
                uiState = homeUiState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RunnersHiAppPreview() {
    RunnersHiTheme {
        RunnersHiApp()
    }
}
