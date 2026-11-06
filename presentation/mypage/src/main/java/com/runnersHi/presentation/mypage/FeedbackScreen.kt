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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import com.runnersHi.presentation.common.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.domain.user.usecase.SendFeedbackUseCase
import com.runnersHi.presentation.common.component.BackTopBar
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.Primary

/** 마이페이지 > 개발자에게 */
@Composable
fun FeedbackRoute(
    onBack: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val state by viewModel.collectState()
    val context = LocalContext.current

    viewModel.collectEffect { effect ->
        when (effect) {
            is FeedbackContract.Effect.ShowToast ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            FeedbackContract.Effect.Close -> onBack()
        }
    }
    BackHandler(onBack = onBack)
    FeedbackScreen(state = state, onEvent = viewModel::sendEvent, onBack = onBack)
}

@Composable
fun FeedbackScreen(
    state: FeedbackContract.State,
    onEvent: (FeedbackContract.Event) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueGray90)
            .systemBarsPadding()
            .imePadding()
    ) {
        BackTopBar(onBack = onBack)
        Text(
            text = "개발자에게",
            color = BlueGray5,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BlueGray80)
                .padding(16.dp)
        ) {
            BasicTextField(
                value = state.content,
                onValueChange = { onEvent(FeedbackContract.Event.ContentChanged(it)) },
                textStyle = TextStyle(color = BlueGray5, fontSize = 16.sp, lineHeight = 24.sp),
                cursorBrush = SolidColor(Primary),
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { contentDescription = "의견 입력" }
            )
            if (state.content.isEmpty()) {
                Text(
                    text = "불편한 점이나 바라는 점을 적어 주세요",
                    color = BlueGray40,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        }
        Text(
            text = "${state.content.length}/${SendFeedbackUseCase.MAX_LENGTH}",
            color = BlueGray40,
            fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 24.dp, vertical = 8.dp)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (state.canSend) Primary else BlueGray70)
                .clickable(enabled = state.canSend) { onEvent(FeedbackContract.Event.SendClicked) }
        ) {
            Text(
                text = if (state.isSending) "보내는 중" else "보내기",
                color = if (state.canSend) BlueGray90 else BlueGray40,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
