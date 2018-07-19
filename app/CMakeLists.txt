# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.



# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.



# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.




#设置so库路径  定义一个my_lib_path变量

set(my_lib_path ${CMAKE_SOURCE_DIR}/libs)

#将第三方库作为动态库引用，keosdlib是第三方so的文件名（去掉前缀lib）

add_library( gmp

             SHARED

             IMPORTED )

add_library( keosdlib

             SHARED

             IMPORTED )

#指名第三方库的绝对路径

set_target_properties( keosdlib

                       PROPERTIES IMPORTED_LOCATION

                       ${my_lib_path}/${ANDROID_ABI}/libkeosdlib.so )

set_target_properties( gmp

                       PROPERTIES IMPORTED_LOCATION

                       ${my_lib_path}/${ANDROID_ABI}/libgmp.so )

#把keosdlib添加到链接库，eosJni是我们自己的JNI代码编译出来的so
add_library( # Sets the name of the library.
            eosJni
             # Sets the library as a shared library.
            SHARED
             # Provides a relative path to your source file(s).
             src/main/cpp/eosJni.cpp )



find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( # Specifies the target library.
                                      eosJni

                                     # Links the target library to the log library
                                     # included in the NDK.

                                      keosdlib

                                      gmp

                                      ${log-lib} )



