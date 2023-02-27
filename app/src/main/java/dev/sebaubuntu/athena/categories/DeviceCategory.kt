/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.content.Context
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category

object DeviceCategory : Category {
    override val name = R.string.section_device_name
    override val description = R.string.section_device_description
    override val icon = R.drawable.ic_device
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
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
    }.toMap()
}
