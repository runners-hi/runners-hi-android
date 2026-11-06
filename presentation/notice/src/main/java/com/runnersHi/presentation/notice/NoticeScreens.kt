package com.runnersHi.presentation.notice

import androidx.compose.ui.platform.testTag
import com.runnersHi.presentation.common.component.EmptyView
import com.runnersHi.presentation.common.component.ErrorView
import com.runnersHi.presentation.common.component.LoadingView
import com.runnersHi.presentation.common.component.PullRefresh
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import com.runnersHi.presentation.common.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.component.BackTopBar
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.BlueGrayWhite
import com.runnersHi.presentation.common.theme.Primary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd")
private fun LocalDate.display() = format(DATE_FORMAT)

/** 마이페이지 > 공지사항 */
@Composable
fun NoticeListRoute(
    onBack: () -> Unit,
    onNoticeClick: (String) -> Unit,
    viewModel: NoticeListViewModel = hiltViewModel()
) {
    val state by viewModel.collectState()

    BackHandler(onBack = onBack)
    NoticeScaffold(onBack = onBack) {
        Text(
            text = "공지사항",
            color = BlueGray5,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp)
        )
        when {
            state.isLoading -> LoadingView()
            state.errorMessage != null -> ErrorView(state.errorMessage.orEmpty(), onRetry = {
                viewModel.sendEvent(NoticeListContract.Event.Retry)
            })
            state.notices.isEmpty() -> EmptyView("공지사항이 없어요")
            else -> PullRefresh(
                isRefreshing = false,
                onRefresh = { viewModel.sendEvent(NoticeListContract.Event.Retry) }
            ) { LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(state.notices, key = { _, notice -> notice.id }) { index, notice ->
                    if (index == state.notices.lastIndex && state.nextCursor != null) {
                        LaunchedEffect(notice.id) {
                            viewModel.sendEvent(NoticeListContract.Event.LoadMore)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("notice_item")
                            .clickable { onNoticeClick(notice.id) }
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text(text = notice.title, color = BlueGrayWhite, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = notice.date.display(), color = BlueGray40, fontSize = 13.sp)
                    }
                }
            } }
        }
    }
}

/** 공지사항 상세 */
@Composable
fun NoticeDetailRoute(
    noticeId: String,
    onBack: () -> Unit,
    viewModel: NoticeDetailViewModel = hiltViewModel()
) {
    val state by viewModel.collectState()
    val load = { viewModel.sendEvent(NoticeDetailContract.Event.Load(noticeId)) }

    LaunchedEffect(noticeId) { load() }
    BackHandler(onBack = onBack)
    NoticeScaffold(onBack = onBack) {
        val notice = state.notice
        when {
            state.isLoading -> LoadingView()
            state.errorMessage != null -> ErrorView(state.errorMessage.orEmpty(), onRetry = load)
            notice != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = notice.title,
                    modifier = Modifier.testTag("notice_detail_title"),
                    color = BlueGray5,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = notice.date.display(), color = BlueGray40, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = notice.content, color = BlueGrayWhite, fontSize = 15.sp, lineHeight = 24.sp)
            }
        }
    }
}

@Composable
private fun NoticeScaffold(onBack: () -> Unit, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueGray90)
            .systemBarsPadding()
    ) {
        BackTopBar(onBack = onBack)
        content()
    }
}
