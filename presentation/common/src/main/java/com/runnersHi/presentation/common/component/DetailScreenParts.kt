package com.runnersHi.presentation.common.component

import androidx.compose.ui.platform.testTag
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import com.runnersHi.presentation.common.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.R
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.BlueGrayWhite
import com.runnersHi.presentation.common.theme.Primary

/** 뒤로가기 아이콘만 있는 상단 바 (높이 56) */
@Composable
fun BackTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = "뒤로가기",
            tint = BlueGray5, // iOS arrow_back.svg #F1F2F4 (벡터 원본은 검정이라 색을 입혀야 보인다)
            modifier = Modifier
                .testTag("back_button")
                .size(24.dp)
                .clickable(onClick = onBack)
        )
    }
}

/**
 * 약관 전문 화면.
 * [onAgree]가 있으면 하단에 동의 버튼을 보여준다.
 */
@Composable
fun TermsDetailContent(
    title: String,
    content: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onAgree: (() -> Unit)? = null
) {
    BackHandler(onBack = onBack)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlueGray90)
            .systemBarsPadding()
    ) {
        BackTopBar(onBack = onBack)
        Text(
            text = title,
            color = BlueGray5,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .testTag("terms_detail_title")
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp)
        )
        // iOS TermsDetailView.swift:40-46: 14pt 본문 한 덩어리, lineSpacing 4, 가로 24
        Text(
            text = content,
            color = BlueGrayWhite,
            fontSize = 14.sp,
            lineHeight = 20.71.sp, // 아이폰: SF 14 줄 높이 16.71 + lineSpacing(4)
            modifier = Modifier
                .testTag("terms_detail_content")
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        )
        if (onAgree != null) {
            Box(
                modifier = Modifier
                    .testTag("terms_detail_agree")
                    .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(71.dp))
                    .background(Primary)
                    .clickable(onClick = onAgree),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "동의하기",
                    color = BlueGray90,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
