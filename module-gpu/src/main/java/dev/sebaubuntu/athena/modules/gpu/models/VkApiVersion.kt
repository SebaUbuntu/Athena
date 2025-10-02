/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.gpu.models

data class VkApiVersion(
    /**
     * Variant number.
     */
    val variant: UInt,
    /**
     * Major version number.
     */
    val major: UInt,
    /**
     * Minor version number.
     */
    val minor: UInt,

    /**
     * Patch version number.
     */
    val patch: UInt,
) {
    val version = 0UL
        .or(variant.shl(29).toULong())
        .or(major.shl(22).toULong())
        .or(minor.shl(12).toULong())
        .or(patch.toULong())

    companion object {
        /**
         * The variant is a 3-bit integer packed into bits 31-29.
         * The major version is a 7-bit integer packed into bits 28-22.
         * The minor version number is a 10-bit integer packed into bits 21-12.
         * The patch version number is a 12-bit integer packed into bits 11-0.
         */
        fun fromVersion(version: ULong): VkApiVersion {
            val variant = version.shr(29).toUInt()
            val major = version.shr(22).and(0x7FU).toUInt()
            val minor = version.shr(12).and(0x3FFU).toUInt()
            val patch = version.and(0xFFFU).toUInt()

            return VkApiVersion(variant, major, minor, patch)
        }
    }
}
