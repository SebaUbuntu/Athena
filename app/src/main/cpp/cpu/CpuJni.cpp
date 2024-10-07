/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#include "CpuJni.h"

#include <cpuinfo.h>
#include "../jni/jni_utils.h"

jobject CpuJni::cacheToJava(const struct cpuinfo_cache *cache) {
    if (cache == nullptr) {
        return nullptr;
    }

    auto object = mEnv->CallStaticObjectMethod(
            cacheClazz, cacheFromCpuInfoMethodID,
            cache->size,
            cache->associativity,
            cache->sets,
            cache->partitions,
            cache->line_size,
            cache->flags,
            cache->processor_start,
            cache->processor_count
    );
    JNI_CHECK(mEnv);

    return object;
}

jobject CpuJni::clusterToJava(const struct cpuinfo_cluster *cluster) {
    if (cluster == nullptr) {
        return nullptr;
    }

    auto object = mEnv->CallStaticObjectMethod(
            clusterClazz, clusterFromCpuInfoMethodID,
            cluster->processor_start,
            cluster->processor_count,
            cluster->core_start,
            cluster->core_count,
            cluster->cluster_id,
            packageToJava(cluster->package),
            cluster->vendor,
            cluster->uarch,
#if CPUINFO_ARCH_X86 || CPUINFO_ARCH_X86_64
            cluster->cpuid,
#else
            0,
#endif
#if CPUINFO_ARCH_ARM || CPUINFO_ARCH_ARM64
            cluster->midr,
#else
            0,
#endif
            cluster->frequency
    );
    JNI_CHECK(mEnv);

    return object;
}

jobject CpuJni::coreToJava(const struct cpuinfo_core *core) {
    if (core == nullptr) {
        return nullptr;
    }

    auto object = mEnv->CallStaticObjectMethod(
            coreClazz, coreFromCpuInfoMethodID,
            core->processor_start,
            core->processor_count,
            core->core_id,
            clusterToJava(core->cluster),
            packageToJava(core->package),
            core->vendor,
            core->uarch,
#if CPUINFO_ARCH_X86 || CPUINFO_ARCH_X86_64
            core->cpuid,
#else
            0,
#endif
#if CPUINFO_ARCH_ARM || CPUINFO_ARCH_ARM64
            core->midr,
#else
            0,
#endif
            core->frequency
    );
    JNI_CHECK(mEnv);

    return object;
}

jobject CpuJni::packageToJava(const struct cpuinfo_package *package) {
    if (package == nullptr) {
        return nullptr;
    }

    auto object = mEnv->CallStaticObjectMethod(
            packageClazz, packageFromCpuInfoMethodID,
            mEnv->NewStringUTF(package->name),
            package->processor_start,
            package->processor_count,
            package->core_start,
            package->core_count,
            package->cluster_start,
            package->cluster_count
    );
    JNI_CHECK(mEnv);

    return object;
}

jobject CpuJni::processorToJava(const struct cpuinfo_processor *processor) {
    if (processor == nullptr) {
        return nullptr;
    }

    auto object = mEnv->CallStaticObjectMethod(
            processorClazz, processorFromCpuInfoMethodID,
            processor->smt_id,
            coreToJava(processor->core),
            clusterToJava(processor->cluster),
            packageToJava(processor->package),
            processor->linux_id,
#if CPUINFO_ARCH_X86 || CPUINFO_ARCH_X86_64
            processor->apic_id,
#else
            0,
#endif
            processorCacheToJava(processor)
    );
    JNI_CHECK(mEnv);

    return object;
}

jobject CpuJni::processorCacheToJava(const struct cpuinfo_processor *processor) {
    if (processor == nullptr) {
        return nullptr;
    }

    auto object = mEnv->CallStaticObjectMethod(
            processorCacheClazz, processorCacheFromCpuInfoMethodID,
            cacheToJava(processor->cache.l1i),
            cacheToJava(processor->cache.l1d),
            cacheToJava(processor->cache.l2),
            cacheToJava(processor->cache.l3),
            cacheToJava(processor->cache.l4)
    );
    JNI_CHECK(mEnv);

    return object;
}

jobject CpuJni::uarchInfoToJava(const struct cpuinfo_uarch_info *uarchInfo) {
    if (uarchInfo == nullptr) {
        return nullptr;
    }

    auto object = mEnv->CallStaticObjectMethod(
            uarchInfoClazz, uarchInfoFromCpuInfoMethodID,
            uarchInfo->uarch,
#if CPUINFO_ARCH_X86 || CPUINFO_ARCH_X86_64
            uarchInfo->cpuid,
#else
            0,
#endif
#if CPUINFO_ARCH_ARM || CPUINFO_ARCH_ARM64
            uarchInfo->midr,
#else
            0,
#endif
            uarchInfo->processor_count,
            uarchInfo->core_count
    );
    JNI_CHECK(mEnv);

    return object;
}
