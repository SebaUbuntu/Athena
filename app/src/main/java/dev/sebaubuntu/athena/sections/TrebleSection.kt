/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.DeviceInfo
import dev.sebaubuntu.athena.vintf.VINTFUtils

object TrebleSection : Section() {
    override val name = R.string.section_treble_name
    override val description = R.string.section_treble_description
    override val icon = R.drawable.ic_treble
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mapOf<String, Map<String, String>>()

    override val navigationActionId = R.id.action_mainFragment_to_trebleFragment
}
