package com.runnersHi

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** 첫 실행부터 홈 진입까지의 실제 화면 흐름 (Mock 로그인, Mock 건강 데이터). */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class LoginToHomeFlowTest {

    @get:Rule
    val rule = createEmptyComposeRule()

    @Before
    fun clearSession() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.dataDir.resolve("shared_prefs").listFiles()?.forEach { it.delete() }
    }

    @Test
    fun 로그인부터_약관_지역선택을_거쳐_홈에_도착한다() {
        ActivityScenario.launch(MainActivity::class.java).use {
            waitFor("카카오로 시작하기")
            rule.onNodeWithText("카카오로 시작하기").performClick()

            waitFor("약관에 모두 동의합니다.")
            rule.onNodeWithText("약관에 모두 동의합니다.").performClick()
            rule.onNodeWithText("다음").performClick()

            waitFor("러닝 지역을 골라주세요")
            rule.onNode(hasSetTextAction()).performTextInput("인천")
            waitFor("인천광역시")
            rule.onNodeWithText("인천광역시").performClick()

            waitFor("Today's Run")
            waitFor("7.4 km")
        }
    }

    private fun waitFor(text: String) {
        rule.waitUntilAtLeastOneExists(hasText(text, substring = true), timeoutMillis = 15_000)
    }
}
