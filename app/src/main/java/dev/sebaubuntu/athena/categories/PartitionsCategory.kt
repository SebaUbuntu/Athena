/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.content.Context
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category
import dev.sebaubuntu.athena.utils.DeviceInfo

object PartitionsCategory : Category {
    override val name = R.string.section_partitions_name
    override val description = R.string.section_partitions_description
    override val icon = R.drawable.ic_partitions
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mapOf(
        "Partitions" to mapOf(
            "Dynamic partitions" to DeviceInfo.dynamicPartitions,
            "Updatable APEX" to DeviceInfo.updatableApex,
        )
    )
}
