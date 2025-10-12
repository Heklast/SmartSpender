package com.heklast.smartspender.core.ui

import android.os.Build
import com.heklast.smartspender.Platform

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()