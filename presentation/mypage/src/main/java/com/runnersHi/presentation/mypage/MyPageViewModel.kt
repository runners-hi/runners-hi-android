package com.runnersHi.presentation.mypage

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.region.usecase.GetSelectedRegionUseCase
import com.runnersHi.domain.user.usecase.GetCurrentUserUseCase
import com.runnersHi.domain.user.usecase.RemoveProfileImageUseCase
import com.runnersHi.domain.user.usecase.UpdateProfileImageUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getSelectedRegionUseCase: GetSelectedRegionUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
    private val removeProfileImageUseCase: RemoveProfileImageUseCase
) : MviViewModel<MyPageContract.State, MyPageContract.Event, MyPageContract.Effect>(
    MyPageContract.State()
) {

    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                updateState {
                    copy(
                        nickname = user?.name ?: MyPageContract.DEFAULT_NICKNAME,
                        profileImageUrl = user?.profileImageUrl
                    )
                }
            }
        }
        viewModelScope.launch {
            getSelectedRegionUseCase().collect { region ->
                updateState { copy(regionName = region?.name) }
            }
        }
    }

    override fun handleEvent(event: MyPageContract.Event) {
        when (event) {
            MyPageContract.Event.RegionClicked -> sendEffect(MyPageContract.Effect.NavigateToRegion)
            MyPageContract.Event.TermsClicked -> sendEffect(MyPageContract.Effect.NavigateToTerms)
            MyPageContract.Event.AccountClicked -> sendEffect(MyPageContract.Effect.NavigateToAccount)
            MyPageContract.Event.NoticesClicked -> sendEffect(MyPageContract.Effect.NavigateToNotices)
            MyPageContract.Event.NotificationsClicked ->
                sendEffect(MyPageContract.Effect.NavigateToNotifications)
            MyPageContract.Event.FeedbackClicked -> sendEffect(MyPageContract.Effect.NavigateToFeedback)
            MyPageContract.Event.AvatarClicked -> onAvatarClicked()
            MyPageContract.Event.PhotoMenuDismissed -> updateState { copy(showPhotoMenu = false) }
            MyPageContract.Event.ChangePhotoClicked -> {
                updateState { copy(showPhotoMenu = false) }
                sendEffect(MyPageContract.Effect.OpenPhotoPicker)
            }
            MyPageContract.Event.RemovePhotoClicked -> {
                updateState { copy(showPhotoMenu = false) }
                runPhotoChange("사진을 삭제하지 못했어요") { removeProfileImageUseCase() }
            }
            is MyPageContract.Event.PhotoEncoded ->
                runPhotoChange("사진을 바꾸지 못했어요") { updateProfileImageUseCase(event.jpegBytes) }
            MyPageContract.Event.PhotoEncodeFailed ->
                sendEffect(MyPageContract.Effect.ShowToast("이 사진은 쓸 수 없어요"))
        }
    }

    private fun onAvatarClicked() {
        if (currentState.isUpdatingPhoto) return
        if (currentState.profileImageUrl == null) {
            sendEffect(MyPageContract.Effect.OpenPhotoPicker)
        } else {
            updateState { copy(showPhotoMenu = true) }
        }
    }

    private fun runPhotoChange(failureMessage: String, change: suspend () -> Result<Unit>) {
        if (currentState.isUpdatingPhoto) return
        updateState { copy(isUpdatingPhoto = true) }
        viewModelScope.launch {
            val result = change()
            updateState { copy(isUpdatingPhoto = false) }
            result.onFailure { error ->
                sendEffect(MyPageContract.Effect.ShowToast(error.message ?: failureMessage))
            }
        }
    }
}
