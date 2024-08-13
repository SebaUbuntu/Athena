/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "CpuUtils"

#include <cpuinfo.h>
#include <jni.h>
#include "CpuJni.h"
#include "jni_utils.h"
#include "logging.h"

#define CPUINFO_JNI_FUNC(func_name, clazz_lowercase, cpuinfo_func_name)               \
    extern "C"                                                                        \
    JNIEXPORT void JNICALL                                                            \
    Java_dev_sebaubuntu_athena_utils_CpuInfoUtils_get##func_name(JNIEnv *env,         \
                                                                 jobject thiz,        \
                                                                 jobject arraylist) { \
        auto cpuJni = CpuJni(env);                                                    \
        auto addMethodID = getArrayListAddMethodID(env, arraylist);                   \
                                                                                      \
        cpuinfo_initialize();                                                         \
                                                                                      \
        auto elements_count = cpuinfo_get_##cpuinfo_func_name##_count();              \
        if (elements_count < 1) {                                                     \
            LOGI("No ##clazz_lowercase## found");                                     \
            return;                                                                   \
        }                                                                             \
                                                                                      \
        auto elements = cpuinfo_get_##cpuinfo_func_name();                            \
                                                                                      \
        for (int i = 0; i < elements_count; i++) {                                    \
            env->CallBooleanMethod(                                                   \
                    arraylist, addMethodID,                                           \
                    cpuJni.clazz_lowercase##ToJava(&elements[i]));                    \
            JNI_CHECK(env);                                                           \
        }                                                                             \
                                                                                      \
        cpuinfo_deinitialize();                                                       \
    }

CPUINFO_JNI_FUNC(Processors, processor, processors)
CPUINFO_JNI_FUNC(Cores, core, cores)
CPUINFO_JNI_FUNC(Clusters, cluster, clusters)
CPUINFO_JNI_FUNC(Packages, package, packages)
CPUINFO_JNI_FUNC(Uarchs, uarchInfo, uarchs)
CPUINFO_JNI_FUNC(L1iCaches, cache, l1i_caches)
CPUINFO_JNI_FUNC(L1dCaches, cache, l1d_caches)
CPUINFO_JNI_FUNC(L2Caches, cache, l2_caches)
CPUINFO_JNI_FUNC(L3Caches, cache, l3_caches)
CPUINFO_JNI_FUNC(L4Caches, cache, l4_caches)
