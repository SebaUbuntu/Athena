/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.cpu

import java.io.File
import java.util.regex.Pattern
import kotlin.reflect.safeCast

data class CPU(
    val id: Int,
) {
    private val cpuBaseDir = "${CPUINFO_BASE_DIR}/cpu${id}"

    init {
        assert(File(cpuBaseDir).exists()) { "CPU $id doesn't exist" }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        val obj = CPU::class.safeCast(other) ?: return false
        return id == obj.id
    }

    /**
     * Current frequency, in Hz.
     */
    val currentFrequency: Int
        get() = this.getInt(CPUINFO_CURRENT_FREQ)

    /**
     * Minimum frequency, in Hz.
     */
    val minimumFrequency: Int
        get() = this.getInt(CPUINFO_MINIMUM_FREQ)

    /**
     * Maximum frequency, in Hz.
     */
    val maximumFrequency: Int
        get() = this.getInt(CPUINFO_MAXIMUM_FREQ)

    val coreSiblings: Array<Int>
        get() = this.getIntArray(CPUINFO_CORE_SIBLINGS)

    private fun getInt(subdir: String, default: Int = -1) =
        Companion.getInt("${cpuBaseDir}/${subdir}", default)

    private fun getIntArray(subdir: String, default: Array<Int> = arrayOf()) =
        Companion.getIntArray("${cpuBaseDir}/${subdir}", default)

    companion object {
        private const val CPUINFO_BASE_DIR = "/sys/devices/system/cpu"

        private const val CPUINFO_CURRENT_FREQ = "cpufreq/cpuinfo_cur_freq"
        private const val CPUINFO_MINIMUM_FREQ = "cpufreq/cpuinfo_min_freq"
        private const val CPUINFO_MAXIMUM_FREQ = "cpufreq/cpuinfo_max_freq"
        private const val CPUINFO_CORE_SIBLINGS = "topology/core_siblings_list"

        fun getCPUs() = mutableListOf<CPU>().apply {
            val cpusDir = File("${CPUINFO_BASE_DIR}/")
            val cpusDirs = cpusDir.listFiles { pathname ->
                Pattern.matches("cpu[0-9]+", pathname.name)
            } ?: arrayOf()
            for (dir in cpusDirs) {
                val cpuId = runCatching {
                    dir.name.removePrefix("cpu").toInt()
                }.getOrNull() ?: continue

                this.add(CPU(cpuId))
            }
        }.toTypedArray().apply {
            sortBy { it.id }
        }

        private fun getInt(dir: String, default: Int = -1) = runCatching {
            val file = File(dir)
            file.readLines()[0]
        }.getOrNull()?.toIntOrNull() ?: default

        private fun getIntArray(dir: String, default: Array<Int> = arrayOf()) = runCatching {
            val file = File(dir)
            val text = file.readLines()[0]

            val values = text.split("-")

            val range = mutableListOf<Int>()
            if (values.size == 1) {
                range.add(values.first().toInt())
            } else if (values.size == 2) {
                for (i in values[0].toInt()..values[1].toInt()) {
                    range.add(i)
                }
            }

            return@runCatching range.toTypedArray()
        }.getOrNull() ?: default
    }
}
