#include <jni.h>
#include <string>
#include "ChessEngine.h"
#include "model/PieceMove.h"


extern "C" JNIEXPORT jstring JNICALL
Java_com_chess_game_ui_JniChessHelper_sayHelloToUser(JNIEnv* env, jobject /*thiz*/, jstring name) {

    const char* nameCStr = env->GetStringUTFChars(name, nullptr);
    std::string result = sayHelloToUser(nameCStr);
    env->ReleaseStringUTFChars(name, nameCStr);
    return env->NewStringUTF(result.c_str());

}

extern "C" JNIEXPORT jobject JNICALL
Java_com_chess_game_logic_JniChessHelper_pawnMove(JNIEnv* env, jobject /*thiz*/, jstring piece){

}