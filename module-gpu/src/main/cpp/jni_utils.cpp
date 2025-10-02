/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#include "jni_utils.h"

void withJniCheck(JNIEnv *env, const std::function<void()> &func) {
    func();

    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        throw std::runtime_error("JNI exception");
    }
}

jmethodID getArrayListAddMethodID(JNIEnv *env, jobject object) {
    jclass arrayListClazz = withJniCheck<jclass>(env, [=]() {
        return env->GetObjectClass(object);
    });

    auto methodID = withJniCheck<jmethodID>(env, [=]() {
        return env->GetMethodID(
                arrayListClazz,
                "add",
                "(Ljava/lang/Object;)Z");
    });

    return methodID;
}
