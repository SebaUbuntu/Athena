/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import java.text.DecimalFormat

object BytesUtils {
    internal enum class SizeUnitBinaryPrefixes(
        val unitBase: Long
    ) {
        Bytes(1L),
        KiB(Bytes.unitBase shl 10),
        MiB(KiB.unitBase shl 10),
        GiB(MiB.unitBase shl 10),
        TiB(GiB.unitBase shl 10),
        PiB(TiB.unitBase shl 10),
        EiB(PiB.unitBase shl 10);

        companion object {
            fun unitsInDescending() = entries.asReversed()
        }
    }

    internal enum class SizeUnitSIPrefixes(
        val unitBase: Long
    ) {
        Bytes(1L),
        KB(Bytes.unitBase * 1000),
        MB(KB.unitBase * 1000),
        GB(MB.unitBase * 1000),
        TB(GB.unitBase * 1000),
        PB(TB.unitBase * 1000),
        EB(PB.unitBase * 1000);

        companion object {
            fun unitsInDescending() = entries.asReversed()
        }
    }

    private val DEC_FORMAT = DecimalFormat("#.##")

    private fun formatSize(size: Long, divider: Long, unitName: String) =
        DEC_FORMAT.format(size.toDouble() / divider) + " " + unitName

    fun toHumanReadableBinaryPrefixes(size: Long): String {
        val units = SizeUnitBinaryPrefixes.unitsInDescending()
        require(size >= 0) { "Invalid file size: $size" }
        var result: String? = null
        for (unit in units) {
            if (size >= unit.unitBase) {
                result = formatSize(size, unit.unitBase, unit.name)
                break
            }
        }
        return result ?: formatSize(
            size, SizeUnitBinaryPrefixes.Bytes.unitBase, SizeUnitBinaryPrefixes.Bytes.name
        )
    }

    fun toHumanReadableSIPrefixes(size: Long): String {
        val units = SizeUnitSIPrefixes.unitsInDescending()
        require(size >= 0) { "Invalid file size: $size" }
        var result: String? = null
        for (unit in units) {
            if (size >= unit.unitBase) {
                result = formatSize(size, unit.unitBase, unit.name)
                break
            }
        }
        return result ?: formatSize(
            size, SizeUnitSIPrefixes.Bytes.unitBase, SizeUnitSIPrefixes.Bytes.name
        )
    }
}
