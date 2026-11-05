package com.runnersHi.presentation.notification

import com.runnersHi.domain.notification.model.AppNotification
import com.runnersHi.domain.notification.usecase.GetNotificationsUseCase
import com.runnersHi.domain.notification.usecase.GetPushEnabledUseCase
import com.runnersHi.domain.notification.usecase.MarkNotificationsReadUseCase
import com.runnersHi.domain.notification.usecase.SetPushEnabledUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    private var items: Result<List<AppNotification>> = Result.success(emptyList())
    private var markReadCount = 0
    private var setResult: Result<Unit> = Result.success(Unit)

    private val viewModel by lazy {
        NotificationViewModel(
            object : GetNotificationsUseCase {
                override suspend fun invoke() = items
            },
            object : MarkNotificationsReadUseCase {
                override suspend fun invoke(): Result<Unit> {
                    markReadCount++
                    return Result.success(Unit)
                }
            },
            object : GetPushEnabledUseCase {
                override suspend fun invoke() = Result.success(true)
            },
            object : SetPushEnabledUseCase {
                override suspend fun invoke(enabled: Boolean) = setResult
            }
        )
    }

    private fun item(isRead: Boolean) =
        AppNotification("1", "미션 달성", "첫 러닝 완료", isRead, LocalDateTime.of(2026, 11, 5, 9, 0))

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `안 읽은 알림이 있으면 화면 표시는 두고 서버에 읽음 처리한다`() {
        items = Result.success(listOf(item(isRead = false)))

        val state = viewModel.state.value

        assertFalse(state.notifications.single().isRead)
        assertEquals(1, markReadCount)
    }

    @Test
    fun `모두 읽었으면 읽음 요청을 보내지 않는다`() {
        items = Result.success(listOf(item(isRead = true)))

        viewModel.state.value

        assertEquals(0, markReadCount)
    }

    @Test
    fun `불러오기에 실패하면 오류를 보여주고 다시 시도할 수 있다`() {
        items = Result.failure(IllegalStateException("실패"))
        assertEquals("실패", viewModel.state.value.errorMessage)

        items = Result.success(emptyList())
        viewModel.sendEvent(NotificationContract.Event.Retry)

        assertEquals(null, viewModel.state.value.errorMessage)
    }

    @Test
    fun `설정 변경에 실패하면 이전 값으로 되돌린다`() {
        setResult = Result.failure(IllegalStateException("실패"))
        assertTrue(viewModel.state.value.pushEnabled)

        viewModel.sendEvent(NotificationContract.Event.PushToggled(false))

        assertTrue(viewModel.state.value.pushEnabled)
    }
}
