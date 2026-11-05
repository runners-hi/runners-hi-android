package com.runnersHi.domain.auth.model

enum class SocialLoginType {
    KAKAO,
    APPLE,

    /** 스토어 심사자용. token 자리에 심사 코드를 넣는다 */
    REVIEW
}
