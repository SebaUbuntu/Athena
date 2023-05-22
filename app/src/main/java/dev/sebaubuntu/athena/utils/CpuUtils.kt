/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import java.io.File
import java.util.regex.Pattern
import kotlin.reflect.safeCast

object CpuUtils {
    private const val CPUINFO_BASE_DIR = "/sys/devices/system/cpu"

    private const val CPUINFO_MIN_FREQ = "cpufreq/cpuinfo_min_freq"
    private const val CPUINFO_MAX_FREQ = "cpufreq/cpuinfo_max_freq"
    private const val CPUINFO_CORE_SIBLINGS = "topology/core_siblings_list"

    data class Cpu(
        val id: Int,
    ) {
        private val cpuBaseDir = "${CPUINFO_BASE_DIR}/cpu${id}"

        val minFrequency = getInt(CPUINFO_MIN_FREQ) / 1000
        val maxFrequency = getInt(CPUINFO_MAX_FREQ) / 1000
        val coreSiblings = getIntArray(CPUINFO_CORE_SIBLINGS)

        override fun hashCode(): Int {
            return id.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            val obj = Cpu::class.safeCast(other) ?: return false
            return id == obj.id
        }

        private fun getInt(subdir: String, default: Int = -1) = runCatching {
            val file = File("${cpuBaseDir}/${subdir}")
            file.readLines()[0]
        }.getOrNull()?.toIntOrNull() ?: default

        private fun getIntArray(subdir: String, default: Array<Int> = arrayOf()) = runCatching {
            val file = File("${cpuBaseDir}/${subdir}")
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

    val cpus = mutableListOf<Cpu>().apply {
        val cpusDir = File("${CPUINFO_BASE_DIR}/")
        val cpusDirs = cpusDir.listFiles { pathname ->
            Pattern.matches("cpu[0-9]+", pathname.name)
        } ?: arrayOf()
        for (dir in cpusDirs) {
            val cpuId = runCatching {
                dir.name.removePrefix("cpu").toInt()
            }.getOrNull() ?: continue

            this.add(Cpu(cpuId))
        }
    }.toTypedArray().apply {
        sortBy { it.id }
    }
}
