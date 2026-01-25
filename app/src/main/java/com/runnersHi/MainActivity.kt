package com.runnersHi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.runnersHi.navigation.RunnersHiNavHost
import com.runnersHi.presentation.common.theme.RunnersHiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RunnersHiTheme {
                RunnersHiNavHost()
            }
        }
    }
}
