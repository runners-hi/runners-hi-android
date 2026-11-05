package com.runnersHi.presentation.notice

import com.runnersHi.domain.notice.model.Notice
import com.runnersHi.domain.notice.model.NoticePage
import com.runnersHi.domain.notice.usecase.GetNoticesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class NoticeListViewModelTest {

    private fun notice(id: String) = Notice(id, "공지 $id", LocalDate.of(2026, 11, 1))

    private fun viewModel(block: (String?) -> Result<NoticePage>) = NoticeListViewModel(
        object : GetNoticesUseCase {
            override suspend fun invoke(cursor: String?) = block(cursor)
        }
    )

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `다음 페이지를 이어 붙인다`() {
        val vm = viewModel { cursor ->
            Result.success(
                if (cursor == null) NoticePage(listOf(notice("2")), "2") else NoticePage(listOf(notice("1")), null)
            )
        }

        vm.sendEvent(NoticeListContract.Event.LoadMore)

        assertEquals(listOf("2", "1"), vm.state.value.notices.map { it.id })
        assertNull(vm.state.value.nextCursor)
    }

    @Test
    fun `다음 페이지가 실패해도 목록을 유지한다`() {
        val vm = viewModel { cursor ->
            if (cursor == null) Result.success(NoticePage(listOf(notice("2")), "2"))
            else Result.failure(IllegalStateException("x"))
        }

        vm.sendEvent(NoticeListContract.Event.LoadMore)

        assertEquals(1, vm.state.value.notices.size)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun `첫 페이지 실패는 오류를 보이고 재시도할 수 있다`() {
        var fail = true
        val vm = viewModel {
            if (fail) Result.failure(IllegalStateException("서버 오류"))
            else Result.success(NoticePage(emptyList(), null))
        }
        assertEquals("서버 오류", vm.state.value.errorMessage)

        fail = false
        vm.sendEvent(NoticeListContract.Event.Retry)

        assertNull(vm.state.value.errorMessage)
        assertTrue(vm.state.value.notices.isEmpty())
    }
}
