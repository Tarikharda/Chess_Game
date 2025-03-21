package com.chess.game.jniExample

interface NativeBase64 {

    class Error(msg: String) : Exception(msg)

    fun encode(bytes: ByteArray, url: Boolean = false): String

    fun decode(b64: String): ByteArray
}
