package com.runnersHi.presentation.main

import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.usecase.GetStoredTokenUseCase
import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.model.ExerciseType
import com.runnersHi.domain.health.model.HealthPermissionStatus
import com.runnersHi.domain.health.usecase.CheckHealthPermissionUseCase
import com.runnersHi.domain.health.usecase.GetTodayRunningDataUseCase
import com.runnersHi.domain.health.usecase.GetWeekRunningDataUseCase
import com.runnersHi.domain.home.model.HomeData
import com.runnersHi.domain.home.model.MissionEventData
import com.runnersHi.domain.home.model.ThisWeekData
import com.runnersHi.domain.home.model.Tier
import com.runnersHi.domain.home.model.TierInfo
import com.runnersHi.domain.home.model.TodaysRun
import com.runnersHi.domain.home.usecase.GetHomeDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val home = HomeData(
        tier = TierInfo(Tier.GOLD, "Gold", 1, 50),
        // 서버 값이 아니라 건강 기록으로만 계산하는지 확인하려고 큰 값을 넣는다.
        todaysRun = TodaysRun(99.0, 1, 1, 1, 1),
        thisWeek = ThisWeekData(99.0, emptyList()),
        missionEvent = MissionEventData(null, emptyList())
    )

    private val record = ExerciseRecord(
        id = "r1",
        startTime = Instant.now(),
        endTime = Instant.now(),
        exerciseType = ExerciseType.RUNNING,
        distanceMeters = 5_000.0,
        durationMillis = 25 * 60 * 1000L,
        calories = 300.0
    )

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(
        token: AuthToken? = AuthToken("access", "refresh", 3600),
        permission: HealthPermissionStatus = HealthPermissionStatus(true, true, false),
        records: List<ExerciseRecord> = listOf(record),
        onHomeToken: (String) -> Unit = {}
    ) = MainViewModel(
        getStoredTokenUseCase = object : GetStoredTokenUseCase {
            override suspend fun invoke() = token
        },
        getHomeDataUseCase = object : GetHomeDataUseCase {
            override suspend fun invoke(userToken: String): Result<HomeData> {
                onHomeToken(userToken)
                return Result.success(home)
            }
        },
        checkHealthPermissionUseCase = object : CheckHealthPermissionUseCase {
            override suspend fun invoke() = permission
        },
        getTodayRunningDataUseCase = object : GetTodayRunningDataUseCase {
            override suspend fun invoke() = Result.success(records)
        },
        getWeekRunningDataUseCase = object : GetWeekRunningDataUseCase {
            override suspend fun invoke() = Result.success(records)
        }
    )

    @Test
    fun `권한이 있으면 건강 기록으로 오늘 러닝을 계산한다`() {
        val state = viewModel().state.value

        assertEquals("5.0 km", state.todaysRun?.distance)
        assertEquals("5'00''", state.todaysRun?.pace)
        assertFalse(state.isTodayEmpty)
        assertFalse(state.isLoading)
    }

    @Test
    fun `기록이 없으면 서버 목업 대신 빈 상태를 보여준다`() {
        val state = viewModel(records = emptyList()).state.value

        assertNull(state.todaysRun)
        assertNull(state.thisWeek)
        assertTrue(state.isTodayEmpty)
        assertTrue(state.isWeekEmpty)
        assertNotNull(state.tierInfo)
    }

    @Test
    fun `저장된 토큰으로 홈 데이터를 요청한다`() {
        var requested: String? = null
        viewModel(onHomeToken = { requested = it })

        assertEquals("access", requested)
    }

    @Test
    fun `토큰이 없으면 오류 상태가 된다`() {
        val state = viewModel(token = null).state.value

        assertEquals("로그인 정보가 없습니다.", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun `권한 허용 결과를 받으면 기록을 다시 불러온다`() {
        val vm = viewModel(permission = HealthPermissionStatus(true, false, true))
        assertNull(vm.state.value.todaysRun)

        vm.sendEvent(MainContract.Event.HealthPermissionResult(granted = true))

        assertEquals("5.0 km", vm.state.value.todaysRun?.distance)
    }
}
