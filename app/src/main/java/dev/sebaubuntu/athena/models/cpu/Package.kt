/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

data class Package(
    /**
     * SoC or processor chip model name
     */
    val name: String,

    /**
     * Index of the first logical processor on this physical package
     */
    val processorStart: UInt,

    /**
     * Number of logical processors on this physical package
     */
    val processorCount: UInt,

    /**
     * Index of the first core on this physical package
     */
    val coreStart: UInt,

    /**
     * Number of cores on this physical package
     */
    val coreCount: UInt,

    /**
     * Index of the first cluster of cores on this physical package
     */
    val clusterStart: UInt,

    /**
     * Number of clusters of cores on this physical package
     */
    val clusterCount: UInt,
) {
    companion object {
        @JvmStatic
        fun fromCpuInfo(
            name: String,
            processorStart: Int,
            processorCount: Int,
            coreStart: Int,
            coreCount: Int,
            clusterStart: Int,
            clusterCount: Int,
        ) = Package(
            name,
            processorStart.toUInt(),
            processorCount.toUInt(),
            coreStart.toUInt(),
            coreCount.toUInt(),
            clusterStart.toUInt(),
            clusterCount.toUInt(),
        )
    }
}
