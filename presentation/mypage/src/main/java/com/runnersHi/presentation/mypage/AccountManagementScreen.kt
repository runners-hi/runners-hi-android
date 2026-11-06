package com.runnersHi.presentation.mypage

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.runnersHi.presentation.common.theme.BlueGrayWhite
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import com.runnersHi.presentation.common.component.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.R as CommonR
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
    bottomBar: @Composable () -> Unit = {},
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
            text = { Text(text = "러닝 기록, 미션 달성 내역, 프로필 사진 등 계정 정보가 모두 삭제되고 되돌릴 수 없어요.", color = BlueGray40) },
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

    // 아이폰 3배 화면 실측: 로그아웃 버튼 위 끝 ~ 탭바 위 끝 45.0pt(버튼 52 중 8.0이 탭바에 가려짐). 9.33으로 두면 43.8pt로 1.2 낮았다.
    var bottomBarHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Box(modifier = Modifier.fillMaxSize().background(BlueGray90)) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .then(
                if (bottomBarHeight > 0.dp) Modifier.padding(bottom = bottomBarHeight - 8.0.dp)
                else Modifier.navigationBarsPadding().padding(bottom = 40.dp)
            )
    ) {
        // iOS 인라인 내비게이션 바: 높이 44, 가운데 타이틀 17 semibold
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            contentAlignment = Alignment.Center
        ) {
            // 아이폰 26 기본 뒤로 버튼: 지름 44 원(테두리 0.5) 안에 흰 꺾쇠 10.67x18.67, 꺾쇠는 원 왼쪽에서 15·위에서 13
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(0.5.dp, BlueGray80, CircleShape)
                    .clickable(onClick = onBack)
            ) {
                Image(
                    painter = painterResource(id = CommonR.drawable.ic_arrow_right_w_fit),
                    contentDescription = "뒤로가기",
                    colorFilter = ColorFilter.tint(BlueGrayWhite),
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .padding(start = 15.dp, top = 13.dp)
                        .size(width = 10.67.dp, height = 18.67.dp)
                        .graphicsLayer { scaleX = -1f }
                )
            }
            Text(
                text = "계정 관리",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = if (state.isWithdrawing) "탈퇴 처리 중..." else "회원 탈퇴",
            color = BlueGray40,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .testTag("withdraw_button")
                .clickable(enabled = !state.isWithdrawing) {
                    viewModel.sendEvent(AccountManagementContract.Event.WithdrawClicked)
                }
                .padding(16.dp)
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BlueGray70)
                .testTag("logout_button")
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
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .onSizeChanged { bottomBarHeight = with(density) { it.height.toDp() } }
    ) { bottomBar() }
    }
}
