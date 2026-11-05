package com.runnersHi.presentation.record

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
                .height(56.dp)
                .padding(start = 24.dp, end = 24.dp, top = 12.dp)
                .wrapContentHeight(Alignment.CenterVertically)
        )
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Primary)
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "today") {
            Spacer(modifier = Modifier.height(20.dp))
            TodayCard(state.today)
        }
        items(state.past, key = { it.id }) { record -> PastCard(record) }
        item(key = "bottom") { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun RecordCard(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(BlueGray80)
            .border(1.dp, BlueGray70, shape)
            .padding(20.dp)
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
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (today == null) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = CommonR.drawable.ic_empty_track),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(60.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "러닝으로 하루를 채워보세요", color = Color.White, fontSize = 14.sp)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
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
            lineHeight = 24.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = BlueGray40, fontSize = 14.sp)
    }
}

@Composable
private fun PastCard(record: PastRecordUiModel) {
    RecordCard {
        Text(text = record.date, color = BlueGray20, fontSize = 16.sp, lineHeight = 20.sp)
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
            lineHeight = 24.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = BlueGray40,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                color = BlueGray40,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
