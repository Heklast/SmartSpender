package com.heklast.smartspender.core.telemetry

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.heklast.smartspender.core.platform.appCtx
import kotlin.getValue

actual object Telemetry {

    private val analytics by lazy { FirebaseAnalytics.getInstance(appCtx) }
    private val crash by lazy { FirebaseCrashlytics.getInstance() }

    actual fun setUserId(id: String?) {
        analytics.setUserId(id)
        crash.setUserId(id ?: "")
    }

    actual fun setUserProperty(key: String, value: String?) {
        analytics.setUserProperty(key, value)
        value?.let { crash.setCustomKey(key, it) }
    }

    actual fun logEvent(name: String, params: Map<String, Any?>) {
        val bundle = Bundle()
        params.forEach { (k, v) ->
            when (v) {
                null -> {}
                is String -> bundle.putString(k, v)
                is Int -> bundle.putInt(k, v)
                is Long -> bundle.putLong(k, v)
                is Double -> bundle.putDouble(k, v)
                is Float -> bundle.putFloat(k, v)
                is Boolean -> bundle.putInt(k, if (v) 1 else 0)
                else -> bundle.putString(k, v.toString())
            }
        }
        analytics.logEvent(name, bundle)
    }

    actual fun recordError(t: Throwable, extras: Map<String, Any?>) {
        extras.forEach { (k, v) ->
            when (v) {
                null -> {}
                is String -> crash.setCustomKey(k, v)
                is Int -> crash.setCustomKey(k, v)
                is Long -> crash.setCustomKey(k, v)
                is Double -> crash.setCustomKey(k, v)
                is Float -> crash.setCustomKey(k, v)
                is Boolean -> crash.setCustomKey(k, v)
                else -> crash.setCustomKey(k, v.toString())
            }
        }
        crash.recordException(t)
    }
}
