package com.runnersHi.debug

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * 앱 프로세스를 끄고 새로 띄운다. 별도 프로세스(:debug_restart)에서 돌아 앱 프로세스가 죽어도 남는다.
 */
class DebugRestartActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        packageManager.getLaunchIntentForPackage(packageName)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(it)
        }
        finish()
        Runtime.getRuntime().exit(0)
    }

    companion object {
        fun restart(context: Context) {
            context.startActivity(
                Intent(context, DebugRestartActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            Runtime.getRuntime().exit(0)
        }
    }
}
