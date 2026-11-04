package com.runnersHi.presentation.ranking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.domain.home.model.Tier
import com.runnersHi.domain.ranking.model.RankingEntry
import com.runnersHi.presentation.common.R as CommonR
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray30
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.Primary
import java.util.Locale

@Composable
fun RankingRoute(
    modifier: Modifier = Modifier,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val state by viewModel.collectState()
    RankingScreen(state = state, onEvent = viewModel::sendEvent, modifier = modifier)
}

@Composable
fun RankingScreen(
    state: RankingContract.State,
    onEvent: (RankingContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TitleBar(regionName = state.regionName)
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            state.errorMessage != null -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.errorMessage, color = BlueGray40, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "다시 시도",
                    color = Primary,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onEvent(RankingContract.Event.Retry) }
                )
            }
            else -> RankingList(state)
        }
    }
}

@Composable
private fun TitleBar(regionName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "랭킹", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        if (regionName.isNotEmpty()) {
            Icon(
                painter = painterResource(id = CommonR.drawable.ic_location_main),
                contentDescription = null,
                tint = BlueGray40,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = regionName,
                color = BlueGray40,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RankingList(state: RankingContract.State) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        state.topEntries.firstOrNull()?.let { first ->
            item(key = "first") { FirstPlaceCard(first) }
        }
        items(state.topEntries.drop(1), key = { it.rank }) { entry -> RankingRow(entry) }
        state.myEntry?.let { me ->
            item(key = "ellipsis") {
                Text(
                    text = "⋮",
                    color = BlueGray40,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth().padding(start = 40.dp)
                )
            }
            item(key = "me") { RankingRow(me) }
        }
        item(key = "bottom") { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun FirstPlaceCard(entry: RankingEntry) {
    val shape = RoundedCornerShape(40.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp + 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = shape,
                    ambientColor = Primary.copy(alpha = 0.78f),
                    spotColor = Primary.copy(alpha = 0.78f)
                )
                .clip(shape)
                .background(BlueGray80)
                .border(1.dp, Primary, shape)
                .padding(start = 20.dp, end = 20.dp, top = 58.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle(size = 24)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = entry.displayNickname,
                    color = Color.White,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TierLabel(entry.tier)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.distanceKm.toKm(),
                color = Primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Image(
            painter = painterResource(id = CommonR.drawable.ranking_gold),
            contentDescription = "1위",
            modifier = Modifier
                .size(100.dp)
                .offset(y = (-50).dp)
        )
    }
}

@Composable
private fun RankingRow(entry: RankingEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(BlueGray80)
            .padding(start = 12.dp, end = 19.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.Center) {
            val medal = when (entry.rank) {
                2 -> CommonR.drawable.ranking_silver
                3 -> CommonR.drawable.ranking_bronze
                else -> null
            }
            if (medal != null) {
                Image(
                    painter = painterResource(id = medal),
                    contentDescription = "${entry.rank}위",
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Text(text = entry.rank.toString(), color = Color.White, fontSize = 14.sp)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle(size = 20)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = entry.displayNickname,
                    color = BlueGray5,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TierLabel(entry.tier)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = entry.distanceKm.toKm(),
            color = if (entry.rank <= 3) Primary else Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AvatarCircle(size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(BlueGray70),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = CommonR.drawable.ic_user_mono),
            contentDescription = null,
            colorFilter = ColorFilter.tint(BlueGray40),
            modifier = Modifier.size((size / 2).dp)
        )
    }
}

@Composable
private fun TierLabel(tier: Tier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = tier.iconRes()),
            contentDescription = null,
            modifier = Modifier.size(width = 18.dp, height = 12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = tier.displayName(), color = BlueGray30, fontSize = 14.sp)
    }
}

private fun Tier.iconRes(): Int = when (this) {
    Tier.BRONZE -> CommonR.drawable.ic_tier_bronze
    Tier.SILVER -> CommonR.drawable.ic_tier_silver
    Tier.GOLD -> CommonR.drawable.ic_tier_gold
    Tier.PLATINUM -> CommonR.drawable.ic_tier_platinum
    Tier.DIAMOND -> CommonR.drawable.ic_tier_diamond
}

private fun Tier.displayName(): String =
    name.lowercase(Locale.ROOT).replaceFirstChar { it.titlecase(Locale.ROOT) }

private fun Double.toKm(): String = String.format(Locale.US, "%.2fkm", this)
