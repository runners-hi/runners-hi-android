package com.runnersHi.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.runnersHi.BuildConfig
import com.runnersHi.auth.AppleLoginHandler
import com.runnersHi.auth.KakaoLoginHandler
import com.runnersHi.health.HealthPermissionHandler
import com.runnersHi.presentation.common.navigation.BottomNavTab
import com.runnersHi.presentation.launcher.LauncherRoute
import com.runnersHi.presentation.login.LoginContentImpl
import com.runnersHi.presentation.main.MainContract
import com.runnersHi.presentation.main.MainRoute
import com.runnersHi.presentation.main.MainViewModel
import com.runnersHi.presentation.mission.MissionRoute
import com.runnersHi.presentation.mypage.AccountManagementRoute
import com.runnersHi.presentation.common.navigation.RunnersHiBottomNavigation
import com.runnersHi.presentation.mypage.MyPageRoute
import com.runnersHi.presentation.mypage.FeedbackRoute
import com.runnersHi.presentation.notice.NoticeDetailRoute
import com.runnersHi.presentation.notification.NotificationRoute
import com.runnersHi.presentation.notice.NoticeListRoute
import com.runnersHi.presentation.ranking.RankingRoute
import com.runnersHi.presentation.record.RecordRoute
import com.runnersHi.presentation.region.RegionSelectionRoute
import com.runnersHi.presentation.splash.SplashContentImpl
import com.runnersHi.presentation.terms.TermsAgreementRoute
import com.runnersHi.presentation.terms.TermsListRoute
import kotlinx.coroutines.launch

/**
 * 앱 전체 네비게이션 호스트
 */
