package com.runnersHi.presentation.mypage

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.region.usecase.GetSelectedRegionUseCase
import com.runnersHi.domain.user.usecase.GetCurrentUserUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getSelectedRegionUseCase: GetSelectedRegionUseCase
) : MviViewModel<MyPageContract.State, MyPageContract.Event, MyPageContract.Effect>(
    MyPageContract.State()
) {

    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                updateState { copy(nickname = user?.name ?: MyPageContract.DEFAULT_NICKNAME) }
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
            MyPageContract.Event.NotReadyClicked ->
                sendEffect(MyPageContract.Effect.ShowToast("준비 중인 기능이에요"))
        }
    }
}
