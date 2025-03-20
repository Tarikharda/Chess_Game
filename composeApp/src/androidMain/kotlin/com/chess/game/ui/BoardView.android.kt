package com.chess.game.ui


actual fun calculateChessMoves(position: String): String {
    return JniChessHelper.calculateMoves(position)
}

actual fun sayHelloToUser(name: String): String {
    return JniChessHelper.sayHelloToUser(name)
}

// JNI Wrapper
private object JniChessHelper {
    init {
        System.loadLibrary("chess-engine")
    }

    external fun calculateMoves(position: String):String

    external fun sayHelloToUser(name: String):String
}