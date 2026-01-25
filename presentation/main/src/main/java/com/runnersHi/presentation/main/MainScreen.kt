package com.runnersHi.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.domain.home.model.Tier
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.PrimaryCyan
import com.runnersHi.presentation.common.theme.PrimaryRed
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import kotlinx.coroutines.launch

// Colors from design spec
private val CardBackground = BlueGray80
private val CardBorder = BlueGray70
private val ProgressBackground = BlueGray70
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFFC7CBD1)
private val TextTertiary = Color(0xFF8F97A3)
private val InactiveColor = Color(0xFF75808B)
private val DayActiveBackground = Color(0xFF255860)
private val MissionCompletedBackground = Color(0x33FFCB2F)
private val MissionCompletedBorder = Color(0xFFFFCB2F)

@Composable
fun MainRoute(
    viewModel: MainViewModel = hiltViewModel(),
    onRequestHealthPermission: () -> Unit = {},
    onOpenHealthConnectSettings: () -> Unit = {},
    onOpenPlayStoreForHealthConnect: () -> Unit = {}
) {
    val state by viewModel.collectState()

    viewModel.collectEffect { effect ->
        when (effect) {
            is MainContract.Effect.NavigateToTierDetail -> {
                // TODO: Navigate to tier detail
            }
            is MainContract.Effect.NavigateToTodaysRunDetail -> {
                // TODO: Navigate to today's run detail
            }
            is MainContract.Effect.NavigateToMissionEvent -> {
                // TODO: Navigate to mission event
            }
            is MainContract.Effect.NavigateToMissionDetail -> {
                // TODO: Navigate to mission detail
            }
            is MainContract.Effect.ShowToast -> {
                // TODO: Show toast
            }
            is MainContract.Effect.RequestHealthPermission -> {
                onRequestHealthPermission()
            }
            is MainContract.Effect.OpenHealthConnectSettings -> {
                onOpenHealthConnectSettings()
            }
            is MainContract.Effect.OpenPlayStoreForHealthConnect -> {
                onOpenPlayStoreForHealthConnect()
            }
        }
    }

    MainScreen(
        state = state,
        onEvent = viewModel::sendEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Tier Info Bottom Sheet
    if (state.showTierInfoSheet) {
        TierInfoBottomSheet(
            tierInfo = state.tierInfo,
            tierGuideList = state.tierGuideList,
            sheetState = sheetState,
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    onEvent(MainContract.Event.TierSheetDismissed)
                }
            }
        )
    }

    Scaffold(
        containerColor = BlueGray90,
        bottomBar = {
            MainBottomNavigation(
                currentTab = state.currentTab,
                onTabSelected = { onEvent(MainContract.Event.BottomNavClicked(it)) }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (state.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.errorMessage,
                        color = TextSecondary,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "다시 시도",
                        color = Primary,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable { onEvent(MainContract.Event.RefreshData) }
                    )
                }
            }
        } else {
            when (state.currentTab) {
                BottomNavTab.HOME -> HomeContent(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier.padding(innerPadding)
                )
                else -> PlaceholderContent(
                    tabName = state.currentTab.name,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BlueGray90),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Title Bar
        item {
            TitleBar()
        }

        // Title Bar 하단 고정 영역
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Tier Card
        item {
            state.tierInfo?.let { tierInfo ->
                TierCard(
                    tierInfo = tierInfo,
                    onClick = { onEvent(MainContract.Event.TierCardClicked) },
                    onArrowClick = { onEvent(MainContract.Event.TierArrowClicked) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Today's Run Card (Empty State or Normal)
        item {
            if (state.isEmptyState) {
                TodaysRunEmptyCard(
                    onClick = { onEvent(MainContract.Event.TodaysRunClicked) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            } else {
                state.todaysRun?.let { todaysRun ->
                    TodaysRunCard(
                        todaysRun = todaysRun,
                        onClick = { onEvent(MainContract.Event.TodaysRunClicked) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // This Week Card (Empty State or Normal)
        item {
            if (state.isEmptyState) {
                ThisWeekEmptyCard(
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            } else {
                state.thisWeek?.let { thisWeek ->
                    ThisWeekCard(
                        thisWeek = thisWeek,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Mission Event Section
        item {
            state.missionEvent?.let { missionEvent ->
                MissionEventSection(
                    missionEvent = missionEvent,
                    onHeaderClick = { onEvent(MainContract.Event.MissionEventClicked) },
                    onMissionClick = { id -> onEvent(MainContract.Event.MissionItemClicked(id)) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun TitleBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Runners HI",
            color = Primary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "알림",
            tint = TextPrimary,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun TierCard(
    tierInfo: TierInfoUiModel,
    onClick: () -> Unit,
    onArrowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tier Icon (placeholder)
                Box(
                    modifier = Modifier
                        .size(60.dp, 40.dp)
                        .background(tierInfo.tier.toProgressColor().copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tierInfo.tier.name.first().toString(),
                        color = tierInfo.tier.toProgressColor(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tierInfo.tierName,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = tierInfo.level,
                        color = Color(0xFFF1F2F4),
                        fontSize = 14.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onArrowClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ">",
                        color = TextPrimary,
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress to next level",
                    color = TextTertiary,
                    fontSize = 14.sp
                )
                Text(
                    text = tierInfo.progressText,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { tierInfo.progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = tierInfo.tier.toProgressColor(),
                trackColor = ProgressBackground,
            )
        }
    }
}

@Composable
private fun TodaysRunCard(
    todaysRun: TodaysRunUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Run",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = ">",
                    color = TextSecondary,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RunDataItem(
                    icon = "🏃",
                    iconBackgroundColor = PrimaryCyan.copy(alpha = 0.2f),
                    value = todaysRun.distance,
                    label = "Distance",
                    modifier = Modifier.weight(1f)
                )
                RunDataItem(
                    icon = "🔥",
                    iconBackgroundColor = PrimaryRed.copy(alpha = 0.2f),
                    value = todaysRun.pace,
                    label = "Pace",
                    modifier = Modifier.weight(1f)
                )
                RunDataItem(
                    icon = "⏱",
                    iconBackgroundColor = Primary.copy(alpha = 0.2f),
                    value = todaysRun.time,
                    label = "Time",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RunDataItem(
    icon: String,
    iconBackgroundColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(34.dp, 44.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            color = TextTertiary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ThisWeekCard(
    thisWeek: ThisWeekUiModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "This Week",
                color = TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = thisWeek.totalDistance,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                thisWeek.days.forEach { day ->
                    DayIndicator(day = day)
                }
            }
        }
    }
}

@Composable
private fun DayIndicator(
    day: DayUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(30.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                if (day.hasRun) DayActiveBackground
                else Color(0x33ABB1BA)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.label,
            color = if (day.hasRun) PrimaryCyan else TextTertiary,
            fontSize = 14.sp
        )
        if (day.hasRun && day.distance != null) {
            Text(
                text = day.distance,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun MissionEventSection(
    missionEvent: MissionEventUiModel,
    onHeaderClick: () -> Unit,
    onMissionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onHeaderClick)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "미션 이벤트",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = ">",
                color = TextPrimary,
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Event Banner
        missionEvent.banner?.let { banner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(CardBackground)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row {
                    Text(
                        text = banner.title,
                        color = TextTertiary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = banner.period,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mission Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(300.dp)
        ) {
            items(missionEvent.missions) { mission ->
                MissionItem(
                    mission = mission,
                    onClick = { onMissionClick(mission.id) }
                )
            }
        }
    }
}

@Composable
private fun MissionItem(
    mission: MissionItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon Container
        Box(
            modifier = Modifier
                .size(72.dp, 100.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(
                    if (mission.isCompleted) MissionCompletedBackground
                    else CardBackground
                )
                .border(
                    1.dp,
                    if (mission.isCompleted) MissionCompletedBorder else CardBorder,
                    RoundedCornerShape(36.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder icon
            Text(
                text = if (mission.isCompleted) "✓" else "🏅",
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mission.name,
            color = Color(0xFFF1F2F4),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = mission.description,
            color = TextTertiary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun TodaysRunEmptyCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Run",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = ">",
                    color = TextSecondary,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Empty State Illustration (placeholder)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(BlueGray70),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏃",
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "오늘 달린 기록이 없어요",
                color = TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "러닝하러 가기",
                color = Primary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ThisWeekEmptyCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "This Week",
                color = TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "0 km",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Empty week indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                    DayIndicator(
                        day = DayUiModel(
                            label = day,
                            distance = null,
                            hasRun = false
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TierInfoBottomSheet(
    tierInfo: TierInfoUiModel?,
    tierGuideList: List<TierGuideItem>,
    sheetState: androidx.compose.material3.SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BlueGray80,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BlueGray70)
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Text(
                text = "러너 티어",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Current Tier Info
            tierInfo?.let { info ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BlueGray90)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(info.tier.toProgressColor().copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = info.tier.name.first().toString(),
                            color = info.tier.toProgressColor(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = info.tierName,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = info.level,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tier Guide List
            Text(
                text = "티어 가이드",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            tierGuideList.forEach { guideItem ->
                TierGuideRow(
                    guideItem = guideItem,
                    isCurrentTier = tierInfo?.tier == guideItem.tier
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tier Info Texts
            Text(
                text = "안내",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            tierInfoTexts.forEach { text ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "•",
                        color = TextTertiary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = text,
                        color = TextTertiary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TierGuideRow(
    guideItem: TierGuideItem,
    isCurrentTier: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isCurrentTier) BlueGray90 else Color.Transparent)
            .border(
                width = if (isCurrentTier) 1.dp else 0.dp,
                color = if (isCurrentTier) guideItem.tier.toProgressColor() else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(guideItem.tier.toProgressColor().copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = guideItem.tier.name.first().toString(),
                color = guideItem.tier.toProgressColor(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = guideItem.tierName,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = guideItem.levelRange,
                color = TextTertiary,
                fontSize = 12.sp
            )
        }
        if (isCurrentTier) {
            Text(
                text = "현재",
                color = Primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PlaceholderContent(
    tabName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueGray90),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$tabName 화면\n(준비 중)",
            color = TextSecondary,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MainBottomNavigation(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = BlueGray90,
        contentColor = Primary
    ) {
        BottomNavTab.entries.forEach { tab ->
            val selected = currentTab == tab

            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.toIcon(selected),
                        contentDescription = tab.toLabel(),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(text = tab.toLabel())
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

private fun BottomNavTab.toIcon(selected: Boolean): ImageVector {
    return when (this) {
        BottomNavTab.HOME -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
        BottomNavTab.RANKING -> if (selected) Icons.Filled.Star else Icons.Outlined.Star
        BottomNavTab.RECORD -> if (selected) Icons.Filled.Star else Icons.Outlined.Star
        BottomNavTab.MISSION -> if (selected) Icons.Filled.Star else Icons.Outlined.Star
        BottomNavTab.MY_PAGE -> if (selected) Icons.Filled.Person else Icons.Outlined.Person
    }
}

private fun BottomNavTab.toLabel(): String {
    return when (this) {
        BottomNavTab.HOME -> "홈"
        BottomNavTab.RANKING -> "랭킹"
        BottomNavTab.RECORD -> "기록"
        BottomNavTab.MISSION -> "미션"
        BottomNavTab.MY_PAGE -> "마이페이지"
    }
}

private fun Tier.toProgressColor(): Color {
    return when (this) {
        Tier.BRONZE -> Color(0xFFFF6B35)
        Tier.SILVER -> Color(0xFFA8B4C4)
        Tier.GOLD -> Primary
        Tier.PLATINUM -> Color(0xFF4ADE80)
        Tier.DIAMOND -> PrimaryCyan
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF17191C)
@Composable
private fun MainScreenPreview() {
    RunnersHiTheme {
        MainScreen(
            state = MainContract.State(
                isLoading = false,
                tierInfo = TierInfoUiModel(
                    tier = Tier.GOLD,
                    tierName = "Gold Runner",
                    level = "Level 31",
                    progressPercent = 10,
                    progressText = "10%"
                ),
                todaysRun = TodaysRunUiModel(
                    distance = "7.4 km",
                    pace = "8'00''",
                    time = "40:00"
                ),
                thisWeek = ThisWeekUiModel(
                    totalDistance = "7.4 km",
                    days = listOf(
                        DayUiModel("M", "10", true),
                        DayUiModel("T", null, false),
                        DayUiModel("W", "0.3", true),
                        DayUiModel("T", "0.3", true),
                        DayUiModel("F", "0.3", true),
                        DayUiModel("S", "0.3", true),
                        DayUiModel("S", "0.3", true)
                    )
                ),
                missionEvent = MissionEventUiModel(
                    banner = EventBannerUiModel("추석 이벤트", "2025.10.01 - 2025.10.31"),
                    missions = listOf(
                        MissionItemUiModel("1", "문라이트", "10월 러닝 인증", "", true),
                        MissionItemUiModel("2", "전력질주", "페이스 5'00\"", "", false),
                        MissionItemUiModel("3", "밤톨 러닝", "1km", "", true)
                    )
                )
            ),
            onEvent = {}
        )
    }
}
