package com.heklast.smartspender.core.common

sealed interface Result<out T> {
    data class Ok<T>(val value: T) : Result<T>
    data class Err(val cause: Throwable) : Result<Nothing>
}

inline fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Ok -> value
    is Result.Err -> throw cause
}
