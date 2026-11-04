package com.runnersHi.presentation.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.domain.home.model.Tier
import com.runnersHi.presentation.common.R as CommonR
import com.runnersHi.presentation.common.component.MissionGrid
import com.runnersHi.presentation.common.component.MissionItemModel
import com.runnersHi.presentation.common.component.SeasonBannerPill
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.navigation.BottomNavTab
import com.runnersHi.presentation.common.navigation.RunnersHiBottomNavigation
import com.runnersHi.presentation.common.theme.BlueGray20
import com.runnersHi.presentation.common.theme.BlueGray30
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.PrimaryAmber
import com.runnersHi.presentation.common.theme.PrimaryRed
import com.runnersHi.presentation.common.theme.PrimarySecondary
import com.runnersHi.presentation.common.theme.PrimaryYellow
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import com.runnersHi.presentation.common.theme.TierBronzeProgress
import com.runnersHi.presentation.common.theme.TierPlatinumProgress
import com.runnersHi.presentation.common.theme.TierSilverProgress
import kotlinx.coroutines.launch

// Colors from design spec (Figma)
private val CardBackground = BlueGray80          // #2E3238
private val CardBorder = BlueGray70              // #454B54
private val ProgressBackground = BlueGray70     // #454B54
private val TextPrimary = Color.White            // #FFFFFF
private val TextSecondary = BlueGray20
private val TextTertiary = BlueGray40
private val DayActiveBackground = PrimarySecondary

