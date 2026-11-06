package com.runnersHi.presentation.terms

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.component.TermsDetailContent
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.BlueGrayWhite
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.PrimarySecondary
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import com.runnersHi.presentation.terms.R

// Figma 색상
private val ButtonDisabled = PrimarySecondary
private val ButtonTextDisabled = BlueGray90

/**
 * Terms Agreement Route: 상태 수집 및 이펙트 처리
 */
@Composable
fun TermsAgreementRoute(
    viewModel: TermsAgreementViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val state by viewModel.collectState()
    val context = LocalContext.current

    viewModel.collectEffect { effect ->
        when (effect) {
            is TermsAgreementContract.Effect.NavigateToMain -> onNavigateToMain()
            is TermsAgreementContract.Effect.NavigateBack -> onNavigateBack?.invoke()
            is TermsAgreementContract.Effect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val detail = state.detailItem
    if (detail != null) {
        TermsDetailContent(
            title = detail.title,
            content = detail.content,
            onBack = { viewModel.sendEvent(TermsAgreementContract.Event.DetailDismissed) },
            onAgree = { viewModel.sendEvent(TermsAgreementContract.Event.DetailAgreed(detail.id)) }
        )
    } else {
        BackHandler { viewModel.sendEvent(TermsAgreementContract.Event.BackClicked) }
        TermsAgreementScreen(
            state = state,
            onEvent = viewModel::sendEvent
        )
    }
}

/**
 * Terms Agreement Screen: 순수 UI (Figma 디자인 적용)
 */
@Composable
fun TermsAgreementScreen(
    state: TermsAgreementContract.State,
    onEvent: (TermsAgreementContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueGray90)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 네비게이션 바
            TopNavigationBar(
                onBackClick = { onEvent(TermsAgreementContract.Event.BackClicked) }
            )

            // 타이틀
            // iOS TermsAgreementView.swift:29-38: VStack(spacing 4) 두 줄, 24 semibold
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 32.dp)
            ) {
                listOf("가입을 위해", "약관 동의가 필요해요").forEach { line ->
                    Text(
                        text = line,
                        color = BlueGray5,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 29.sp
                    )
                }
            }

            // 전체 동의
            AllAgreeRow(
                isChecked = state.allAgreed,
                // 목록을 받기 전에는 체크할 약관이 없다.
                onCheckedChange = {
                    if (state.termsItems.isNotEmpty()) onEvent(TermsAgreementContract.Event.ToggleAllAgreed)
                },
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            // 구분선
            HorizontalDivider(
                thickness = 1.dp,
                color = BlueGray70
            )

            // 약관 목록
            if (state.isLoading && state.termsItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp)
                ) {
                    items(state.termsItems) { item ->
                        TermsItemRow(
                            item = item,
                            onCheckedChange = {
                                onEvent(TermsAgreementContract.Event.ToggleTermItem(item.id))
                            },
                            onArrowClick = {
                                onEvent(TermsAgreementContract.Event.ViewTermsDetail(item))
                            }
                        )
                    }
                }
            }

            // 에러 메시지
            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(vertical = 8.dp)
                )
            }

            // 다음 버튼
            Button(
                onClick = { onEvent(TermsAgreementContract.Event.ProceedClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(71.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.canProceed) Primary else ButtonDisabled,
                    disabledContainerColor = ButtonDisabled
                ),
                enabled = state.canProceed && !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = BlueGrayWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "다음",
                        color = BlueGray90,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "뒤로가기",
                tint = BlueGray5, // iOS arrow_back.svg #F1F2F4
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun AllAgreeRow(
    isChecked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                id = if (isChecked) R.drawable.ic_check_on else R.drawable.ic_check_off
            ),
            contentDescription = if (isChecked) "선택됨" else "선택 안됨",
            tint = if (isChecked) Color.Unspecified else Color.White, // 아이폰 check_gray.svg 채움 흰색
            modifier = Modifier.size(24.dp).scale(if (isChecked) 1.2f else 1f) // iOS check_main 은 20 뷰박스를 24 에 꽉 채움
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "약관에 모두 동의합니다.",
            color = BlueGrayWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TermsItemRow(
    item: TermsItemUiModel,
    onCheckedChange: () -> Unit,
    onArrowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                id = if (item.isAgreed) R.drawable.ic_check_on else R.drawable.ic_check_off
            ),
            contentDescription = if (item.isAgreed) "동의함" else "동의 안함",
            tint = if (item.isAgreed) Color.Unspecified else Color.White, // 아이폰 check_gray.svg 채움 흰색
            modifier = Modifier.size(24.dp).scale(if (item.isAgreed) 1.2f else 1f) // iOS check_main 은 20 뷰박스를 24 에 꽉 채움
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = buildTermsTitle(item.title, item.required),
            color = BlueGrayWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "상세보기",
            tint = BlueGray5, // iOS arrow_right.svg #F1F2F4
            modifier = Modifier
                .size(24.dp)
                .clickable { onArrowClick() }
        )
    }
}

private fun buildTermsTitle(title: String, required: Boolean): String {
    val suffix = if (required) " (필수)" else " (선택)"
    return "$title$suffix"
}

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun TermsAgreementScreenPreview() {
    RunnersHiTheme {
        TermsAgreementScreen(
            state = TermsAgreementContract.State(
                isLoading = false,
                termsItems = listOf(
                    TermsItemUiModel("1", "서비스 이용약관 동의", "https://example.com", true, false),
                    TermsItemUiModel("2", "개인정보 수집 및 이용 동의", "https://example.com", true, false),
                    TermsItemUiModel("3", "마케팅 정보 수신 동의", "https://example.com", false, false),
                    TermsItemUiModel("4", "SNS 수신 동의", "https://example.com", false, false),
                    TermsItemUiModel("5", "만 14세 이상입니다", "https://example.com", true, false)
                )
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun TermsAgreementScreenAllAgreedPreview() {
    RunnersHiTheme {
        TermsAgreementScreen(
            state = TermsAgreementContract.State(
                isLoading = false,
                allAgreed = true,
                canProceed = true,
                termsItems = listOf(
                    TermsItemUiModel("1", "서비스 이용약관 동의", "https://example.com", true, true),
                    TermsItemUiModel("2", "개인정보 수집 및 이용 동의", "https://example.com", true, true),
                    TermsItemUiModel("3", "마케팅 정보 수신 동의", "https://example.com", false, true),
                    TermsItemUiModel("4", "SNS 수신 동의", "https://example.com", false, true),
                    TermsItemUiModel("5", "만 14세 이상입니다", "https://example.com", true, true)
                )
            ),
            onEvent = {}
        )
    }
}
