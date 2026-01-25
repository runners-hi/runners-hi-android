package com.runnersHi.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runnersHi.presentation.common.component.Avatar
import com.runnersHi.presentation.common.component.AvatarSize
import com.runnersHi.presentation.common.component.UserRankingItem
import com.runnersHi.presentation.common.model.RankChangeUiModel
import com.runnersHi.presentation.common.model.RankingItemUiModel
import com.runnersHi.presentation.common.model.UserUiModel
import com.runnersHi.presentation.common.theme.Background
import com.runnersHi.presentation.common.theme.BackgroundElevated
import com.runnersHi.presentation.common.theme.OnBackground
import com.runnersHi.presentation.common.theme.OnBackgroundSecondary
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme

@Immutable
data class HomeUiState(
    val currentUser: UserUiModel?,
    val weeklyRanking: List<RankingItemUiModel>,
    val myRank: Int?,
    val myWeeklyDistance: Double
)

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onUserClick: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HomeHeader(
                userName = uiState.currentUser?.name ?: "러너",
                userImageUrl = uiState.currentUser?.profileImageUrl
            )
        }

        item {
            MyStatsCard(
                rank = uiState.myRank,
                weeklyDistance = uiState.myWeeklyDistance
            )
        }

        item {
            SectionHeader(title = "주간 랭킹")
        }

        items(
            items = uiState.weeklyRanking,
            key = { it.user.id }
        ) { rankingItem ->
            UserRankingItem(
                rankingItem = rankingItem,
                onClick = { onUserClick(rankingItem.user.id) }
            )
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    userImageUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "안녕하세요,",
                style = MaterialTheme.typography.bodyMedium,
                color = OnBackgroundSecondary
            )
            Text(
                text = "$userName 님!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = OnBackground
            )
        }

        Avatar(
            imageUrl = userImageUrl,
            name = userName,
            size = AvatarSize.Large,
            showBorder = true
        )
    }
}

@Composable
private fun MyStatsCard(
    rank: Int?,
    weeklyDistance: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundElevated)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "내 순위",
                value = rank?.let { "${it}위" } ?: "-"
            )

            Spacer(modifier = Modifier.width(24.dp))

            StatItem(
                label = "이번 주 거리",
                value = String.format("%.1f km", weeklyDistance),
                valueColor = Primary
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = OnBackground
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = OnBackgroundSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = valueColor
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        color = OnBackground,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun HomeScreenPreview() {
    val mockRankings = remember {
        listOf(
            RankingItemUiModel(
                rank = 1,
                user = UserUiModel("1", "김러너", null, 156.5, 42),
                score = 156.5,
                change = RankChangeUiModel.NONE
            ),
            RankingItemUiModel(
                rank = 2,
                user = UserUiModel("2", "박조깅", null, 142.3, 38),
                score = 142.3,
                change = RankChangeUiModel.UP
            ),
            RankingItemUiModel(
                rank = 3,
                user = UserUiModel("3", "이마라톤", null, 128.7, 35),
                score = 128.7,
                change = RankChangeUiModel.DOWN
            ),
            RankingItemUiModel(
                rank = 4,
                user = UserUiModel("4", "최달리기", null, 115.2, 30),
                score = 115.2,
                change = RankChangeUiModel.UP
            ),
            RankingItemUiModel(
                rank = 5,
                user = UserUiModel("5", "정스프린트", null, 98.4, 28),
                score = 98.4,
                change = RankChangeUiModel.NONE
            )
        )
    }

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

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun HomeHeaderPreview() {
    RunnersHiTheme {
        HomeHeader(
            userName = "김러너",
            userImageUrl = null,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MyStatsCardPreview() {
    RunnersHiTheme {
        MyStatsCard(
            rank = 5,
            weeklyDistance = 42.5,
            modifier = Modifier.padding(16.dp)
        )
    }
}
