/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.cpu.models

/**
 * `struct cpuinfo_uarch_info`
 */
data class UarchInfo(
    /**
     * Type of CPU microarchitecture
     */
    val uarch: Uarch,

    /**
     * Value of CPUID leaf 1 EAX register for the microarchitecture
     */
    val cpuid: UInt?,

    /**
     * Value of Main ID Register (MIDR) for the microarchitecture
     */
    val midr: Midr?,

    /**
     * Number of logical processors with the microarchitecture
     */
    val processorCount: UInt,

    /**
     * Number of cores with the microarchitecture
     */
    val coreCount: UInt,
) {
    companion object {
        @JvmStatic
        fun fromCpuInfo(
            uarch: Int,
            cpuId: Int,
            midr: Int,
            processorCount: Int,
            coreCount: Int,
        ) = UarchInfo(
            Uarch.fromCpuInfo(uarch),
            cpuId.takeUnless { it == 0 }?.toUInt(),
            midr.takeUnless { it == 0 }?.let { Midr.fromCpuInfo(it) },
            processorCount.toUInt(),
            coreCount.toUInt(),
        )
    }
}
