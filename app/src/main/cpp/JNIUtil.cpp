//
// Created by linwang on 2018/7/13.
//

#include <jni.h>
#include "com_cybex_gma_client_ui_JNIUtil.h"


JNIEXPORT jobject JNICALL Java_com_cybex_gma_client_ui_JNIUtil_createKey
  (JNIEnv *, jclass);

JNIEXPORT jstring JNICALL Java_com_cybex_gma_client_ui_JNIUtil_get_1cypher
    (JNIEnv *, jclass, jstring, jstring);

JNIEXPORT jstring JNICALL Java_com_cybex_gma_client_ui_JNIUtil_get_1private_1key
      (JNIEnv *, jclass, jstring, jstring);