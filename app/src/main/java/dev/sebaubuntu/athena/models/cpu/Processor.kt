/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

/**
 * `struct cpuinfo_processor`
 */
data class Processor(
    /**
     * SMT (hyperthread) ID within a core
     */
    val smtId: UInt,

    /**
     * Core containing this logical processor
     */
    val core: Core,

    /**
     * Cluster of cores containing this logical processor
     */
    val cluster: Cluster,

    /**
     * Physical package containing this logical processor
     */
    val cpuPackage: Package,

    /**
     * Linux-specific ID for the logical processor:
     * - Linux kernel exposes information about this logical processor in
     * /sys/devices/system/cpu/cpu<linux_id>/
     * - Bit <linux_id> in the cpu_set_t identifies this logical processor
     */
    val linuxId: UInt,

    /**
     * x86 only
     *
     * APIC ID (unique x86-specific ID of the logical processor)
     */
    val apicId: UInt?,

    /**
     * @see ProcessorCache
     */
    val cache: ProcessorCache,
) {
    companion object {
        @JvmStatic
        fun fromCpuInfo(
            smtId: Int,
            core: Core,
            cluster: Cluster,
            cpuPackage: Package,
            linuxId: Int,
            apicId: Int,
            cache: ProcessorCache,
        ) = Processor(
            smtId.toUInt(),
            core,
            cluster,
            cpuPackage,
            linuxId.toUInt(),
            apicId.takeUnless { it == 0 }?.toUInt(),
            cache,
        )
    }
}
