package com.runnersHi.presentation.record

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.runnersHi.presentation.common.R as CommonR
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray20
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.PrimaryRed
import com.runnersHi.presentation.common.theme.PrimaryYellow

@Composable
fun RecordRoute(
    onRequestHealthPermission: () -> Unit,
    onOpenHealthConnectInstall: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecordViewModel = hiltViewModel()
) {
    val state by viewModel.collectState()

    // 권한 화면이나 스토어에서 돌아올 때도 다시 읽는다.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.sendEvent(RecordContract.Event.Refresh)
    }

    viewModel.collectEffect { effect ->
        when (effect) {
            RecordContract.Effect.RequestHealthPermission -> onRequestHealthPermission()
            RecordContract.Effect.OpenHealthConnectInstall -> onOpenHealthConnectInstall()
        }
    }

    RecordScreen(state = state, onEvent = viewModel::sendEvent, modifier = modifier)
}

@Composable
fun RecordScreen(
    state: RecordContract.State,
    onEvent: (RecordContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "기록",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 14.dp)
        )
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Color.White) // 아이폰: ProgressView.tint(.white)
            }
            state.access == RecordContract.Access.NEEDS_INSTALL -> GuideView(
                message = "러닝 기록을 보려면 Health Connect가 필요해요",
                action = "설치하기",
                onAction = { onEvent(RecordContract.Event.InstallClicked) }
            )
            state.access == RecordContract.Access.NEEDS_PERMISSION -> GuideView(
                message = "러닝 기록을 읽을 수 있게 권한을 허용해주세요",
                action = "권한 허용",
                onAction = { onEvent(RecordContract.Event.GrantPermissionClicked) }
            )
            state.errorMessage != null -> GuideView(
                message = state.errorMessage,
                action = "다시 시도",
                onAction = { onEvent(RecordContract.Event.Refresh) }
            )
            else -> RecordList(state)
        }
    }
}

@Composable
private fun GuideView(message: String, action: String, onAction: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = BlueGray40, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = action,
            color = Primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(onClick = onAction)
        )
    }
}

@Composable
private fun RecordList(state: RecordContract.State) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "today") { TodayCard(state.today) }
        items(state.past, key = { it.id }) { record -> PastCard(record) }
        // 아이폰 하단 여백 100에서 탭 바에 가려지는 48을 뺀 값 (카드 간격 16 포함)
        item(key = "bottom") { Spacer(modifier = Modifier.height(36.dp)) }
    }
}

@Composable
private fun RecordCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(BlueGray80)
            .border(1.dp, BlueGray70, shape)
    ) {
        content()
    }
}

@Composable
private fun TodayCard(today: TodayRecordUiModel?) {
    RecordCard {
        Text(
            text = "Today's Run",
            color = BlueGray20,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = if (today == null) 16.dp else 24.dp)
        )
        if (today == null) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = CommonR.drawable.ic_empty_track),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(60.dp)
                )
                Text(text = "러닝으로 하루를 채워보세요", color = Color.White, fontSize = 14.sp)
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                // 아이폰: HStack(spacing: 4) — 세로 기본 가운데 정렬
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TodayStat(CommonR.drawable.ic_track, Primary, today.distance, "Distance", Modifier.weight(1f))
                TodayStat(CommonR.drawable.ic_flame, PrimaryRed, today.pace, "Pace", Modifier.weight(1f))
                TodayStat(CommonR.drawable.ic_timer, PrimaryYellow, today.time, "Time", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TodayStat(
    @DrawableRes icon: Int,
    tint: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(width = 34.dp, height = 44.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = BlueGray40, fontSize = 14.sp)
    }
}

@Composable
private fun PastCard(record: PastRecordUiModel) {
    RecordCard {
        Text(
            text = record.date,
            color = BlueGray20,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            // 아이폰: HStack(spacing: 0) — 세로 기본 가운데 정렬
            verticalAlignment = Alignment.CenterVertically
        ) {
            PastStat(CommonR.drawable.ic_distance_mono, record.distance, "Distance", Modifier.weight(1f))
            PastStat(CommonR.drawable.ic_pace_mono, record.pace, "Pace", Modifier.weight(1f))
            PastStat(CommonR.drawable.ic_time_mono, record.time, "Time", Modifier.weight(1f))
        }
    }
}

@Composable
private fun PastStat(
    @DrawableRes icon: Int,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 아이폰: .scaledToFit().frame(height: 16) → 너비는 비율대로(16x16.0, 13.02, 13.49)
            // Icon은 높이만 주면 원래 너비(17.5·15·13.34)를 그대로 잡으므로 Image(Fit)로 그린다
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(BlueGray40),
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(16.dp)
            )
            Text(
                text = label,
                color = BlueGray40,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}
