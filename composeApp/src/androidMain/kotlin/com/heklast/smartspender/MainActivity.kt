// androidApp/src/androidMain/kotlin/com/heklast/smartspender/MainActivity.kt
package com.heklast.smartspender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() } // App() is in commonMain
    }
}