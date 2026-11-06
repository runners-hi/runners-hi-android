package com.runnersHi.presentation.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.Primary

/** Maestro가 잡는 접근성 id. 계약: error_view, retry_button, empty_view, loading_view. */
object StateTags {
    const val ERROR_VIEW = "error_view"
    const val RETRY_BUTTON = "retry_button"
    const val EMPTY_VIEW = "empty_view"
    const val LOADING_VIEW = "loading_view"
}

/** 화면 단위 오류. 메시지와 다시 시도 버튼을 보여 준다. */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = BlueGray40,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag(StateTags.ERROR_VIEW)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "다시 시도",
            color = Primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .testTag(StateTags.RETRY_BUTTON)
                .clickable(onClick = onRetry)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/** 데이터가 없는 화면. 의도된 빈 상태로 보이게 한다. */
@Composable
fun EmptyView(
    message: String,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Box(modifier = modifier.padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = BlueGray40,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag(StateTags.EMPTY_VIEW)
        )
    }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier = modifier.testTag(StateTags.LOADING_VIEW), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Primary)
    }
}

/**
 * 당겨서 새로고침. 내용이 짧아도 당길 수 있게 [content]는 화면 전체 높이를 차지해야 한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        content()
    }
}
