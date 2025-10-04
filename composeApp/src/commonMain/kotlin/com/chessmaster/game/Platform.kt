package com.chessmaster.game

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform