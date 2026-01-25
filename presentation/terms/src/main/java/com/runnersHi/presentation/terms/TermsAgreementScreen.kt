package com.runnersHi.presentation.terms

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.BlueGrayWhite
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme

/**
 * Terms Agreement Route: 상태 수집 및 이펙트 처리
 */
@Composable
fun TermsAgreementRoute(
    viewModel: TermsAgreementViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit
) {
    val state by viewModel.collectState()
    val context = LocalContext.current

    viewModel.collectEffect { effect ->
        when (effect) {
            is TermsAgreementContract.Effect.NavigateToMain -> onNavigateToMain()
            is TermsAgreementContract.Effect.OpenTermsWebView -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(effect.url))
                context.startActivity(intent)
            }
            is TermsAgreementContract.Effect.ShowToast -> {
                // TODO: Show toast
            }
        }
    }

    TermsAgreementScreen(
        state = state,
        onEvent = viewModel::sendEvent
    )
}

/**
 * Terms Agreement Screen: 순수 UI
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 제목
            Text(
                text = "서비스 이용을 위해\n약관에 동의해주세요",
                color = BlueGrayWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 전체 동의
            AllAgreeRow(
                isChecked = state.allAgreed,
                onCheckedChange = { onEvent(TermsAgreementContract.Event.ToggleAllAgreed) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
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
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.termsItems) { item ->
                        TermsItemRow(
                            item = item,
                            onCheckedChange = {
                                onEvent(TermsAgreementContract.Event.ToggleTermItem(item.id))
                            },
                            onViewDetail = {
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
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // 동의하고 시작하기 버튼
            Button(
                onClick = { onEvent(TermsAgreementContract.Event.ProceedClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(71.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.canProceed) Primary else BlueGray70,
                    disabledContainerColor = BlueGray70
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
                        text = "동의하고 시작하기",
                        color = if (state.canProceed) Color.Black else BlueGray40,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
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
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onCheckedChange() },
            colors = CheckboxDefaults.colors(
                checkedColor = Primary,
                uncheckedColor = BlueGray40,
                checkmarkColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "전체 동의",
            color = BlueGrayWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TermsItemRow(
    item: TermsItem,
    onCheckedChange: () -> Unit,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isAgreed,
            onCheckedChange = { onCheckedChange() },
            colors = CheckboxDefaults.colors(
                checkedColor = Primary,
                uncheckedColor = BlueGray40,
                checkmarkColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.width(12.dp))

        // 필수/선택 태그
        Text(
            text = if (item.isRequired) "[필수]" else "[선택]",
            color = if (item.isRequired) Primary else BlueGray40,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(4.dp))

        // 약관 제목
        Text(
            text = item.title,
            color = BlueGrayWhite,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        // 상세보기 버튼
        if (item.detailUrl != null) {
            Text(
                text = "보기",
                color = BlueGray40,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onViewDetail() }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun TermsAgreementScreenPreview() {
    RunnersHiTheme {
        TermsAgreementScreen(
            state = TermsAgreementContract.State(
                termsItems = listOf(
                    TermsItem("1", "서비스 이용약관", true, false, "https://example.com"),
                    TermsItem("2", "개인정보 처리방침", true, false, "https://example.com"),
                    TermsItem("3", "위치정보 이용약관", true, false, "https://example.com"),
                    TermsItem("4", "마케팅 정보 수신 동의", false, false, "https://example.com")
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
                allAgreed = true,
                canProceed = true,
                termsItems = listOf(
                    TermsItem("1", "서비스 이용약관", true, true, "https://example.com"),
                    TermsItem("2", "개인정보 처리방침", true, true, "https://example.com"),
                    TermsItem("3", "위치정보 이용약관", true, true, "https://example.com"),
                    TermsItem("4", "마케팅 정보 수신 동의", false, true, "https://example.com")
                )
            ),
            onEvent = {}
        )
    }
}
