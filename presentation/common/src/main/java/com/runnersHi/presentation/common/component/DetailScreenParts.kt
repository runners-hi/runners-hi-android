package com.runnersHi.presentation.common.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
            tint = Color.Unspecified,
            modifier = Modifier
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
        // Figma 341:5201: 상단 바는 상태 표시줄 아래 8 떨어져 있다
        BackTopBar(onBack = onBack, modifier = Modifier.padding(top = 8.dp))
        Text(
            text = title,
            color = BlueGray5,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = TERMS_BODY_TOP)
        ) {
            termsSections(content, title).forEachIndexed { i, section ->
                if (i > 0) Spacer(modifier = Modifier.height(TERMS_SECTION_GAP))
                if (section.heading != null) {
                    Text(
                        text = section.heading,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp
                    )
                    if (section.body.isNotEmpty()) Spacer(modifier = Modifier.height(TERMS_HEADING_GAP))
                }
                if (section.body.isNotEmpty()) {
                    Text(
                        text = section.body,
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (onAgree != null) {
            Box(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 20.dp)
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

private val TERMS_BODY_TOP = 12.dp
private val TERMS_HEADING_GAP = 12.dp
private val TERMS_SECTION_GAP = 34.dp

private data class TermsSection(val heading: String?, val body: String)

/**
 * 빈 줄로 나뉜 약관 본문을 Figma처럼 '굵은 소제목 + 본문' 묶음으로 나눈다.
 * 첫 묶음이 화면 제목과 같은 한 줄이면 중복이라 뺀다.
 */
private fun termsSections(content: String, title: String): List<TermsSection> =
    content.trim().split(Regex("\\n\\s*\\n"))
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .filterIndexed { i, block -> !(i == 0 && '\n' !in block && title.startsWith(block)) }
        .map { block ->
            val lines = block.lines().map { it.trim() }
            if (lines.size > 1) TermsSection(lines.first(), lines.drop(1).joinToString("\n"))
            else TermsSection(null, lines.first())
        }
