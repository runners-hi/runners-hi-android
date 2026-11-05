package com.runnersHi.domain.auth.usecase

/**
 * 회원 탈퇴: 서버 계정을 지우고 성공하면 기기 데이터도 지운다.
 */
interface WithdrawUseCase {
    suspend operator fun invoke(): Result<Unit>
}
