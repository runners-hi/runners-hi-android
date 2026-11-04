package com.runnersHi.data.terms

import android.content.Context

class PrefsTermsLocalDataSource(context: Context) : TermsLocalDataSource {

    private val prefs = context.getSharedPreferences("runnershi_terms", Context.MODE_PRIVATE)

    override suspend fun hasAgreed(): Boolean = prefs.getBoolean(KEY_AGREED, false)

    override suspend fun setAgreed(agreed: Boolean) {
        prefs.edit().putBoolean(KEY_AGREED, agreed).apply()
    }

    private companion object {
        const val KEY_AGREED = "agreed"
    }
}
