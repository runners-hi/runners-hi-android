package com.runnersHi.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runnersHi.presentation.model.RankChangeUiModel
import com.runnersHi.presentation.model.RankingItemUiModel
import com.runnersHi.presentation.model.UserUiModel
import com.runnersHi.presentation.ui.theme.CardBackground
import com.runnersHi.presentation.ui.theme.Error
import com.runnersHi.presentation.ui.theme.OnBackground
import com.runnersHi.presentation.ui.theme.OnBackgroundSecondary
import com.runnersHi.presentation.ui.theme.Primary
import com.runnersHi.presentation.ui.theme.RunnersHiTheme
import com.runnersHi.presentation.ui.theme.Success

@Composable
fun UserRankingItem(
    rankingItem: RankingItemUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RankBadge(rank = rankingItem.rank)

        Spacer(modifier = Modifier.width(12.dp))

        Avatar(
            imageUrl = rankingItem.user.profileImageUrl,
            name = rankingItem.user.name,
            size = AvatarSize.Medium
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = rankingItem.user.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = OnBackground
            )
            Text(
                text = "${rankingItem.user.totalRuns}회 러닝",
                style = MaterialTheme.typography.bodySmall,
                color = OnBackgroundSecondary
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = String.format("%.1f km", rankingItem.score),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Primary
            )
            RankChangeIndicator(change = rankingItem.change)
        }
    }
}

@Composable
private fun RankChangeIndicator(
    change: RankChangeUiModel,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (change) {
        RankChangeUiModel.UP -> "▲" to Success
        RankChangeUiModel.DOWN -> "▼" to Error
        RankChangeUiModel.NONE -> "-" to OnBackgroundSecondary
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun UserRankingItemPreview() {
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
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun UserRankingItemSecondPreview() {
    RunnersHiTheme {
        UserRankingItem(
            rankingItem = RankingItemUiModel(
                rank = 2,
                user = UserUiModel(
                    id = "2",
                    name = "박조깅",
                    profileImageUrl = null,
                    totalDistance = 142.3,
                    totalRuns = 38
                ),
                score = 142.3,
                change = RankChangeUiModel.DOWN
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
