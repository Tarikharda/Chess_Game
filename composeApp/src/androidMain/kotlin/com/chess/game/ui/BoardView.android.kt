package com.chess.game.ui

actual fun sayHelloToUser(name: String): String {
    return JniChessHelper.sayHelloToUser(name);

}


//JNI Wrapper
private object JniChessHelper {
    init {
        System.loadLibrary("chessGame")
    }
    // this who have reference in cpp code so i can using jni helper for it
    external fun sayHelloToUser(name : String):String
}
