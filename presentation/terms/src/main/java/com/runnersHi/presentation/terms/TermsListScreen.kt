package com.runnersHi.presentation.terms

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.component.BackTopBar
import com.runnersHi.presentation.common.component.TermsDetailContent
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.BlueGrayWhite
import com.runnersHi.presentation.common.theme.Primary

/** 마이페이지 > 이용약관 */
@Composable
fun TermsListRoute(
    onBack: () -> Unit,
    viewModel: TermsListViewModel = hiltViewModel()
) {
    val state by viewModel.collectState()

    val detail = state.detailItem
    if (detail != null) {
        TermsDetailContent(
            title = detail.title,
            content = detail.content,
            onBack = { viewModel.sendEvent(TermsListContract.Event.DetailDismissed) }
        )
        return
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
            text = "이용약관",
            color = BlueGray5,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp)
        )
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            state.errorMessage != null -> Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.errorMessage.orEmpty(), color = BlueGray40, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "다시 시도",
                    color = Primary,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable {
                        viewModel.sendEvent(TermsListContract.Event.Retry)
                    }
                )
            }
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.items, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.sendEvent(TermsListContract.Event.ItemClicked(item))
                            }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            color = BlueGrayWhite,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = "상세보기",
                            tint = BlueGrayWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
