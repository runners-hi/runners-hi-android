package com.runnersHi.auth

import android.content.Context
import kotlinx.coroutines.delay

/**
 * Apple Sign In 핸들러
 *
 * TODO: 실제 Apple Sign In 연동 시 구현
 * 현재는 Mock 모드로 동작
 */
object AppleLoginHandler {

    // Mock 모드 플래그 (실제 연동 시 false로 변경)
    private const val USE_MOCK = true

    /**
     * Apple Sign In 실행
     * @return Apple ID 토큰
     */
    suspend fun login(context: Context): String {
        if (USE_MOCK) {
            // Mock: 로그인 지연 시뮬레이션
            delay(1500)
            return "mock_apple_id_token_${System.currentTimeMillis()}"
        }

        // TODO: 실제 Apple Sign In 구현
        // Apple Developer 콘솔에서 Service ID 설정 필요
        // 1. App ID 생성 및 Sign In with Apple 활성화
        // 2. Service ID 생성
        // 3. 키 생성 및 다운로드
        // 4. 서버 사이드 검증 로직 구현
        throw NotImplementedError("Apple Sign In not configured")
    }
}
