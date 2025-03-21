cmake_minimum_required(VERSION 3.18.1)
#project("chessGame")

add_library(
       chessGame
        SHARED
        # Paths relative to CMakeLists.txt location
        ../../nativeMain/cpp/ChessEngine.cpp
        ../../nativeMain/cpp/moves/ChessPieceMoves.cpp

        ../../nativeMain/kotlin/jni/ChessJNI.cpp
)

find_library(log-lib log)

target_include_directories(
       chessGame
        PRIVATE
        ../../nativeMain/cpp  # For ChessEngine.h
)

target_link_libraries(
       chessGame
        android
        log
        ${log-lib}
)