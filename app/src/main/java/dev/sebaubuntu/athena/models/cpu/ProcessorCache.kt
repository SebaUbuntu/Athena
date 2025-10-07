/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

/**
 * `struct cpuinfo_processor { struct {...} cache }`
 */
data class ProcessorCache(
    /**
     * Level 1 instruction cache
     */
    val l1i: Cache?,
    /**
     * Level 1 data cache
     */
    val l1d: Cache?,
    /**
     * Level 2 unified or data cache
     */
    val l2: Cache?,
    /**
     * Level 3 unified or data cache
     */
    val l3: Cache?,
    /**
     * Level 4 unified or data cache
     */
    val l4: Cache?,
) {
    companion object {
        @JvmStatic
        fun fromCpuInfo(
            l1i: Cache?,
            l1d: Cache?,
            l2: Cache?,
            l3: Cache?,
            l4: Cache?,
        ) = ProcessorCache(
            l1i,
            l1d,
            l2,
            l3,
            l4,
        )
    }
}
