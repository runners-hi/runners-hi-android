package com.runnersHi.presentation.region

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import com.runnersHi.presentation.common.theme.Pretendard
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.runnersHi.presentation.common.component.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray20
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.BlueGrayWhite
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme

/**
 * Region Selection Route: 상태 수집 및 이펙트 처리
 */
@Composable
fun RegionSelectionRoute(
    viewModel: RegionSelectionViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val state by viewModel.collectState()
    val context = LocalContext.current

    viewModel.collectEffect { effect ->
        when (effect) {
            is RegionSelectionContract.Effect.NavigateToMain -> onNavigateToMain()
            is RegionSelectionContract.Effect.NavigateBack -> onNavigateBack?.invoke()
            is RegionSelectionContract.Effect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 시스템 뒤로가기도 화면의 뒤로 버튼과 같게 동작한다.
    BackHandler { viewModel.sendEvent(RegionSelectionContract.Event.BackClicked) }

    RegionSelectionScreen(
        state = state,
        onEvent = viewModel::sendEvent
    )
}

/**
 * Region Selection Screen: 순수 UI
 */
@Composable
fun RegionSelectionScreen(
    state: RegionSelectionContract.State,
    onEvent: (RegionSelectionContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueGray90)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
        ) {
            // 상단 네비게이션 바
            TopNavigationBar(
                onBackClick = { onEvent(RegionSelectionContract.Event.BackClicked) }
            )

            // 타이틀
            Text(
                text = "러닝 지역을 골라주세요",
                color = BlueGray5,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 28.64.sp, // 24pt 기본 글줄
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp)
            )

            // 서브 텍스트
            Text(
                text = "러닝 지역은 한 곳만 선택할 수 있어요.",
                color = Primary,
                fontSize = 14.sp,
                lineHeight = 16.71.sp, // 14pt 기본 글줄
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 검색 입력 필드
            SearchTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(RegionSelectionContract.Event.SearchQueryChanged(it)) },
                focusRequester = focusRequester,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 검색 결과 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(BlueGray80)
            ) {
                when {
                    // 네트워크 에러
                    state.isNetworkError -> {
                        NetworkErrorContent(
                            onRetry = { onEvent(RegionSelectionContract.Event.RetrySearch) },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    // 검색 중
                    state.isSearching -> {
                        CircularProgressIndicator(
                            color = Primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    // 검색 결과 없음
                    state.hasSearched && state.searchResults.isEmpty() -> {
                        Text(
                            text = "검색 결과가 없습니다",
                            color = BlueGray40,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    // 검색 결과 있음
                    state.searchResults.isNotEmpty() -> {
                        // 아이폰처럼 목록 배경이 화면 맨 아래까지 이어지게 한다.
                        LazyColumn(
                            contentPadding = WindowInsets.navigationBars.asPaddingValues()
                        ) {
                            items(state.searchResults) { region ->
                                RegionItem(
                                    region = region,
                                    onClick = {
                                        focusManager.clearFocus() // 키보드 닫기
                                        onEvent(RegionSelectionContract.Event.RegionSelected(region))
                                    },
                                    isLoading = state.isLoading
                                )
                            }
                        }
                    }
                    // 초기 상태 (아무것도 표시 안함)
                    else -> {
                        // 빈 상태
                    }
                }
            }
        }

        // 전체 로딩 오버레이 (지역 선택 중)
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BlueGrayWhite, modifier = Modifier.size(30.dp)) // iOS scaleEffect(1.5)
            }
        }
    }
}

@Composable
private fun TopNavigationBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(BlueGray90)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 12.dp)
        ) {
            Icon(
                // 아이폰: Image(systemName: "arrow.left").font(.system(size: 24)), 흰색
                painter = painterResource(id = R.drawable.ic_arrow_left_sf),
                contentDescription = "뒤로가기",
                tint = BlueGrayWhite,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BlueGray80)
            .border(0.5.dp, BlueGray70, RoundedCornerShape(16.dp)) // iOS 1pt 선이 모서리 가운데에 그려진 뒤 잘려 안쪽 0.5만 보인다
            .padding(horizontal = 19.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    color = BlueGrayWhite,
                    fontSize = 16.sp,
                    lineHeight = 19.09.sp,
                    fontFamily = Pretendard,
                    fontFeatureSettings = "'kern' 0"
                ),
                interactionSource = interactionSource,
                cursorBrush = SolidColor(Primary),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = "지역(시) 검색",
                                color = BlueGray20,
                                fontSize = 16.sp,
                                lineHeight = 20.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun RegionItem(
    region: RegionUiModel,
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(enabled = !isLoading) { onClick() }
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = region.name,
            color = BlueGrayWhite,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun NetworkErrorContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "네트워크 오류가 발생했습니다",
            color = BlueGray40,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRetry) {
            Text(
                text = "다시 시도",
                color = Primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun RegionSelectionScreenPreview() {
    RunnersHiTheme {
        RegionSelectionScreen(
            state = RegionSelectionContract.State(
                searchQuery = "강",
                hasSearched = true,
                searchResults = listOf(
                    RegionUiModel("1", "강릉시", 0..1),
                    RegionUiModel("2", "강진군", 0..1)
                )
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun RegionSelectionScreenEmptyPreview() {
    RunnersHiTheme {
        RegionSelectionScreen(
            state = RegionSelectionContract.State(
                searchQuery = "없는지역",
                hasSearched = true,
                searchResults = emptyList()
            ),
            onEvent = {}
        )
    }
}
