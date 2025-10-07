/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once

#include <jni.h>

#define JNI_CHECK(env)                \
    do {                              \
        if (env->ExceptionCheck()) {  \
            env->ExceptionDescribe(); \
            abort();                  \
        }                             \
    } while (0)

#define OBJECT_CLASS_SIG "Ljava/lang/Object;"
#define STRING_CLASS_SIG "Ljava/lang/String;"

jmethodID getArrayListAddMethodID(JNIEnv *env, jobject object);
