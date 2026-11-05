package com.runnersHi.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test

class AppNavigatorViewModelTest {

    @Test
    fun `로그아웃으로 스택을 비우면 밑에 깔린 화면의 상태를 지운다`() {
        val navigator = AppNavigatorViewModel()
        val dropped = mutableListOf<Screen>()
        navigator.onScreenDropped = { dropped += it }
        navigator.resetTo(Screen.Main)
        navigator.push(Screen.AccountManagement)
        val mainStore = navigator.storeFor(Screen.Main)

        navigator.resetTo(Screen.Launcher)

        assertEquals(listOf<Screen>(Screen.Main), dropped)
        assertNotSame(mainStore, navigator.storeFor(Screen.Main))
    }

    @Test
    fun `지금 보이는 화면은 사라질 때 정리하므로 남겨 둔다`() {
        val navigator = AppNavigatorViewModel()
        navigator.push(Screen.Main)
        val mainStore = navigator.storeFor(Screen.Main)

        navigator.resetTo(Screen.RegionSelection)

        assertSame(mainStore, navigator.storeFor(Screen.Main))
    }
}
