package com.heklast.smartspender

interface Platform {
    val name: String
}
expect fun getPlatform(): Platform