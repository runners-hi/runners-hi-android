package com.runnersHi.presentation.common.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * 기본 하단 네비게이션 탭 아이템 목록
 */
val defaultBottomNavItems = listOf(
    BottomNavItem(
        tab = BottomNavTab.HOME,
        label = "홈",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        tab = BottomNavTab.RANKING,
        label = "랭킹",
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Outlined.Star
    ),
    BottomNavItem(
        tab = BottomNavTab.RECORD,
        label = "기록",
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Outlined.Star
    ),
    BottomNavItem(
        tab = BottomNavTab.MISSION,
        label = "미션",
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Outlined.Star
    ),
    BottomNavItem(
        tab = BottomNavTab.MY_PAGE,
        label = "마이페이지",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

private val InactiveColor = androidx.compose.ui.graphics.Color(0xFF75808B)

/**
 * 러너스하이 하단 네비게이션 바
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
    NavigationBar(
        modifier = modifier,
        containerColor = BlueGray90,
        contentColor = Primary
    ) {
        items.forEach { item ->
            val selected = currentTab == item.tab

            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(text = item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = InactiveColor,
                    unselectedTextColor = InactiveColor,
                    indicatorColor = BlueGray90
                )
            )
        }
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
