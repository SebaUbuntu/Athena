/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.SystemProperties

object PropsSection : Section() {
    override val name = R.string.section_props_name
    override val description = R.string.section_props_description
    override val icon = R.drawable.ic_build
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mapOf(
        "Props" to SystemProperties.props
    )

    override val navigationActionId = R.id.action_mainFragment_to_propsFragment
}
