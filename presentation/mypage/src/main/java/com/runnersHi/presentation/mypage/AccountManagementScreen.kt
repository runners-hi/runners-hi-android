package com.runnersHi.presentation.mypage

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.component.BackTopBar
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray90

@Composable
fun AccountManagementRoute(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: AccountManagementViewModel = hiltViewModel()
) {
    viewModel.collectEffect { effect ->
        when (effect) {
            AccountManagementContract.Effect.LoggedOut -> onLoggedOut()
        }
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
