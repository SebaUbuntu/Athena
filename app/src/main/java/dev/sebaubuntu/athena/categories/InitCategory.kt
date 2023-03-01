/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.content.Context
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category
import dev.sebaubuntu.athena.utils.SystemProperties

object InitCategory : Category() {
    override val name = R.string.section_init_name
    override val description = R.string.section_init_description
    override val icon = R.drawable.ic_init
    override val requiredPermissions = arrayOf<String>()

    private const val INIT_SERVICE_PREFIX = "init.svc."

    override fun getInfo(context: Context) = mapOf(
        "Services" to SystemProperties.props.filterKeys { it.startsWith(INIT_SERVICE_PREFIX) }.map {
            it.key.removePrefix(INIT_SERVICE_PREFIX) to it.value
        }.toMap()
    )
}
