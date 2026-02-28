package com.mubashir.customkeyboard

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform