/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once

#include <cstdlib>
#include <jni.h>

#define JNI_CHECK(env, expr)            \
    ({                                  \
        auto _result = (expr);          \
        if ((env)->ExceptionCheck()) {  \
            (env)->ExceptionDescribe(); \
            (env)->ExceptionClear();    \
            abort();                    \
        }                               \
        _result;                        \
    })

#define LIST_CLASS_SIG "Ljava/util/List;"
#define OBJECT_CLASS_SIG "Ljava/lang/Object;"
#define STRING_CLASS_SIG "Ljava/lang/String;"

static jmethodID getArrayListAddMethodID(JNIEnv *env, jobject object) {
    auto arrayListClazz = JNI_CHECK(env, env->GetObjectClass(object));

    auto methodID = JNI_CHECK(env, env->GetMethodID(
            arrayListClazz,
            "add",
            "(" OBJECT_CLASS_SIG ")Z"));

    return methodID;
}
