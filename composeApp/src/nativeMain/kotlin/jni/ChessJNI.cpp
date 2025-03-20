#include <jni.h>
#include <string>
#include <vector>
#include "ChessEngine.h"

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_chess_game_ui_JniChessHelper_calculateMoves( JNIEnv* env, jobject /*thiz*/, jstring position ) {

    const char* posStr = env->GetStringUTFChars(position, nullptr);
    std::vector<std::string> moves = calculateMoves(posStr);
    env->ReleaseStringUTFChars(position, posStr);

    // Convert moves to Java array
    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray result = env->NewObjectArray(moves.size(), stringClass, nullptr);
    for (int i = 0; i < moves.size(); i++) {
        env->SetObjectArrayElement(result, i, env->NewStringUTF(moves[i].c_str()));
    }
    return result;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_chess_game_ui_JniChessHelper_sayHelloToUser(JNIEnv* env, jobject /*thiz*/, jstring Name) {
    // Convert the jstring (Name) to a C-style string
    const char* nameStr = env->GetStringUTFChars(Name, nullptr);
    if (nameStr == nullptr) {
        return nullptr; // Handle out-of-memory error
    }

    // Create the greeting m1essage
    std::string greeting = "Hello: ";
    for(int i = 0 ; i < 10 ;  i++){
        greeting += nameStr;
        greeting += "\n";
    }

    // Release the JNI string to avoid memory leaks
    env->ReleaseStringUTFChars(Name, nameStr);

    // Convert the C++ string back to a jstring and return it
    return env->NewStringUTF(greeting.c_str());
}