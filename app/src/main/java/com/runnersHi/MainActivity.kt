package com.runnersHi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.runnersHi.presentation.common.model.RankChangeUiModel
import com.runnersHi.presentation.common.model.RankingItemUiModel
import com.runnersHi.presentation.common.model.UserUiModel
import com.runnersHi.presentation.common.navigation.RunnersHiBottomNavigation
import com.runnersHi.presentation.common.theme.Background
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import com.runnersHi.presentation.home.HomeScreen
import com.runnersHi.presentation.home.HomeUiState
import com.runnersHi.presentation.login.LoginScreen
import com.runnersHi.presentation.splash.ForceUpdateDialog
import com.runnersHi.presentation.splash.SplashScreen
import com.runnersHi.presentation.splash.SplashUiState
import com.runnersHi.presentation.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RunnersHiTheme {
                RunnersHiNavHost(
                    onKakaoLogin = { onTokenReceived ->
                        loginWithKakao(onTokenReceived)
                    },
                    onAppleLogin = { onTokenReceived ->
                        loginWithApple(onTokenReceived)
                    }
                )
            }
        }
    }

    private fun loginWithKakao(onTokenReceived: (String) -> Unit) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오 로그인 성공: ${token.accessToken}")
                onTokenReceived(token.accessToken)
            }
        }

        // 카카오톡 설치 여부 확인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            // 카카오톡으로 로그인
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공: ${token.accessToken}")
                    onTokenReceived(token.accessToken)
                }
            }
        } else {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun loginWithApple(onTokenReceived: (String) -> Unit) {
        val provider = OAuthProvider.newBuilder("apple.com")
            .setScopes(listOf("email", "name"))
            .build()

        val auth = FirebaseAuth.getInstance()

        // 이미 진행 중인 로그인이 있는지 확인
        val pending = auth.pendingAuthResult
        if (pending != null) {
            pending
                .addOnSuccessListener { result ->
                    Log.i(TAG, "Apple 로그인 성공 (pending)")
                    result.user?.getIdToken(true)?.addOnSuccessListener { tokenResult ->
                        tokenResult.token?.let { onTokenReceived(it) }
                    }
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Apple 로그인 실패 (pending)", error)
                }
        } else {
            auth.startActivityForSignInWithProvider(this, provider)
                .addOnSuccessListener { result ->
                    Log.i(TAG, "Apple 로그인 성공")
                    result.user?.getIdToken(true)?.addOnSuccessListener { tokenResult ->
                        tokenResult.token?.let { onTokenReceived(it) }
                    }
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Apple 로그인 실패", error)
                }
        }
    }
}

@Composable
fun RunnersHiNavHost(
    onKakaoLogin: (onTokenReceived: (String) -> Unit) -> Unit,
    onAppleLogin: (onTokenReceived: (String) -> Unit) -> Unit
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    val context = LocalContext.current

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            when {
                // Splash -> Home/Login: fade out splash, slide in next screen
                initialState == Screen.Splash -> {
                    (fadeIn(animationSpec = tween(300)) +
                            slideInHorizontally(
                                animationSpec = tween(300),
                                initialOffsetX = { fullWidth -> fullWidth }
                            )).togetherWith(
                        fadeOut(animationSpec = tween(300))
                    )
                }
                // Default transition
                else -> {
                    fadeIn(animationSpec = tween(300)).togetherWith(
                        fadeOut(animationSpec = tween(300))
                    )
                }
            }
        },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            is Screen.Splash -> {
                val viewModel: SplashViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    // 앱 버전 가져오기 (실제로는 BuildConfig 사용)
                    val currentVersion = "1.0.0"
                    viewModel.checkAppStatus(currentVersion)
                }

                SplashScreen(progress = uiState.progress)

                when (uiState) {
                    is SplashUiState.ForceUpdate -> {
                        ForceUpdateDialog(
                            onUpdateClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=${context.packageName}")
                                )
                                context.startActivity(intent)
                            }
                        )
                    }
                    is SplashUiState.NavigateToHome -> {
                        LaunchedEffect(Unit) {
                            currentScreen = Screen.Home
                        }
                    }
                    is SplashUiState.NavigateToLogin -> {
                        LaunchedEffect(Unit) {
                            currentScreen = Screen.Login
                        }
                    }
                    is SplashUiState.Error -> {
                        LaunchedEffect(Unit) {
                            // TODO: 에러 처리
                            currentScreen = Screen.Home
                        }
                    }
                    is SplashUiState.Loading -> {
                        // 로딩 중
                    }
                }
            }
            is Screen.Home -> {
                RunnersHiApp()
            }
            is Screen.Login -> {
                LoginScreen(
                    onLoginSuccess = {
                        currentScreen = Screen.Home
                    },
                    onKakaoLoginRequest = onKakaoLogin,
                    onAppleLoginRequest = onAppleLogin
                )
            }
        }
    }
}

sealed class Screen {
    data object Splash : Screen()
    data object Login : Screen()
    data object Home : Screen()
}

@Composable
fun RunnersHiApp() {
    var currentRoute by remember { mutableStateOf("home") }

    val mockRankings = remember {
        listOf(
            RankingItemUiModel(1, UserUiModel("1", "김러너", null, 156.5, 42), 156.5, RankChangeUiModel.NONE),
            RankingItemUiModel(2, UserUiModel("2", "박조깅", null, 142.3, 38), 142.3, RankChangeUiModel.UP),
            RankingItemUiModel(3, UserUiModel("3", "이마라톤", null, 128.7, 35), 128.7, RankChangeUiModel.DOWN),
            RankingItemUiModel(4, UserUiModel("4", "최달리기", null, 115.2, 30), 115.2, RankChangeUiModel.UP),
            RankingItemUiModel(5, UserUiModel("5", "정스프린트", null, 98.4, 28), 98.4, RankChangeUiModel.NONE)
        )
    }

    val homeUiState = remember {
        HomeUiState(
            currentUser = UserUiModel("0", "테스트유저", null, 45.2, 12),
            weeklyRanking = mockRankings,
            myRank = 8,
            myWeeklyDistance = 45.2
        )
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            RunnersHiBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route }
            )
        }
    ) { innerPadding ->
        when (currentRoute) {
            "home" -> HomeScreen(
                uiState = homeUiState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            else -> HomeScreen(
                uiState = homeUiState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}
