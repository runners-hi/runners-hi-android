package com.runnersHi.presentation.common.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.theme.BlueGray20
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80

@Immutable
data class MissionItemModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val isCompleted: Boolean,
    val accentColor: Color
)

/** 시즌 미션 기간을 보여주는 알약 모양 배너 */
@Composable
fun SeasonBannerPill(
    title: String,
    period: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(BlueGray80),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = BlueGray40, fontSize = 14.sp)
        Text(text = period, color = BlueGray20, fontSize = 14.sp)
    }
}

/** 3열 미션 그리드. 스크롤 영역 안에서 쓰도록 Lazy를 쓰지 않는다. */
@Composable
fun MissionGrid(
    items: List<MissionItemModel>,
    modifier: Modifier = Modifier,
    onItemClick: ((String) -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items.chunked(COLUMNS).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                row.forEach { item ->
                    MissionItemView(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = onItemClick?.let { { it(item.id) } }
                    )
                }
                repeat(COLUMNS - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun MissionItemView(
    item: MissionItemModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val border = BorderStroke(1.dp, if (item.isCompleted) item.accentColor else BlueGray70)
        Box(
            modifier = Modifier
                .size(width = 72.dp, height = 100.dp)
                .clip(CircleShape)
                .background(if (item.isCompleted) item.accentColor.copy(alpha = 0.2f) else BlueGray80)
                .border(border, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = missionIconRes(item.iconName)),
                contentDescription = item.title,
                colorFilter = if (item.isCompleted) null else grayscaleFilter,
                modifier = Modifier
                    .size(60.dp)
                    .alpha(if (item.isCompleted) 1f else 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            color = BlueGray5,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.subtitle,
            color = BlueGray40,
            fontSize = 13.sp,
            letterSpacing = (-0.39).sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}

private const val COLUMNS = 3
private val grayscaleFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