@Composable
fun MainRoute(
    viewModel: MainViewModel = hiltViewModel(),
    onRequestHealthPermission: () -> Unit = {},
    onOpenHealthConnectSettings: () -> Unit = {},
    tabContent: @Composable (BottomNavTab, Modifier) -> Unit = { _, _ -> }
) {
    val state by viewModel.collectState()
    val context = LocalContext.current

    viewModel.collectEffect { effect ->
        when (effect) {
            is MainContract.Effect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
            is MainContract.Effect.RequestHealthPermission -> {
                onRequestHealthPermission()
            }
            is MainContract.Effect.OpenHealthConnectSettings -> {
                onOpenHealthConnectSettings()
            }
        }
    }

    // 홈 외의 탭에서 뒤로 가면 홈으로 돌아간다.
    BackHandler(enabled = state.currentTab != BottomNavTab.HOME) {
        viewModel.sendEvent(MainContract.Event.BackToHome)
    }

    MainScreen(
        state = state,
        onEvent = viewModel::sendEvent,
        tabContent = tabContent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit,
    modifier: Modifier = Modifier,
    tabContent: @Composable (BottomNavTab, Modifier) -> Unit = { _, _ -> }
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

    val tabStateHolder = rememberSaveableStateHolder()

    Scaffold(
        containerColor = BlueGray90,
        bottomBar = {
            RunnersHiBottomNavigation(
                currentTab = state.currentTab,
                onTabSelected = { onEvent(MainContract.Event.BottomNavClicked(it)) }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        // 탭을 오가도 스크롤 위치를 유지한다.
        tabStateHolder.SaveableStateProvider(state.currentTab.name) {
            if (state.currentTab != BottomNavTab.HOME) {
                // 다른 탭은 홈 로딩과 무관하게 바로 보여준다.
                tabContent(state.currentTab, Modifier.padding(innerPadding))
            } else if (state.isLoading) {
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
                HomeContent(
                    state = state,
                    onEvent = onEvent,
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
            if (state.isTodayEmpty) {
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
            if (state.isWeekEmpty) {
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
                    onMissionClick = { onEvent(MainContract.Event.MissionEventClicked) },
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
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column {
            // Tier info row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tier Icon - use vector drawable
                Icon(
                    painter = painterResource(id = tierInfo.tier.toTierIconRes()),
                    contentDescription = tierInfo.tierName,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(60.dp, 40.dp)
                )
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
                        color = BlueGray5,
                        fontSize = 14.sp
                    )
                }
                // Arrow button with background
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onArrowClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_bg_right),
                        contentDescription = "티어 정보",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress text row - "Progress to next level" and "10%" on same line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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

            // Progress Bar - no dot, continuous bar
            LinearProgressIndicator(
                progress = { tierInfo.progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = tierInfo.tier.toProgressColor(),
                trackColor = ProgressBackground,
                strokeCap = StrokeCap.Round
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
            // Header row
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
                // Arrow icon (20x20)
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right_mono),
                    contentDescription = "상세 보기",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Data items row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RunDataItem(
                    iconRes = CommonR.drawable.ic_track,
                    iconBackgroundColor = Primary.copy(alpha = 0.2f),
                    value = todaysRun.distance,
                    label = "Distance",
                    modifier = Modifier.weight(1f)
                )
                RunDataItem(
                    iconRes = CommonR.drawable.ic_flame,
                    iconBackgroundColor = PrimaryRed.copy(alpha = 0.2f),
                    value = todaysRun.pace,
                    label = "Pace",
                    modifier = Modifier.weight(1f)
                )
                RunDataItem(
                    iconRes = CommonR.drawable.ic_timer,
                    iconBackgroundColor = PrimaryYellow.copy(alpha = 0.2f),
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
    iconRes: Int,
    iconBackgroundColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon pill (34x44) with icon centered
        Box(
            modifier = Modifier
                .size(34.dp, 44.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
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
            // Title - left aligned
            Text(
                text = "This Week",
                color = TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Total distance - center aligned
            Text(
                text = thisWeek.totalDistance,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Day indicators
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
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pill with day letter (30x40)
        Box(
            modifier = Modifier
                .width(30.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(
                    if (day.hasRun) DayActiveBackground
                    else BlueGray30.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.label,
                color = if (day.hasRun) Primary else TextTertiary,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
        }
        // Distance below pill (only if hasRun)
        if (day.hasRun && day.distance != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = day.distance,
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(30.dp)
            )
        }
    }
}

@Composable
private fun MissionEventSection(
    missionEvent: MissionEventUiModel,
    onHeaderClick: () -> Unit,
    onMissionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Header - "미션 이벤트" with arrow
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
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp
            )
            // Arrow icon (24x24)
            Icon(
                painter = painterResource(id = CommonR.drawable.ic_arrow_right_w),
                contentDescription = "더보기",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        missionEvent.banner?.let { banner ->
            SeasonBannerPill(title = banner.title, period = banner.period)
            Spacer(modifier = Modifier.height(16.dp))
        }

        MissionGrid(
            items = missionEvent.missions,
            onItemClick = { onMissionClick() }
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
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right_mono),
                    contentDescription = "상세 보기",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Empty State Illustration
            Image(
                painter = painterResource(id = CommonR.drawable.ic_empty_track),
                contentDescription = null,
                modifier = Modifier.height(60.dp),
                contentScale = ContentScale.FillHeight
            )

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
                    Icon(
                        painter = painterResource(id = info.tier.toTierIconRes()),
                        contentDescription = info.tierName,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(48.dp, 32.dp)
                    )
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
        Icon(
            painter = painterResource(id = guideItem.tier.toTierIconRes()),
            contentDescription = guideItem.tierName,
            tint = Color.Unspecified,
            modifier = Modifier.size(36.dp, 24.dp)
        )
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

private fun Tier.toProgressColor(): Color {
    return when (this) {
        Tier.BRONZE -> TierBronzeProgress
        Tier.SILVER -> TierSilverProgress
        Tier.GOLD -> PrimaryYellow
        Tier.PLATINUM -> TierPlatinumProgress
        Tier.DIAMOND -> Primary
    }
}

private fun Tier.toTierIconRes(): Int {
    return when (this) {
        Tier.BRONZE -> CommonR.drawable.ic_tier_bronze
        Tier.SILVER -> CommonR.drawable.ic_tier_silver
        Tier.GOLD -> CommonR.drawable.ic_tier_gold
        Tier.PLATINUM -> CommonR.drawable.ic_tier_platinum
        Tier.DIAMOND -> CommonR.drawable.ic_tier_diamond
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
                        MissionItemModel("1", "문라이트", "10월 러닝 인증", "mission_moonlight", true, PrimaryAmber),
                        MissionItemModel("2", "전력질주", "페이스 5'00\"", "mission_sprint", false, PrimaryAmber),
                        MissionItemModel("3", "밤톨 러닝", "1km", "mission_acorn", true, PrimaryAmber)
                    )
                )
            ),
            onEvent = {}
        )
    }
}
