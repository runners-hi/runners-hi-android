package com.runnersHi.presentation.mypage

import com.runnersHi.domain.region.model.Region
import com.runnersHi.domain.region.usecase.GetSelectedRegionUseCase
import com.runnersHi.domain.user.model.User
import com.runnersHi.domain.user.usecase.GetCurrentUserUseCase
import com.runnersHi.domain.user.usecase.RemoveProfileImageUseCase
import com.runnersHi.domain.user.usecase.UpdateProfileImageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPageViewModelTest {

    private val user = MutableStateFlow<User?>(null)
    private var uploadedBytes: ByteArray? = null
    private var uploadResult: Result<Unit> = Result.success(Unit)
    private var removeCount = 0

    private val viewModel by lazy {
        MyPageViewModel(
            object : GetCurrentUserUseCase {
                override fun invoke(): Flow<User?> = user
            },
            object : GetSelectedRegionUseCase {
                override fun invoke(): Flow<Region?> = flowOf(null)
            },
            object : UpdateProfileImageUseCase {
                override suspend fun invoke(jpegBytes: ByteArray): Result<Unit> {
                    uploadedBytes = jpegBytes
                    return uploadResult
                }
            },
            object : RemoveProfileImageUseCase {
                override suspend fun invoke(): Result<Unit> {
                    removeCount++
                    return Result.success(Unit)
                }
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

    @Test
    fun `사진이 없으면 아바타를 눌렀을 때 메뉴 없이 사진 선택을 연다`() = runBlocking {
        viewModel.sendEvent(MyPageContract.Event.AvatarClicked)

        assertFalse(viewModel.state.value.showPhotoMenu)
        assertEquals(MyPageContract.Effect.OpenPhotoPicker, viewModel.effect.first())
    }

    @Test
    fun `사진이 있으면 메뉴를 열고 삭제를 고르면 지운다`() {
        user.value = User("1", "달리미", "https://img.test/me.png", 0.0, 0)
        viewModel.sendEvent(MyPageContract.Event.AvatarClicked)
        assertTrue(viewModel.state.value.showPhotoMenu)

        viewModel.sendEvent(MyPageContract.Event.RemovePhotoClicked)

        assertFalse(viewModel.state.value.showPhotoMenu)
        assertEquals(1, removeCount)
    }

    @Test
    fun `인코딩한 사진을 올리고 실패하면 안내한다`() = runBlocking {
        uploadResult = Result.failure(IllegalStateException("업로드 실패"))
        val bytes = byteArrayOf(1, 2)

        viewModel.sendEvent(MyPageContract.Event.PhotoEncoded(bytes))

        assertEquals(bytes, uploadedBytes)
        assertFalse(viewModel.state.value.isUpdatingPhoto)
        assertEquals(MyPageContract.Effect.ShowToast("업로드 실패"), viewModel.effect.first())
    }
}
