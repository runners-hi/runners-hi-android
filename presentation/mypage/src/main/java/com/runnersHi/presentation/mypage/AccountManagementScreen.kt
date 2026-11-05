package com.runnersHi.presentation.mypage

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.component.BackTopBar
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.Primary

@Composable
fun AccountManagementRoute(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
    onWithdrawn: () -> Unit,
    viewModel: AccountManagementViewModel = hiltViewModel()
) {
    val state by viewModel.collectState()
    val context = LocalContext.current

    viewModel.collectEffect { effect ->
        when (effect) {
            AccountManagementContract.Effect.LoggedOut -> onLoggedOut()
            AccountManagementContract.Effect.Withdrawn -> onWithdrawn()
            is AccountManagementContract.Effect.ShowToast ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
        }
    }

    if (state.showWithdrawConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.sendEvent(AccountManagementContract.Event.WithdrawDismissed) },
            containerColor = BlueGray80,
            title = { Text(text = "정말 탈퇴할까요?", color = BlueGray5) },
            text = { Text(text = "계정 정보와 미션 달성 내역이 삭제되고 되돌릴 수 없어요. 러닝 기록은 누구 것인지 알 수 없게 처리되어 남아요.", color = BlueGray40) },
            confirmButton = {
                TextButton(onClick = { viewModel.sendEvent(AccountManagementContract.Event.WithdrawConfirmed) }) {
                    Text(text = "탈퇴하기", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.sendEvent(AccountManagementContract.Event.WithdrawDismissed) }) {
                    Text(text = "취소", color = BlueGray40)
                }
            }
        )
    }
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueGray90)
            .systemBarsPadding()
    ) {
        BackTopBar(onBack = onBack)
        Text(
            text = "계정 관리",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = if (state.isWithdrawing) "탈퇴 처리 중..." else "회원 탈퇴",
            color = BlueGray40,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(enabled = !state.isWithdrawing) {
                    viewModel.sendEvent(AccountManagementContract.Event.WithdrawClicked)
                }
                .padding(16.dp)
        )
        Box(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 40.dp)
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BlueGray70)
                .clickable { viewModel.sendEvent(AccountManagementContract.Event.LogoutClicked) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "로그아웃",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
