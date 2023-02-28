/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.content.Context
import android.os.Environment
import android.os.StatFs
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.BytesUtils
import dev.sebaubuntu.athena.utils.Category
import dev.sebaubuntu.athena.utils.DeviceInfo

object StorageCategory : Category {
    override val name = R.string.section_storage_name
    override val description = R.string.section_storage_description
    override val icon = R.drawable.ic_storage
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
        val internalStatFs = StatFs(Environment.getDataDirectory().absolutePath)
        val internalTotal = internalStatFs.blockCountLong * internalStatFs.blockSizeLong
        val internalFree = internalStatFs.availableBlocksLong * internalStatFs.blockSizeLong
        this["Internal storage"] = mapOf(
            "Total" to BytesUtils.toHumanReadableSIPrefixes(internalTotal),
            "Available" to BytesUtils.toHumanReadableSIPrefixes(internalFree),
            "Used" to BytesUtils.toHumanReadableSIPrefixes(internalTotal - internalFree),
        )

        val externalStatFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
        val externalTotal = externalStatFs.blockCountLong * externalStatFs.blockSizeLong
        val externalFree = externalStatFs.availableBlocksLong * externalStatFs.blockSizeLong
        this["External storage"] = mapOf(
            "Is emulated" to "${Environment.isExternalStorageEmulated()}",
            "Is removable" to "${Environment.isExternalStorageRemovable()}",
            "Total" to BytesUtils.toHumanReadableSIPrefixes(externalTotal),
            "Available" to BytesUtils.toHumanReadableSIPrefixes(externalFree),
            "Used" to BytesUtils.toHumanReadableSIPrefixes(externalTotal - externalFree),
        )

        this["System partitions"] = mapOf(
            "Dynamic partitions" to DeviceInfo.dynamicPartitions,
            "Updatable APEX" to DeviceInfo.updatableApex,
        )
    }
}
