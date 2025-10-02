/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.cpu.models

/**
 * `struct cpuinfo_cluster`
 */
data class Cluster(
    /**
     * Index of the first logical processor in the cluster
     */
    val processorStart: UInt,

    /**
     * Number of logical processors in the cluster
     */
    val processorCount: UInt,

    /**
     * Index of the first core in the cluster
     */
    val coreStart: UInt,

    /**
     * Number of cores on the cluster
     */
    val coreCount: UInt,

    /**
     * Cluster ID within a package
     */
    val clusterId: UInt,

    /**
     * Physical package containing the cluster
     */
    val cpuPackage: Package,

    /**
     * CPU microarchitecture vendor of the cores in the cluster
     */
    val vendor: Vendor,

    /**
     * CPU microarchitecture of the cores in the cluster
     */
    val uarch: Uarch,

    /**
     * x86 only
     *
     * Value of CPUID leaf 1 EAX register of the cores in the cluster
     */
    val cpuid: UInt?,

    /**
     * ARM and ARM64 only
     *
     * Value of Main ID Register (MIDR) of the cores in the cluster
     */
    val midr: Midr?,

    /**
     * Clock rate (non-Turbo) of the cores in the cluster, in Hz
     */
    val frequency: ULong,
) {
    companion object {
        @JvmStatic
        fun fromCpuInfo(
            processorStart: Int,
            processorCount: Int,
            coreStart: Int,
            coreCount: Int,
            clusterId: Int,
            cpuPackage: Package,
            vendor: Int,
            uarch: Int,
            cpuid: Int,
            midr: Int,
            frequency: Long,
        ) = Cluster(
            processorStart.toUInt(),
            processorCount.toUInt(),
            coreStart.toUInt(),
            coreCount.toUInt(),
            clusterId.toUInt(),
            cpuPackage,
            Vendor.fromCpuInfo(vendor),
            Uarch.fromCpuInfo(uarch),
            cpuid.takeUnless { it == 0 }?.toUInt(),
            midr.takeUnless { it == 0 }?.let { Midr.fromCpuInfo(it) },
            frequency.toULong(),
        )
    }
}
