package com.runnersHi.domain.auth

import com.runnersHi.domain.auth.model.AppleCredential
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppleCredentialTest {

    @Test
    fun `토큰 문자열로 합쳤다가 되돌린다`() {
        val credential = AppleCredential("a.b.c", "code")

        assertEquals(credential, AppleCredential.fromSocialToken(credential.toSocialToken()))
    }

    @Test
    fun `형식이 다르면 읽지 않는다`() {
        assertNull(AppleCredential.fromSocialToken("mock_apple_id_token_1"))
        assertNull(AppleCredential.fromSocialToken("|code"))
    }
}
