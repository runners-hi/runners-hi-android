package com.runnersHi.presentation.mypage

import com.runnersHi.domain.region.model.Region
import com.runnersHi.domain.region.usecase.GetSelectedRegionUseCase
import com.runnersHi.domain.user.model.User
import com.runnersHi.domain.user.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPageViewModelTest {

    private val user = MutableStateFlow<User?>(null)

    private val viewModel by lazy {
        MyPageViewModel(
            object : GetCurrentUserUseCase {
                override fun invoke(): Flow<User?> = user
            },
            object : GetSelectedRegionUseCase {
                override fun invoke(): Flow<Region?> = flowOf(null)
            }
        )
    }

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `사용자의 프로필 사진 주소를 상태에 담는다`() {
        user.value = User("1", "달리미", "https://img.test/me.png", 0.0, 0)

        assertEquals("달리미", viewModel.state.value.nickname)
        assertEquals("https://img.test/me.png", viewModel.state.value.profileImageUrl)
    }

    @Test
    fun `로그아웃하면 사진과 이름을 기본값으로 되돌린다`() {
        user.value = User("1", "달리미", "https://img.test/me.png", 0.0, 0)
        viewModel.state.value

        user.value = null

        assertEquals(MyPageContract.DEFAULT_NICKNAME, viewModel.state.value.nickname)
        assertNull(viewModel.state.value.profileImageUrl)
    }
}
