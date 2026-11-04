package com.runnersHi.domain.auth.usecase

/**
 * 로그아웃: 토큰과 사용자, 지역, 약관 동의 기록을 지운다.
 */
interface LogoutUseCase {
    suspend operator fun invoke()
}
