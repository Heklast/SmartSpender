package com.heklast.smartspender.core.telemetry

expect object Telemetry {
    fun setUserId(id: String?)
    fun setUserProperty(key: String, value: String?)
    fun logEvent(name: String, params: Map<String, Any?> = emptyMap())
    fun recordError(t: Throwable, extras: Map<String, Any?> = emptyMap())
}
