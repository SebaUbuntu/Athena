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

object StorageCategory : Category() {
    override val name = R.string.section_storage_name
    override val description = R.string.section_storage_description
    override val icon = R.drawable.ic_storage
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
        val internalStatFs = StatFs(Environment.getDataDirectory().absolutePath)
        val internalTotal = internalStatFs.blockCountLong * internalStatFs.blockSizeLong
        val internalFree = internalStatFs.availableBlocksLong * internalStatFs.blockSizeLong
        this["Internal storage"] = mapOf(
            "Is encrypted" to (DeviceInfo.isDataEncrypted?.toString() ?: "Unknown"),
            "Encryption type" to when (DeviceInfo.dataEncryptionType) {
                DeviceInfo.EncryptionType.NONE -> "None"
                DeviceInfo.EncryptionType.FDE -> "FDE"
                DeviceInfo.EncryptionType.FBE -> "FBE"
                else -> "Unknown"
            },
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

        this["System partitions"] = mutableMapOf<String, String>().apply {
            this["Has updatable APEX"] = (DeviceInfo.hasUpdatableApex?.toString() ?: "Unknown")
            this["Uses system as root"] = (DeviceInfo.usesSystemAsRoot?.toString() ?: "Unknown")
            this["Uses A/B"] = (DeviceInfo.usesAb?.toString() ?: "Unknown")
            if (DeviceInfo.abOtaPartitions.isNotEmpty()) {
                this["A/B OTA partitions"] = DeviceInfo.abOtaPartitions.joinToString()
            }
            this["Uses dynamic partitions"] =
                    (DeviceInfo.usesDynamicPartitions?.toString() ?: "Unknown")
            this["Uses retrofitted dynamic partitions"] =
                    (DeviceInfo.usesRetrofittedDynamicPartitions?.toString() ?: "Unknown")
            this["Uses virtual A/B"] = (DeviceInfo.usesVab?.toString() ?: "Unknown")
            this["Uses retrofitted virtual A/B"] =
                    (DeviceInfo.usesRetrofittedVab?.toString() ?: "Unknown")
            this["Uses compressed virtual A/B"] = (DeviceInfo.usesVabc?.toString() ?: "Unknown")
        }
    }
}
