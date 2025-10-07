/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once

#include <cpuinfo.h>
#include <cstdlib>
#include <jni.h>
#include "../jni/jni_utils.h"

#define CPU_PACKAGE "dev/sebaubuntu/athena/models/cpu"

#define CPU_CLASS_SIG(clazz) "L" CPU_PACKAGE "/" #clazz ";"

#define DECLARE_CPU_CLASS(clazz, fromCpuInfo_args_signature)                                       \
        inline static jclass get##clazz##Class(JNIEnv *env) {                                      \
            auto clazzObject = env->FindClass(CPU_PACKAGE "/" #clazz);                             \
            JNI_CHECK(env);                                                                        \
            return clazzObject;                                                                    \
        }                                                                                          \
                                                                                                   \
        inline static jmethodID get##clazz##FromCpuInfoMethodID(JNIEnv *env, jclass clazzObject) { \
            auto methodID = env->GetStaticMethodID(                                                \
                    clazzObject,                                                                   \
                    "fromCpuInfo", "(" fromCpuInfo_args_signature ")" CPU_CLASS_SIG(clazz));       \
            JNI_CHECK(env);                                                                        \
            return methodID;                                                                       \
        }

DECLARE_CPU_CLASS(Cache, "IIIIIIII")

DECLARE_CPU_CLASS(Cluster, "IIIII" CPU_CLASS_SIG(Package) "IIIIJ")

DECLARE_CPU_CLASS(Core, "III" CPU_CLASS_SIG(Cluster) CPU_CLASS_SIG(Package) "IIIIJ")

DECLARE_CPU_CLASS(Package, STRING_CLASS_SIG "IIIIII")

DECLARE_CPU_CLASS(Processor,
                  "I" CPU_CLASS_SIG(Core) CPU_CLASS_SIG(Cluster) CPU_CLASS_SIG(
                          Package) "II" CPU_CLASS_SIG(ProcessorCache))

DECLARE_CPU_CLASS(ProcessorCache,
                  CPU_CLASS_SIG(Cache) CPU_CLASS_SIG(Cache) CPU_CLASS_SIG(Cache) CPU_CLASS_SIG(
                          Cache) CPU_CLASS_SIG(Cache))

DECLARE_CPU_CLASS(Tlb, "IIJ")

DECLARE_CPU_CLASS(TraceCache, "II")

DECLARE_CPU_CLASS(Uarch, "I")

DECLARE_CPU_CLASS(UarchInfo, "IIIII")

DECLARE_CPU_CLASS(Vendor, "I")

#define DEFINE_CLASS_ATTRIBUTES(clazz_lowercase)    \
    jclass clazz_lowercase##Clazz;                  \
    jmethodID clazz_lowercase##FromCpuInfoMethodID;

#define FILL_CLASS_ATTRIBUTES(env, clazz_lowercase, clazz)                \
    this->clazz_lowercase##Clazz = get##clazz##Class(env);                \
    this->clazz_lowercase##FromCpuInfoMethodID =                          \
            get##clazz##FromCpuInfoMethodID(env, clazz_lowercase##Clazz);

struct CpuJni {
    explicit CpuJni(JNIEnv *env) : mEnv(env) {
        FILL_CLASS_ATTRIBUTES(env, cache, Cache)
        FILL_CLASS_ATTRIBUTES(env, cluster, Cluster)
        FILL_CLASS_ATTRIBUTES(env, core, Core)
        FILL_CLASS_ATTRIBUTES(env, package, Package)
        FILL_CLASS_ATTRIBUTES(env, processor, Processor)
        FILL_CLASS_ATTRIBUTES(env, processorCache, ProcessorCache)
        FILL_CLASS_ATTRIBUTES(env, tlb, Tlb)
        FILL_CLASS_ATTRIBUTES(env, traceCache, TraceCache)
        FILL_CLASS_ATTRIBUTES(env, uarch, Uarch)
        FILL_CLASS_ATTRIBUTES(env, uarchInfo, UarchInfo)
        FILL_CLASS_ATTRIBUTES(env, vendor, Vendor)
    }

    JNIEnv *mEnv;

    DEFINE_CLASS_ATTRIBUTES(cache)
    DEFINE_CLASS_ATTRIBUTES(cluster)
    DEFINE_CLASS_ATTRIBUTES(core)
    DEFINE_CLASS_ATTRIBUTES(package)
    DEFINE_CLASS_ATTRIBUTES(processor)
    DEFINE_CLASS_ATTRIBUTES(processorCache)
    DEFINE_CLASS_ATTRIBUTES(tlb)
    DEFINE_CLASS_ATTRIBUTES(traceCache)
    DEFINE_CLASS_ATTRIBUTES(uarch)
    DEFINE_CLASS_ATTRIBUTES(uarchInfo)
    DEFINE_CLASS_ATTRIBUTES(vendor)

    jobject cacheToJava(const struct cpuinfo_cache *cache);

    jobject clusterToJava(const struct cpuinfo_cluster *cluster);

    jobject coreToJava(const struct cpuinfo_core *core);

    jobject packageToJava(const struct cpuinfo_package *package);

    jobject processorToJava(const struct cpuinfo_processor *processor);

    jobject processorCacheToJava(const struct cpuinfo_processor *processor);

    jobject uarchInfoToJava(const struct cpuinfo_uarch_info *uarchInfo);
};

#undef DEFINE_CLASS_ATTRIBUTES
#undef FILL_CLASS_ATTRIBUTES
