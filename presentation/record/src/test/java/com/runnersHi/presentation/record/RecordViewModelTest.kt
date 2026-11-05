package com.runnersHi.presentation.record

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.model.ExerciseType
import com.runnersHi.domain.health.model.HealthPermissionStatus
import com.runnersHi.domain.health.usecase.CheckHealthPermissionUseCase
import com.runnersHi.domain.health.usecase.GetRunningRecordsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class RecordViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun record(id: String, start: Instant) = ExerciseRecord(
        id = id,
        startTime = start,
        endTime = start,
        exerciseType = ExerciseType.RUNNING,
        distanceMeters = 5_000.0,
        durationMillis = 25 * 60_000L,
        calories = 300.0
    )

    private fun loaded(
        permission: HealthPermissionStatus = HealthPermissionStatus(true, true, false),
        records: Result<List<ExerciseRecord>> = Result.success(emptyList())
    ): RecordContract.State {
        val vm = RecordViewModel(
            checkHealthPermissionUseCase = object : CheckHealthPermissionUseCase {
                override suspend fun invoke() = permission
            },
            getRunningRecordsUseCase = object : GetRunningRecordsUseCase {
                override suspend fun invoke() = records
            }
        )
        vm.sendEvent(RecordContract.Event.Refresh)
        return vm.state.value
    }

    @Test
    fun `오늘 기록은 합쳐서 위 카드로, 지난 기록은 목록으로 나눈다`() {
        val now = Instant.now()
        val old = now.minus(Duration.ofDays(3))

        val state = loaded(records = Result.success(listOf(record("a", now), record("b", now), record("c", old))))

        assertEquals("10.0 km", state.today?.distance)
        assertEquals("5’00’’", state.today?.pace)
        assertEquals(listOf("c"), state.past.map { it.id })
    }

    @Test
    fun `기록이 없으면 오늘 카드는 빈 상태다`() {
        val state = loaded()

        assertNull(state.today)
        assertEquals(emptyList<PastRecordUiModel>(), state.past)
    }

    @Test
    fun `Health Connect가 없으면 설치 안내를 보여준다`() {
        val state = loaded(permission = HealthPermissionStatus(false, false, false))

        assertEquals(RecordContract.Access.NEEDS_INSTALL, state.access)
    }

    @Test
    fun `권한이 없으면 권한 안내를 보여준다`() {
        val state = loaded(permission = HealthPermissionStatus(true, false, true))

        assertEquals(RecordContract.Access.NEEDS_PERMISSION, state.access)
    }

    @Test
    fun `기록을 못 읽으면 오류를 보여준다`() {
        val state = loaded(records = Result.failure(IllegalStateException("읽기 실패")))

        assertEquals("읽기 실패", state.errorMessage)
    }
}
