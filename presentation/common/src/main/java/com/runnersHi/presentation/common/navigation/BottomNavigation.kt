package com.runnersHi.presentation.common.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.R
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.RunnersHiTheme

/**
 * 앱 하단 네비게이션 탭
 */
enum class BottomNavTab {
    HOME, RANKING, RECORD, MISSION, MY_PAGE
}

/**
 * 하단 네비게이션 탭 아이템 정보
 */
@Immutable
data class BottomNavItem(
    val tab: BottomNavTab,
    val label: String,
    @DrawableRes val iconRes: Int
)

/**
 * 기본 하단 네비게이션 탭 아이템 목록
 */
val defaultBottomNavItems = listOf(
    BottomNavItem(
        tab = BottomNavTab.HOME,
        label = "홈",
        iconRes = R.drawable.ic_home_mono
    ),
    BottomNavItem(
        tab = BottomNavTab.RANKING,
        label = "랭킹",
        iconRes = R.drawable.ic_rank_mono
    ),
    BottomNavItem(
        tab = BottomNavTab.RECORD,
        label = "기록",
        iconRes = R.drawable.ic_record_mono
    ),
    BottomNavItem(
        tab = BottomNavTab.MISSION,
        label = "미션",
        iconRes = R.drawable.ic_mission_mono
    ),
    BottomNavItem(
        tab = BottomNavTab.MY_PAGE,
        label = "마이페이지",
        iconRes = R.drawable.ic_user_mono
    )
)

// Figma Design Colors
private val InactiveColor = Color(0xFF75808B) // BlueGray/50

/**
 * 러너스하이 하단 네비게이션 바
 *
 * Figma: node-id=1-1119
 *
 * @param currentTab 현재 선택된 탭
 * @param onTabSelected 탭 선택 콜백
 * @param modifier Modifier
 * @param items 표시할 탭 아이템 목록 (기본값: defaultBottomNavItems)
 */
@Composable
fun RunnersHiBottomNavigation(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier,
    items: List<BottomNavItem> = defaultBottomNavItems
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BlueGray90)
    ) {
        // Top line (1dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BlueGray70)
        )

        // Tab row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentTab == item.tab
                val color = if (selected) Primary else InactiveColor

                NavTabItem(
                    item = item,
                    selected = selected,
                    color = color,
                    onClick = { onTabSelected(item.tab) }
                )
            }
        }

        // Bottom home indicator bar
        HomeIndicatorBar()
    }
}

@Composable
private fun NavTabItem(
    item: BottomNavItem,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .height(60.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = item.iconRes),
            contentDescription = item.label,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun HomeIndicatorBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(66.dp)
                .height(4.dp)
                .background(
                    color = BlueGray40,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun RunnersHiBottomNavigationPreview() {
    RunnersHiTheme {
        RunnersHiBottomNavigation(
            currentTab = BottomNavTab.HOME,
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun RunnersHiBottomNavigationRankingSelectedPreview() {
    RunnersHiTheme {
        RunnersHiBottomNavigation(
            currentTab = BottomNavTab.RANKING,
            onTabSelected = {}
        )
    }
}
