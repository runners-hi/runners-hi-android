package com.runnersHi.presentation.mypage

import com.runnersHi.domain.user.usecase.SendFeedbackUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedbackViewModelTest {

    private val sent = mutableListOf<String>()
    private var result: Result<Unit> = Result.success(Unit)

    private val viewModel by lazy {
        FeedbackViewModel(object : SendFeedbackUseCase {
            override suspend fun invoke(content: String): Result<Unit> {
                sent += content
                return result
            }
        })
    }

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `빈 내용은 보내지 않는다`() {
        viewModel.sendEvent(FeedbackContract.Event.ContentChanged("   "))
        viewModel.sendEvent(FeedbackContract.Event.SendClicked)

        assertFalse(viewModel.state.value.canSend)
        assertTrue(sent.isEmpty())
    }

    @Test
    fun `글자 수 상한을 넘으면 잘라낸다`() {
        viewModel.sendEvent(FeedbackContract.Event.ContentChanged("가".repeat(1200)))

        assertEquals(SendFeedbackUseCase.MAX_LENGTH, viewModel.state.value.content.length)
    }

    @Test
    fun `보내기에 성공하면 안내하고 닫는다`() = runBlocking {
        viewModel.sendEvent(FeedbackContract.Event.ContentChanged("좋아요"))
        viewModel.sendEvent(FeedbackContract.Event.SendClicked)

        assertEquals(listOf("좋아요"), sent)
        assertTrue(viewModel.effect.first() is FeedbackContract.Effect.ShowToast)
        assertEquals(FeedbackContract.Effect.Close, viewModel.effect.first())
    }

    @Test
    fun `실패하면 내용을 남기고 다시 보낼 수 있다`() {
        result = Result.failure(IllegalStateException("실패"))
        viewModel.sendEvent(FeedbackContract.Event.ContentChanged("좋아요"))
        viewModel.sendEvent(FeedbackContract.Event.SendClicked)

        assertEquals("좋아요", viewModel.state.value.content)
        assertTrue(viewModel.state.value.canSend)
    }
}
