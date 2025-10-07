/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

/**
 * `struct cpuinfo_tlb`
 */
data class Tlb(
    val entries: UInt,
    val associativity: UInt,
    val pages: ULong,
) {
    companion object {
        @JvmStatic
        fun fromCpuInfo(
            entries: Int,
            associativity: Int,
            pages: Long,
        ) = Tlb(
            entries.toUInt(),
            associativity.toUInt(),
            pages.toULong(),
        )
    }
}
