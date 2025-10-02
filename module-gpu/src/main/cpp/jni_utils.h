/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once

#include <functional>
#include <jni.h>

void withJniCheck(JNIEnv *env, const std::function<void()> &func);

template<typename T>
T withJniCheck(JNIEnv *env, const std::function<T()> &func) {
    T result = func();

    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        throw std::runtime_error("JNI exception");
    }

    return result;
}

jmethodID getArrayListAddMethodID(JNIEnv *env, jobject object);
