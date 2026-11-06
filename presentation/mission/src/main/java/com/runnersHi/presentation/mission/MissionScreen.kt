package com.runnersHi.presentation.mission

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import com.runnersHi.presentation.common.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.component.MissionGrid
import com.runnersHi.presentation.common.component.SeasonBannerPill
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.Primary

@Composable
fun MissionRoute(
    modifier: Modifier = Modifier,
    viewModel: MissionViewModel = hiltViewModel()
) {
    val state by viewModel.collectState()
    MissionScreen(state = state, onEvent = viewModel::sendEvent, modifier = modifier)
}

@Composable
fun MissionScreen(
    state: MissionContract.State,
    onEvent: (MissionContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "미션",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .height(56.dp)
                .padding(horizontal = 24.dp)
                .wrapContentHeight(Alignment.CenterVertically)
        )
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
                    modifier = Modifier.clickable { onEvent(MissionContract.Event.Retry) }
                )
            }
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 112.dp), // 아이폰 40+120에서 탭 바에 가려지는 48을 뺀 값
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                state.sections.forEach { section -> SectionView(section) }
            }
        }
    }
}

@Composable
private fun SectionView(section: MissionSectionUiModel) {
    Column {
        Text(
            text = section.title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        if (section.bannerTitle != null && section.bannerPeriod != null) {
            Spacer(modifier = Modifier.height(16.dp))
            SeasonBannerPill(title = section.bannerTitle, period = section.bannerPeriod)
        }
        Spacer(modifier = Modifier.height(16.dp))
        MissionGrid(items = section.items)
    }
}
