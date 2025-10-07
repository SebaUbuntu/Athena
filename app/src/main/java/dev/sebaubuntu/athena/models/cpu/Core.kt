/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

/**
 * `struct cpuinfo_core`
 */
data class Core(
    /**
     * Index of the first logical processor on this core.
     */
    val processorStart: UInt,

    /**
     * Number of logical processors on this core
     */
    val processorCount: UInt,

    /**
     * Core ID within a package
     */
    val coreId: UInt,

    /**
     * Cluster containing this core
     */
    val cluster: Cluster,

    /**
     * Physical package containing this core.
     */
    val cpuPackage: Package,

    /**
     * Vendor of the CPU microarchitecture for this core
     */
    val vendor: Vendor,

    /**
     * CPU microarchitecture for this core
     */
    val uarch: Uarch,

    /**
     * x86 only
     *
     * Value of CPUID leaf 1 EAX register for this core
     */
    val cpuid: UInt?,

    /**
     * ARM and ARM64 only
     *
     * Value of Main ID Register (MIDR) for this core
     */
    val midr: Midr?,

    /**
     * Clock rate (non-Turbo) of the core, in Hz
     */
    val frequency: ULong,
) {
    companion object {
        @JvmStatic
        fun fromCpuInfo(
            processorStart: Int,
            processorCount: Int,
            coreId: Int,
            cluster: Cluster,
            cpuPackage: Package,
            vendor: Int,
            uarch: Int,
            cpuId: Int,
            midr: Int,
            frequency: Long,
        ) = Core(
            processorStart.toUInt(),
            processorCount.toUInt(),
            coreId.toUInt(),
            cluster,
            cpuPackage,
            Vendor.fromCpuInfo(vendor),
            Uarch.fromCpuInfo(uarch),
            cpuId.takeUnless { it == 0 }?.toUInt(),
            midr.takeUnless { it == 0 }?.let { Midr.fromCpuInfo(it) },
            frequency.toULong(),
        )
    }
}
