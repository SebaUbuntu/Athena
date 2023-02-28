/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.content.Context
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category
import dev.sebaubuntu.athena.utils.DeviceInfo

object StorageCategory : Category {
    override val name = R.string.section_storage_name
    override val description = R.string.section_storage_description
    override val icon = R.drawable.ic_storage
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mapOf(
        "Partitions" to mapOf(
            "Dynamic partitions" to DeviceInfo.dynamicPartitions,
            "Updatable APEX" to DeviceInfo.updatableApex,
        )
    )
}
