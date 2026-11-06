package com.runnersHi.presentation.common.component

import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.runnersHi.presentation.common.component.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.theme.BlueGray20
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray90

@Immutable
data class MissionItemModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val isCompleted: Boolean,
    val accentColor: Color,
    val imageUrl: String? = null
)

/** 홈과 미션 탭은 글자 굵기와 줄 간격이 다르다 */
enum class MissionGridStyle(
    val titleWeight: FontWeight,
    val titleLineHeight: TextUnit,
    val subtitleLineHeight: TextUnit,
    val textGap: Dp
) {
    // iOS MissionItemView는 홈/탭 공용: 14 regular, 13 tracking -0.39, 간격 2
    HOME(FontWeight.Normal, 16.7.sp, 15.5.sp, 2.dp),
    TAB(FontWeight.Normal, 16.7.sp, 15.5.sp, 2.dp)
}

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
    style: MissionGridStyle = MissionGridStyle.TAB,
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
                        style = style,
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
    style: MissionGridStyle,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val strokeColor = if (item.isCompleted) item.accentColor else BlueGray70
        val strokeWidth = 1.dp
        Box(
            modifier = Modifier
                .size(width = 72.dp, height = 100.dp)
                // 아이폰 MissionItemView는 홈/탭 공용: .shadow(color: blueGray90 30%, radius: 11, y: 4)
                .iosShadow(CircleShape, BlueGray90.copy(alpha = 0.3f), radius = 11.dp, y = 4.dp)
                .background(if (item.isCompleted) item.accentColor.copy(alpha = 0.2f) else BlueGray80, CircleShape)
                // SwiftUI stroke는 테두리선이 경계 한가운데에 걸친다(안쪽 0.5, 바깥 0.5)
                .drawBehind {
                    drawOutline(
                        outline = CircleShape.createOutline(size, layoutDirection, this),
                        color = strokeColor,
                        style = Stroke(width = strokeWidth.toPx())
                    )
                }
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 아이폰 MissionView: Image.resizable().scaledToFit().frame(60, 60) — 모든 미션 같은 틀
            val imageModifier = Modifier
                .requiredSize(60.dp)
                .alpha(if (item.isCompleted) 1f else 0.6f)
            if (item.imageUrl != null) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    colorFilter = if (item.isCompleted) null else grayscaleFilter,
                    placeholder = painterResource(id = missionIconRes(item.iconName)),
                    error = painterResource(id = missionIconRes(item.iconName)),
                    modifier = imageModifier
                )
            } else {
                Image(
                    painter = painterResource(id = missionIconRes(item.iconName)),
                    contentDescription = item.title,
                    colorFilter = if (item.isCompleted) null else grayscaleFilter,
                    modifier = imageModifier
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            color = BlueGray5,
            fontSize = 14.sp,
            fontWeight = style.titleWeight,
            lineHeight = style.titleLineHeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(style.textGap))
        Text(
            text = item.subtitle,
            color = BlueGray40,
            fontSize = 13.sp,
            letterSpacing = (-0.39).sp,
            lineHeight = style.subtitleLineHeight,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}


private const val COLUMNS = 3
private val grayscaleFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
