package com.runnersHi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * 뒤로가기 스택과 화면별 ViewModelStore를 관리한다.
 * 화면이 스택에서 빠지면 그 화면의 ViewModel도 함께 지운다.
 */
class AppNavigatorViewModel : ViewModel() {

    private val stack = mutableStateListOf<Screen>(Screen.Launcher)
    private val stores = mutableMapOf<Screen, ViewModelStore>()

    val current: Screen get() = stack.last()
    val canPop: Boolean get() = stack.size > 1

    fun push(screen: Screen) {
        if (current != screen) stack.add(screen)
    }

    /** @return 스택에 돌아갈 화면이 없으면 false */
    fun pop(): Boolean {
        if (!canPop) return false
        stack.removeAt(stack.lastIndex)
        return true
    }

    fun resetTo(screen: Screen) {
        stack.clear()
        stack.add(screen)
    }

    fun isInStack(screen: Screen): Boolean = screen in stack

    fun storeFor(screen: Screen): ViewModelStore = stores.getOrPut(screen) { ViewModelStore() }

    fun clearIfRemoved(screen: Screen) {
        if (screen !in stack) stores.remove(screen)?.clear()
    }

    override fun onCleared() {
        stores.values.forEach { it.clear() }
        stores.clear()
    }
}

private class ScreenViewModelOwner(
    override val viewModelStore: ViewModelStore,
    private val parent: HasDefaultViewModelProviderFactory
) : ViewModelStoreOwner, HasDefaultViewModelProviderFactory {
    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = parent.defaultViewModelProviderFactory
    override val defaultViewModelCreationExtras: CreationExtras
        get() = parent.defaultViewModelCreationExtras
}

/** hiltViewModel()이 화면별 저장소를 쓰도록 감싼다. */
@Composable
fun ScreenViewModelScope(
    navigator: AppNavigatorViewModel,
    screen: Screen,
    content: @Composable () -> Unit
) {
    val parent = checkNotNull(LocalViewModelStoreOwner.current) as HasDefaultViewModelProviderFactory
    val owner = remember(screen) { ScreenViewModelOwner(navigator.storeFor(screen), parent) }

    // 전환 애니메이션이 끝나 화면이 사라질 때 스택에 없으면 정리한다.
    DisposableEffect(screen) {
        onDispose { navigator.clearIfRemoved(screen) }
    }

    CompositionLocalProvider(LocalViewModelStoreOwner provides owner, content = content)
}
