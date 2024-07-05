/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.readLines
import kotlin.reflect.safeCast

data class Cpu(
    val id: Int,
) {
    private val cpuBaseDir = CPUINFO_BASE_DIR / "cpu${id}"

    init {
        assert(cpuBaseDir.exists()) { "CPU $id doesn't exist" }
    }

    override fun hashCode() = id.hashCode()

    override fun equals(other: Any?) = Cpu::class.safeCast(other)?.let {
        id == it.id
    } ?: false

    val isOnline: Boolean?
        get() = getIntArray(ONLINE_CPUS)?.contains(id)

    /**
     * Current frequency, in Hz.
     */
    val currentFrequencyHz: Long?
        get() = getLong(cpuBaseDir / CPUINFO_CURRENT_FREQ)?.let { it * 1000 }

    /**
     * Minimum frequency, in Hz.
     */
    val minimumFrequencyHz: Long?
        get() = getLong(cpuBaseDir / CPUINFO_MINIMUM_FREQ)?.let { it * 1000 }

    /**
     * Maximum frequency, in Hz.
     */
    val maximumFrequencyHz: Long?
        get() = getLong(cpuBaseDir / CPUINFO_MAXIMUM_FREQ)?.let { it * 1000 }

    /**
     * Current frequency, in Hz.
     */
    val scalingCurrentFrequencyHz: Long?
        get() = getLong(cpuBaseDir / SCALING_CURRENT_FREQ)?.let { it * 1000 }

    /**
     * Minimum frequency, in Hz.
     */
    val scalingMinimumFrequencyHz: Long?
        get() = getLong(cpuBaseDir / SCALING_MINIMUM_FREQ)?.let { it * 1000 }

    /**
     * Maximum frequency, in Hz.
     */
    val scalingMaximumFrequencyHz: Long?
        get() = getLong(cpuBaseDir / SCALING_MAXIMUM_FREQ)?.let { it * 1000 }

    /**
     * Physical package ID of this CPU. Typically corresponds to a physical
     * socket number, but the actual value is architecture and platform
     * dependent.
     */
    val physicalPackageId: Int?
        get() = getInt(cpuBaseDir / CPUINFO_PHYSICAL_PACKAGE_ID)?.takeIf { it != -1 }

    /**
     * The CPU cluster ID of this CPU. Typically it is the hardware platform's
     * identifier (rather than the kernel's). The actual value is
     * architecture and platform dependent.
     */
    val clusterId: Int?
        get() = getInt(cpuBaseDir / CPUINFO_CLUSTER_ID)?.takeIf { it != -1 }

    /**
     * The CPU die ID of this CPU. Typically it is the hardware platform's
     * identifier (rather than the kernel's). The actual value is
     * architecture and platform dependent.
     */
    val dieId: Int?
        get() = getInt(cpuBaseDir / CPUINFO_DIE_ID)?.takeIf { it != -1 }

    /**
     * The CPU core ID of this CPU. Typically it is the hardware platform's
     * identifier (rather than the kernel's). The actual value is
     * architecture and platform dependent.
     */
    val coreId: Int
        get() = getInt(cpuBaseDir / CPUINFO_CORE_ID) ?: 0

    // File utils

    private fun getInt(path: Path) = runCatching {
        path.readLines()[0]
    }.getOrNull()?.toIntOrNull()

    private fun getLong(path: Path) = runCatching {
        path.readLines()[0]
    }.getOrNull()?.toLongOrNull()

    private fun getIntArray(path: Path) = runCatching {
        val text = path.readLines()[0]

        val range = mutableListOf<Int>()

        for (item in text.split(",")) {
            val values = item.split('-')
            assert(values.size <= 2)

            if (values.size == 1) {
                range.add(values.first().toInt())
            } else if (values.size == 2) {
                for (i in values[0].toInt()..values[1].toInt()) {
                    range.add(i)
                }
            }
        }

        return@runCatching range.toTypedArray()
    }.getOrNull()

    companion object {
        val CPUINFO_BASE_DIR = Path("/sys/devices/system/cpu")

        private val ONLINE_CPUS = CPUINFO_BASE_DIR / "online"

        private const val CPUINFO_CURRENT_FREQ = "cpufreq/cpuinfo_cur_freq"
        private const val CPUINFO_MINIMUM_FREQ = "cpufreq/cpuinfo_min_freq"
        private const val CPUINFO_MAXIMUM_FREQ = "cpufreq/cpuinfo_max_freq"

        private const val SCALING_CURRENT_FREQ = "cpufreq/scaling_cur_freq"
        private const val SCALING_MINIMUM_FREQ = "cpufreq/scaling_min_freq"
        private const val SCALING_MAXIMUM_FREQ = "cpufreq/scaling_max_freq"

        private const val CPUINFO_PHYSICAL_PACKAGE_ID = "topology/physical_package_id"
        private const val CPUINFO_CLUSTER_ID = "topology/cluster_id"
        private const val CPUINFO_DIE_ID = "topology/die_id"
        private const val CPUINFO_CORE_ID = "topology/core_id"
    }
}
