package com.runnersHi.presentation.notification

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import com.runnersHi.presentation.common.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.domain.notification.model.AppNotification
import com.runnersHi.presentation.common.component.BackTopBar
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.BlueGrayWhite
import com.runnersHi.presentation.common.theme.Primary
import java.time.format.DateTimeFormatter

private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")

/** 홈 종 아이콘, 마이페이지 > 알림 */
@Composable
fun NotificationRoute(
    onBack: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val state by viewModel.collectState()
    val context = LocalContext.current

    viewModel.collectEffect { effect ->
        when (effect) {
            is NotificationContract.Effect.ShowToast ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
        }
    }
    BackHandler(onBack = onBack)
    NotificationScreen(state = state, onEvent = viewModel::sendEvent, onBack = onBack)
}

@Composable
fun NotificationScreen(
    state: NotificationContract.State,
    onEvent: (NotificationContract.Event) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueGray90)
            .systemBarsPadding()
    ) {
        BackTopBar(onBack = onBack)
        Text(
            text = "알림",
            color = BlueGray5,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "푸시 알림 받기",
                color = BlueGrayWhite,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = state.pushEnabled,
                onCheckedChange = { onEvent(NotificationContract.Event.PushToggled(it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BlueGray90,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = BlueGray40,
                    uncheckedTrackColor = BlueGray80,
                    uncheckedBorderColor = BlueGray70
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            state.errorMessage != null -> Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.errorMessage, color = BlueGray40, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "다시 시도",
                    color = Primary,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onEvent(NotificationContract.Event.Retry) }
                )
            }
            state.notifications.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(text = "아직 받은 알림이 없어요", color = BlueGray40, fontSize = 14.sp)
            }
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.notifications, key = { it.id }) { NotificationRow(it) }
            }
        }
    }
}

@Composable
private fun NotificationRow(item: AppNotification) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (!item.isRead) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Primary, CircleShape)
                )
            }
            Text(
                text = item.title,
                color = BlueGrayWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(text = item.body, color = BlueGray5, fontSize = 14.sp, lineHeight = 20.sp)
        Text(text = item.createdAt.format(DATE_FORMAT), color = BlueGray40, fontSize = 13.sp)
    }
}
