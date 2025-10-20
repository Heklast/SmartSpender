package com.heklast.smartspender.core.telemetry

// No-op for now. You can wire Firebase iOS later if needed.
actual object Telemetry {
    actual fun setUserId(id: String?) {}
    actual fun setUserProperty(key: String, value: String?) {}
    actual fun logEvent(name: String, params: Map<String, Any?>) {}
    actual fun recordError(t: Throwable, extras: Map<String, Any?>) {}
}
