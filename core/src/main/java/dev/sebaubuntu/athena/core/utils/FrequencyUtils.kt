/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.utils

import java.text.DecimalFormat

object FrequencyUtils {
    enum class SizeUnit(
        val unitBase: Long
    ) {
        Hz(1L),
        kHz(Hz.unitBase * 1000),
        MHz(kHz.unitBase * 1000),
        GHz(MHz.unitBase * 1000),
        THz(GHz.unitBase * 1000),
        PHz(THz.unitBase * 1000),
        EHz(PHz.unitBase * 1000);

        companion object {
            fun unitsInDescending() = entries.asReversed()
        }
    }

    private val DEC_FORMAT = DecimalFormat("#.##")

    private fun formatSize(size: Long, divider: Long, unitName: String) =
        "${DEC_FORMAT.format(size.toDouble() / divider)} $unitName"

    fun toHumanReadable(sizeHz: Long): String {
        val units = SizeUnit.unitsInDescending()
        require(sizeHz >= 0) { "Invalid frequency: $sizeHz" }
        var result: String? = null
        for (unit in units) {
            if (sizeHz >= unit.unitBase) {
                result = formatSize(sizeHz, unit.unitBase, unit.name)
                break
            }
        }
        return result ?: formatSize(
            sizeHz, SizeUnit.Hz.unitBase, SizeUnit.Hz.name
        )
    }
}
