/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

/**
 * `struct cpuinfo_trace_cache`
 */
data class TraceCache(
    val uops: UInt,
    val associativity: UInt,
) {
    companion object {
        @JvmStatic
        fun fromCpuInfo(
            uops: Int,
            associativity: Int,
        ) = TraceCache(
            uops.toUInt(),
            associativity.toUInt(),
        )
    }
}
