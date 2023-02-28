/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.BytesUtils
import dev.sebaubuntu.athena.utils.Category

object DeviceCategory : Category {
    override val name = R.string.section_device_name
    override val description = R.string.section_device_description
    override val icon = R.drawable.ic_device
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
        val activityManager = context.getSystemService(ActivityManager::class.java)
        val memoryInfo = ActivityManager.MemoryInfo().apply {
            activityManager.getMemoryInfo(this)
        }

        val internalStatFs = StatFs(Environment.getDataDirectory().absolutePath)
        val internalTotal =
            internalStatFs.blockCountLong * internalStatFs.blockSizeLong
        val internalFree =
            internalStatFs.availableBlocksLong * internalStatFs.blockSizeLong

        val externalStatFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
        val externalTotal =
            externalStatFs.blockCountLong * externalStatFs.blockSizeLong
        val externalFree =
            externalStatFs.availableBlocksLong * externalStatFs.blockSizeLong

        this["General"] = mutableMapOf(
            "Device" to Build.DEVICE,
            "Brand" to Build.BRAND,
            "Model" to Build.MODEL,
            "Manufacturer" to Build.MANUFACTURER,
            "Product name" to Build.PRODUCT,
            "Hardware name" to Build.HARDWARE,
            "Board name" to Build.BOARD,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this["SKU"] = mapOf(
                "Hardware SKU" to Build.SKU,
                "ODM SKU" to Build.ODM_SKU,
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this["SoC"] = mapOf(
                "Manufacturer" to Build.SOC_MANUFACTURER,
                "Model" to Build.SOC_MODEL,
            )
        }

        this["RAM"] = mapOf(
            "Total memory" to BytesUtils.toHumanReadableSIPrefixes(memoryInfo.totalMem),
            "Available memory" to BytesUtils.toHumanReadableSIPrefixes(memoryInfo.availMem),
            "Is a low memory system" to "${memoryInfo.lowMemory}",
        )

        this["Internal storage"] = mapOf(
            "Total" to BytesUtils.toHumanReadableSIPrefixes(internalTotal),
            "Available" to BytesUtils.toHumanReadableSIPrefixes(internalFree),
            "Used" to BytesUtils.toHumanReadableSIPrefixes(internalTotal - internalFree),
        )

        this["External storage"] = mapOf(
            "Is emulated" to "${Environment.isExternalStorageEmulated()}",
            "Is removable" to "${Environment.isExternalStorageRemovable()}",
            "Total" to BytesUtils.toHumanReadableSIPrefixes(externalTotal),
            "Available" to BytesUtils.toHumanReadableSIPrefixes(externalFree),
            "Used" to BytesUtils.toHumanReadableSIPrefixes(externalTotal - externalFree),
        )
    }.toMap()
}
