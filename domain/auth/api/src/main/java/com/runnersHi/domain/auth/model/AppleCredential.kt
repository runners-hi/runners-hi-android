package com.runnersHi.domain.auth.model

/** Apple 로그인 결과. 소셜 토큰 문자열 하나로 전달하기 위해 합쳐서 넘긴다. */
data class AppleCredential(
    val idToken: String,
    val authorizationCode: String
) {
    fun toSocialToken(): String = "$idToken$SEPARATOR$authorizationCode"

    companion object {
        private const val SEPARATOR = "|"

        fun fromSocialToken(token: String): AppleCredential? {
            val parts = token.split(SEPARATOR)
            if (parts.size != 2 || parts.any { it.isBlank() }) return null
            return AppleCredential(parts[0], parts[1])
        }
    }
}

/** 사용자가 소셜 로그인 화면을 닫았다. 오류로 표시하지 않는다. */
class SocialLoginCancelledException : Exception("로그인을 취소했어요")
