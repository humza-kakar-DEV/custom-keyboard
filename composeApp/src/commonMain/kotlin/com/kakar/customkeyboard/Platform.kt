package com.kakar.customkeyboard

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform