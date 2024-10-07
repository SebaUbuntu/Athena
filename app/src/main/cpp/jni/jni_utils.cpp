/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#include "jni_utils.h"

#include <cstdlib>

jmethodID getArrayListAddMethodID(JNIEnv *env, jobject object) {
    auto arrayListClazz = env->GetObjectClass(object);
    JNI_CHECK(env);

    auto methodID = env->GetMethodID(arrayListClazz, "add", "(" OBJECT_CLASS_SIG ")Z");
    JNI_CHECK(env);

    return methodID;
}
