package com.runnersHi.presentation.mypage

import androidx.compose.runtime.Immutable
import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object MyPageContract {

    @Immutable
    data class State(
        val nickname: String = DEFAULT_NICKNAME,
        val profileImageUrl: String? = null,
        val regionName: String? = null,
        val isUpdatingPhoto: Boolean = false,
        val showPhotoMenu: Boolean = false
    ) : UiState

    sealed interface Event : UiEvent {
        data object RegionClicked : Event
        data object TermsClicked : Event
        data object AccountClicked : Event
        data object NoticesClicked : Event
        data object NotificationsClicked : Event
        data object FeedbackClicked : Event
        data object AvatarClicked : Event
        data object PhotoMenuDismissed : Event
        data object ChangePhotoClicked : Event
        data object RemovePhotoClicked : Event
        data class PhotoEncoded(val jpegBytes: ByteArray) : Event {
            override fun equals(other: Any?): Boolean = this === other
            override fun hashCode(): Int = System.identityHashCode(this)
        }
        data object PhotoEncodeFailed : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToRegion : Effect
        data object NavigateToTerms : Effect
        data object NavigateToAccount : Effect
        data object NavigateToNotices : Effect
        data object NavigateToNotifications : Effect
        data object NavigateToFeedback : Effect
        data object OpenPhotoPicker : Effect
        data class ShowToast(val message: String) : Effect
    }

    const val DEFAULT_NICKNAME = "러너"
}
