/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

/**
 * `struct cpuinfo_cache`
 */
data class Cache(
    /**
     * Cache size in bytes
     */
    val size: UInt,

    /**
     * Number of ways of associativity
     */
    val associativity: UInt,

    /**
     * Number of sets
     */
    val sets: UInt,

    /**
     * Number of partitions
     */
    val partitions: UInt,

    /**
     * Line size in bytes
     */
    val lineSize: UInt,

    /**
     * Binary characteristics of the cache (unified cache, inclusive cache,
     * cache with complex indexing).
     *
     * @see `CPUINFO_CACHE_UNIFIED`, `CPUINFO_CACHE_INCLUSIVE`,
     *      `CPUINFO_CACHE_COMPLEX_INDEXING`
     */
    val flags: UInt,

    /**
     * Index of the first logical processor that shares this cache
     */
    val processorStart: UInt,

    /**
     * Number of logical processors that share this cache
     */
    val processorCount: UInt,
) {
    companion object {
        @JvmStatic
        fun fromCpuInfo(
            size: Int,
            associativity: Int,
            sets: Int,
            partitions: Int,
            lineSize: Int,
            flags: Int,
            processorStart: Int,
            processorCount: Int,
        ) = Cache(
            size.toUInt(),
            associativity.toUInt(),
            sets.toUInt(),
            partitions.toUInt(),
            lineSize.toUInt(),
            flags.toUInt(),
            processorStart.toUInt(),
            processorCount.toUInt(),
        )
    }
}
