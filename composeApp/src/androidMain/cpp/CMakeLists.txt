cmake_minimum_required(VERSION 3.18.1)
project("chess-engine")

add_library(
        chess-engine
        SHARED
        # Paths relative to CMakeLists.txt location
        ../../nativeMain/cpp/ChessEngine.cpp
        ../../nativeMain/kotlin/jni/ChessJNI.cpp
)

find_library(log-lib log)

target_include_directories(
        chess-engine
        PRIVATE
        ../../nativeMain/cpp  # For ChessEngine.h
)

target_link_libraries(
        chess-engine
        android
        log
        ${log-lib}
)