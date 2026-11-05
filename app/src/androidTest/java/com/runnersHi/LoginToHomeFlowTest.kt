package com.runnersHi

import android.view.KeyEvent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
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

            waitFor("다음")
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

    @Test
    fun 모든_탭과_지역변경_약관_로그아웃이_동작한다() {
        ActivityScenario.launch(MainActivity::class.java).use {
            waitFor("카카오로 시작하기")
            rule.onNodeWithText("카카오로 시작하기").performClick()

            // 약관 상세의 동의하기는 해당 약관만 체크하고 돌아온다.
            waitFor("다음")
            rule.onAllNodesWithContentDescription("상세보기")[1].performClick()
            waitFor("동의하기")
            rule.onNodeWithText("동의하기").performClick()
            waitFor("다음")
            rule.onNodeWithText("약관에 모두 동의합니다.").performClick()
            rule.onNodeWithText("다음").performClick()

            waitFor("러닝 지역을 골라주세요")
            rule.onNode(hasSetTextAction()).performTextInput("인천")
            waitFor("인천광역시")
            rule.onNodeWithText("인천광역시").performClick()
            waitFor("Today's Run")

            // 랭킹: 고른 지역과 내 순위
            rule.onNodeWithText("랭킹").performClick()
            waitFor("인천광역시")
            waitFor("가나다라마가나다라마가나다라마")
            waitFor("(나)")

            // 기록: 오늘 + 지난 기록
            rule.onNodeWithText("기록").performClick()
            waitFor("7.4 km")
            waitFor("Pace")

            // 미션
            rule.onNodeWithText("미션").performClick()
            waitFor("Welcome Mission")
            waitFor("보름달 챌린지")

            // 마이페이지: 프로필, 버전 (Figma대로 지역 이름은 랭킹에만 표시)
            rule.onNodeWithText("마이페이지").performClick()
            waitFor("잘먹는프로도")
            waitFor(BuildConfig.VERSION_NAME)

            // 지역을 바꾸면 랭킹에 바로 반영된다.
            rule.onNodeWithText("내 지역 설정").performClick()
            waitFor("러닝 지역을 골라주세요")
            // 시스템 뒤로가기는 앱을 끄지 않고 마이페이지로 돌아간다.
            InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
            waitFor("내 지역 설정")
            rule.onNodeWithText("내 지역 설정").performClick()
            waitFor("러닝 지역을 골라주세요")
            rule.onNode(hasSetTextAction()).performTextInput("강릉")
            waitFor("강릉시")
            rule.onNodeWithText("강릉시").performClick()
            waitFor("내 지역 설정")
            rule.onNodeWithText("랭킹").performClick()
            waitFor("강릉시")
            rule.onNodeWithText("마이페이지").performClick()
            waitFor("내 지역 설정")

            // 이용약관: 상세에는 동의 버튼이 없다.
            rule.onNodeWithText("이용약관").performScrollTo().performClick()
            waitFor("서비스 이용약관")
            rule.onAllNodesWithText("서비스 이용약관", substring = true).onFirst().performClick()
            rule.waitUntilAtLeastOneExists(hasContentDescription("뒤로가기"), 15_000)
            rule.onAllNodesWithText("동의하기").assertCountEquals(0)
            rule.onNodeWithContentDescription("뒤로가기").performClick()
            waitFor("이용약관")
            rule.onNodeWithContentDescription("뒤로가기").performClick()
            waitFor("내 지역 설정")

            // 로그아웃하면 가입 정보가 지워져 처음부터 다시 진행한다.
            rule.onNodeWithText("계정 관리").performScrollTo().performClick()
            waitFor("로그아웃")
            rule.onNodeWithText("로그아웃").performClick()
            waitFor("카카오로 시작하기")
            rule.onNodeWithText("카카오로 시작하기").assertIsDisplayed().performClick()
            waitFor("약관에 모두 동의합니다.")
        }
    }

    @Test
    fun 가입을_마치면_다시_실행해도_바로_홈으로_간다() {
        ActivityScenario.launch(MainActivity::class.java).use {
            waitFor("카카오로 시작하기")
            rule.onNodeWithText("카카오로 시작하기").performClick()
            waitFor("다음")
            rule.onNodeWithText("약관에 모두 동의합니다.").performClick()
            rule.onNodeWithText("다음").performClick()
            waitFor("러닝 지역을 골라주세요")
            rule.onNode(hasSetTextAction()).performTextInput("인천")
            waitFor("인천광역시")
            rule.onNodeWithText("인천광역시").performClick()
            waitFor("Today's Run")
        }
        ActivityScenario.launch(MainActivity::class.java).use {
            waitFor("Today's Run")
            rule.onAllNodesWithText("카카오로 시작하기").assertCountEquals(0)
        }
    }

    private fun waitFor(text: String) {
        rule.waitUntilAtLeastOneExists(hasText(text, substring = true), timeoutMillis = 15_000)
    }
}
