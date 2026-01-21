package com.runnersHi.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.ui.theme.OnPrimary
import com.runnersHi.presentation.ui.theme.RankBronze
import com.runnersHi.presentation.ui.theme.RankGold
import com.runnersHi.presentation.ui.theme.RankSilver
import com.runnersHi.presentation.ui.theme.RunnersHiTheme
import com.runnersHi.presentation.ui.theme.SurfaceVariant

@Composable
fun RankBadge(
    rank: Int,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (rank) {
        1 -> RankGold
        2 -> RankSilver
        3 -> RankBronze
        else -> SurfaceVariant
    }

    val textColor = when (rank) {
        1, 2, 3 -> OnPrimary
        else -> Color.White
    }

    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            ),
            color = textColor
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun RankBadgeFirstPreview() {
    RunnersHiTheme {
        RankBadge(rank = 1, modifier = Modifier.padding(8.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun RankBadgeSecondPreview() {
    RunnersHiTheme {
        RankBadge(rank = 2, modifier = Modifier.padding(8.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun RankBadgeThirdPreview() {
    RunnersHiTheme {
        RankBadge(rank = 3, modifier = Modifier.padding(8.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun RankBadgeOtherPreview() {
    RunnersHiTheme {
        RankBadge(rank = 42, modifier = Modifier.padding(8.dp))
    }
}
