/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

data class LinuxCpu(
    val id: Int,
) {
    private val cpuBaseDir = CPUINFO_BASE_DIR / "cpu${id}"

    init {
        require(FILESYSTEM.exists(cpuBaseDir)) { "CPU $id doesn't exist" }
    }

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

    // File utils

    private fun getString(path: Path) = runCatching {
        FILESYSTEM.read(path) {
            readUtf8Line()
        }
    }.getOrNull()

    private fun getLong(path: Path) = getString(path)?.toLongOrNull()

    private fun getIntArray(path: Path) = getString(path)?.let {
        mutableListOf<Int>().apply {
            for (item in it.split(",")) {
                val values = item.split('-')
                assert(values.size <= 2)

                if (values.size == 1) {
                    add(values.first().toInt())
                } else if (values.size == 2) {
                    for (i in values[0].toInt()..values[1].toInt()) {
                        add(i)
                    }
                }
            }
        }.toTypedArray()
    }

    companion object {
        val CPUINFO_BASE_DIR = "/sys/devices/system/cpu".toPath()

        private val FILESYSTEM = FileSystem.SYSTEM

        private val ONLINE_CPUS = CPUINFO_BASE_DIR / "online"

        private const val CPUINFO_CURRENT_FREQ = "cpufreq/cpuinfo_cur_freq"
        private const val CPUINFO_MINIMUM_FREQ = "cpufreq/cpuinfo_min_freq"
        private const val CPUINFO_MAXIMUM_FREQ = "cpufreq/cpuinfo_max_freq"

        private const val SCALING_CURRENT_FREQ = "cpufreq/scaling_cur_freq"
        private const val SCALING_MINIMUM_FREQ = "cpufreq/scaling_min_freq"
        private const val SCALING_MAXIMUM_FREQ = "cpufreq/scaling_max_freq"

        fun fromProcessor(processor: Processor) = LinuxCpu(processor.linuxId.toInt())
    }
}