@Composable
fun RunnersHiNavHost(
    navigator: AppNavigatorViewModel = viewModel(),
    onMainEntered: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val stateHolder = rememberSaveableStateHolder()
    val sessionViewModel: SessionViewModel = hiltViewModel()

    DisposableEffect(navigator) {
        navigator.onScreenDropped = { stateHolder.removeState(it.toString()) }
        onDispose { navigator.onScreenDropped = null }
    }

    // 토큰이 완전히 만료되면 어느 화면에 있든 로그인으로 돌아간다.
    LaunchedEffect(Unit) {
        sessionViewModel.expired.collect {
            runCatching { KakaoLoginHandler.logout() }
            navigator.resetTo(Screen.Launcher)
        }
    }

    AnimatedContent(
        targetState = navigator.current,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)).togetherWith(
                fadeOut(animationSpec = tween(300))
            )
        },
        label = "screen_transition"
    ) { screen ->
        // 스택에 남은 화면은 돌아왔을 때 스크롤 위치를 유지하고, 빠진 화면은 지운다.
        DisposableEffect(screen) {
            onDispose {
                if (!navigator.isInStack(screen)) stateHolder.removeState(screen.toString())
            }
        }
        stateHolder.SaveableStateProvider(screen.toString()) {
            ScreenViewModelScope(navigator, screen) {
                when (screen) {
                    Screen.Launcher -> LauncherRoute(
                        splashContent = SplashContentImpl,
                        loginContent = LoginContentImpl,
                        currentVersion = BuildConfig.VERSION_NAME,
                        onNavigateToHome = { navigator.resetTo(Screen.Main) },
                        onNavigateToTermsAgreement = { navigator.resetTo(Screen.TermsAgreement) },
                        onNavigateToRegionSelection = { navigator.resetTo(Screen.RegionSelection) },
                        onKakaoLogin = { context -> KakaoLoginHandler.login(context) },
                        onAppleLogin = if (AppleLoginHandler.isAvailable) {
                            { context -> AppleLoginHandler.login(context) }
                        } else {
                            null
                        }
                    )
                    Screen.TermsAgreement -> TermsAgreementRoute(
                        onNavigateToMain = { navigator.push(Screen.RegionSelection) },
                        onNavigateBack = {
                            scope.launch {
                                runCatching { KakaoLoginHandler.logout() }
                                navigator.resetTo(Screen.Launcher)
                            }
                        }
                    )
                    Screen.RegionSelection -> RegionSelectionRoute(
                        onNavigateToMain = { navigator.resetTo(Screen.Main) },
                        onNavigateBack = {
                            if (!navigator.pop()) navigator.resetTo(Screen.TermsAgreement)
                        }
                    )
                    Screen.Main -> MainBlock(navigator, onMainEntered)
                    Screen.RegionChange -> RegionSelectionRoute(
                        onNavigateToMain = { navigator.pop() },
                        onNavigateBack = { navigator.pop() }
                    )
                    Screen.TermsList -> TermsListRoute(onBack = { navigator.pop() })
                    Screen.AccountManagement -> AccountManagementRoute(
                        onBack = { navigator.pop() },
                        // 아이폰: 계정 관리는 마이페이지 탭 안에서 열려 아래 탭바가 그대로 보인다
                        bottomBar = {
                            RunnersHiBottomNavigation(
                                currentTab = BottomNavTab.MY_PAGE,
                                onTabSelected = { tab ->
                                    navigator.pendingTab = tab
                                    navigator.pop()
                                }
                            )
                        },
                        onLoggedOut = {
                            scope.launch {
                                runCatching { KakaoLoginHandler.logout() }
                                navigator.resetTo(Screen.Launcher)
                            }
                        },
                        onWithdrawn = {
                            scope.launch {
                                // 카카오 연결이 이미 끊겼거나 Apple 계정이어도 탈퇴는 끝난 상태다.
                                runCatching { KakaoLoginHandler.unlink() }
                                navigator.resetTo(Screen.Launcher)
                            }
                        }
                    )
                    Screen.Notifications -> NotificationRoute(onBack = { navigator.pop() })
                    Screen.Feedback -> FeedbackRoute(onBack = { navigator.pop() })
                    Screen.NoticeList -> NoticeListRoute(
                        onBack = { navigator.pop() },
                        onNoticeClick = { id -> navigator.push(Screen.NoticeDetail(id)) }
                    )
                    is Screen.NoticeDetail -> NoticeDetailRoute(
                        noticeId = screen.id,
                        onBack = { navigator.pop() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainBlock(navigator: AppNavigatorViewModel, onMainEntered: () -> Unit) {
    val context = LocalContext.current
    val mainViewModel: MainViewModel = hiltViewModel()

    val healthPermissionLauncher = rememberLauncherForActivityResult(
        contract = HealthPermissionHandler.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        val allGranted = HealthPermissionHandler.REQUIRED_PERMISSIONS.all {
            it in grantedPermissions
        }
        mainViewModel.sendEvent(MainContract.Event.HealthPermissionResult(allGranted))
    }
    val requestHealthPermission = {
        healthPermissionLauncher.launch(HealthPermissionHandler.REQUIRED_PERMISSIONS)
    }
    val startActivitySafely = { intent: android.content.Intent ->
        // 기기에 해당 화면이 없을 수 있다.
        runCatching { context.startActivity(intent) }
        Unit
    }

    LaunchedEffect(Unit) { onMainEntered() }
    LaunchedEffect(navigator.pendingTab) {
        navigator.pendingTab?.let {
            mainViewModel.sendEvent(MainContract.Event.BottomNavClicked(it))
            navigator.pendingTab = null
        }
    }

    MainRoute(
        viewModel = mainViewModel,
        onNavigateToNotifications = { navigator.push(Screen.Notifications) },
        onRequestHealthPermission = requestHealthPermission,
        onOpenHealthConnectSettings = {
            startActivitySafely(HealthPermissionHandler.getHealthConnectSettingsIntent())
        },
        tabContent = { tab, modifier ->
            when (tab) {
                BottomNavTab.HOME -> Unit
                BottomNavTab.RANKING -> RankingRoute(modifier = modifier)
                BottomNavTab.RECORD -> RecordRoute(
                    onRequestHealthPermission = requestHealthPermission,
                    onOpenHealthConnectInstall = {
                        startActivitySafely(HealthPermissionHandler.getPlayStoreIntent())
                    },
                    modifier = modifier
                )
                BottomNavTab.MISSION -> MissionRoute(modifier = modifier)
                BottomNavTab.MY_PAGE -> MyPageRoute(
                    versionName = BuildConfig.VERSION_NAME,
                    onNavigateToRegion = { navigator.push(Screen.RegionChange) },
                    onNavigateToTerms = { navigator.push(Screen.TermsList) },
                    onNavigateToAccount = { navigator.push(Screen.AccountManagement) },
                    onNavigateToNotices = { navigator.push(Screen.NoticeList) },
                    onNavigateToNotifications = { navigator.push(Screen.Notifications) },
                    onNavigateToFeedback = { navigator.push(Screen.Feedback) },
                    modifier = modifier
                )
            }
        }
    )
}

/**
 * 앱 최상위 화면
 */
sealed class Screen {
    data object Launcher : Screen()          // 스플래시 + 로그인 (통합)
    data object TermsAgreement : Screen()    // 신규 유저 이용약관 동의
    data object RegionSelection : Screen()   // 가입 중 러닝 지역 선택
    data object Main : Screen()
    data object RegionChange : Screen()      // 마이페이지 > 내 지역 설정
    data object TermsList : Screen()         // 마이페이지 > 이용약관
    data object AccountManagement : Screen() // 마이페이지 > 계정 관리
    data object Notifications : Screen()     // 홈 종 아이콘, 마이페이지 > 알림
    data object Feedback : Screen()          // 마이페이지 > 개발자에게
    data object NoticeList : Screen()        // 마이페이지 > 공지사항
    data class NoticeDetail(val id: String) : Screen()
}
