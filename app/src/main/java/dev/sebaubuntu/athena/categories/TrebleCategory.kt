/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.content.Context
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category
import dev.sebaubuntu.athena.utils.DeviceInfo
import dev.sebaubuntu.athena.utils.VintfUtils

object TrebleCategory : Category {
    override val name = R.string.section_treble_name
    override val description = R.string.section_treble_description
    override val icon = R.drawable.ic_treble
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mapOf(
        "General" to mapOf(
            "Treble enabled" to DeviceInfo.trebleEnabled,
            "VNDK version" to DeviceInfo.vndkVersion,
        ),
        "HALs" to VintfUtils.halList.associate {
            it.name to it.type.toString()
        }
    )
}
