package com.runnersHi.presentation.launcher

import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.usecase.CheckLoginStatusUseCase
import com.runnersHi.domain.auth.usecase.LoginCheckResult
import com.runnersHi.domain.auth.usecase.LoginResult
import com.runnersHi.domain.auth.usecase.LoginWithSocialUseCase
import com.runnersHi.domain.region.model.Region
import com.runnersHi.domain.region.usecase.GetSelectedRegionUseCase
import com.runnersHi.domain.splash.usecase.CheckAppVersionUseCase
import com.runnersHi.domain.splash.usecase.VersionCheckResult
import com.runnersHi.domain.terms.usecase.HasAgreedTermsUseCase
import com.runnersHi.presentation.launcher.api.LauncherContract
import com.runnersHi.presentation.login.api.LoginContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LauncherViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(
        login: LoginCheckResult = LoginCheckResult.LoggedIn,
        loginResult: LoginResult = LoginResult.Success,
        agreed: Boolean = true,
        region: Region? = Region("1", "인천광역시"),
        version: () -> VersionCheckResult = { VersionCheckResult.UpToDate }
    ) = LauncherViewModel(
        checkAppVersionUseCase = object : CheckAppVersionUseCase {
            override suspend fun invoke(currentVersion: String) = version()
        },
        checkLoginStatusUseCase = object : CheckLoginStatusUseCase {
            override suspend fun invoke() = login
        },
        loginWithSocialUseCase = object : LoginWithSocialUseCase {
            override suspend fun invoke(type: SocialLoginType, token: String) =
                Result.success(loginResult)
        },
        hasAgreedTermsUseCase = object : HasAgreedTermsUseCase {
            override suspend fun invoke() = agreed
        },
        getSelectedRegionUseCase = object : GetSelectedRegionUseCase {
            override fun invoke() = flowOf(region)
        }
    )

    private suspend fun TestScope.effectAfterStart(vm: LauncherViewModel): LauncherContract.Effect {
        vm.sendEvent(LauncherContract.Event.CheckAppStatus("1.0.0"))
        advanceUntilIdle()
        return vm.effect.first()
    }

    @Test
    fun `로그인과 가입을 모두 마쳤으면 홈으로 간다`() = runTest(dispatcher) {
        assertEquals(LauncherContract.Effect.NavigateToHome, effectAfterStart(viewModel()))
    }

    @Test
    fun `약관에 동의하지 않았으면 약관 화면으로 간다`() = runTest(dispatcher) {
        assertEquals(
            LauncherContract.Effect.NavigateToTermsAgreement,
            effectAfterStart(viewModel(agreed = false))
        )
    }

    @Test
    fun `지역을 고르지 않았으면 지역 화면으로 간다`() = runTest(dispatcher) {
        assertEquals(
            LauncherContract.Effect.NavigateToRegionSelection,
            effectAfterStart(viewModel(region = null))
        )
    }

    @Test
    fun `버전 확인이 실패하면 이유를 보여주고 다시 시도하면 이어서 진행한다`() = runTest(dispatcher) {
        var result: VersionCheckResult = VersionCheckResult.Error(Exception("서버에 연결할 수 없어요"))
        val vm = viewModel(login = LoginCheckResult.NotLoggedIn, version = { result })

        vm.sendEvent(LauncherContract.Event.CheckAppStatus("1.0.0"))
        advanceUntilIdle()
        assertEquals("서버에 연결할 수 없어요", vm.state.value.splashState.errorMessage)
        assertEquals(LauncherContract.Phase.SPLASH, vm.state.value.phase)

        result = VersionCheckResult.UpToDate
        vm.sendEvent(LauncherContract.Event.CheckAppStatus("1.0.0"))
        advanceUntilIdle()

        assertEquals(null, vm.state.value.splashState.errorMessage)
        assertEquals(LauncherContract.Phase.LOGIN, vm.state.value.phase)
    }

    @Test
    fun `로그인 정보가 없으면 로그인 단계에 머문다`() = runTest(dispatcher) {
        val vm = viewModel(login = LoginCheckResult.NotLoggedIn)

        vm.sendEvent(LauncherContract.Event.CheckAppStatus("1.0.0"))
        advanceUntilIdle()

        assertEquals(LauncherContract.Phase.LOGIN, vm.state.value.phase)
    }

    @Test
    fun `기존 회원이 로그인해도 지역이 없으면 지역 화면으로 간다`() = runTest(dispatcher) {
        val vm = viewModel(region = null)

        vm.sendEvent(
            LauncherContract.Event.LoginEvent(LoginContract.Event.KakaoTokenReceived("token"))
        )
        advanceUntilIdle()

        assertEquals(LauncherContract.Effect.NavigateToRegionSelection, vm.effect.first())
    }

    @Test
    fun `신규 회원은 약관 화면으로 간다`() = runTest(dispatcher) {
        val vm = viewModel(loginResult = LoginResult.NewUser("u1"))

        vm.sendEvent(
            LauncherContract.Event.LoginEvent(LoginContract.Event.KakaoTokenReceived("token"))
        )
        advanceUntilIdle()

        assertEquals(LauncherContract.Effect.NavigateToTermsAgreement, vm.effect.first())
    }
}
