/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.flow.asFlow

object DeviceSection : Section(
    "device",
    R.string.section_device_name,
    R.string.section_device_description,
    R.drawable.ic_perm_device_information,
) {
    override fun dataFlow(context: Context) = {
        val activityManager = context.getSystemService(ActivityManager::class.java)
        val memoryInfo = ActivityManager.MemoryInfo().apply {
            activityManager.getMemoryInfo(this)
        }

        listOfNotNull(
            Subsection(
                "general",
                listOf(
                    Information(
                        "device",
                        InformationValue.StringValue(Build.DEVICE),
                        R.string.device_device,
                    ),
                    Information(
                        "brand",
                        InformationValue.StringValue(Build.BRAND),
                        R.string.device_brand,
                    ),
                    Information(
                        "model",
                        InformationValue.StringValue(Build.MODEL),
                        R.string.device_model,
                    ),
                    Information(
                        "manufacturer",
                        InformationValue.StringValue(Build.MANUFACTURER),
                        R.string.device_manufacturer,
                    ),
                    Information(
                        "product_name",
                        InformationValue.StringValue(Build.PRODUCT),
                        R.string.device_product_name,
                    ),
                    Information(
                        "hardware_name",
                        InformationValue.StringValue(Build.HARDWARE),
                        R.string.device_hardware_name,
                    ),
                    Information(
                        "board_name",
                        InformationValue.StringValue(Build.BOARD),
                        R.string.device_board_name,
                    ),
                ),
                R.string.device_general,
            ),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Subsection(
                    "sku",
                    listOf(
                        Information(
                            "hardware_sku",
                            InformationValue.StringValue(Build.SKU),
                            R.string.device_hardware_sku,
                        ),
                        Information(
                            "odm_sku",
                            InformationValue.StringValue(Build.ODM_SKU),
                            R.string.device_odm_sku,
                        ),
                    ),
                    R.string.device_sku,
                )
            } else {
                null
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Subsection(
                    "soc",
                    listOf(
                        Information(
                            "manufacturer",
                            InformationValue.StringValue(Build.SOC_MANUFACTURER),
                            R.string.device_soc_manufacturer,
                        ),
                        Information(
                            "model",
                            InformationValue.StringValue(Build.SOC_MODEL),
                            R.string.device_soc_model,
                        ),
                    ),
                    R.string.device_soc,
                )
            } else {
                null
            },
            Subsection(
                "ram",
                listOfNotNull(
                    Information(
                        "total_memory",
                        InformationValue.BytesValue(memoryInfo.totalMem),
                        R.string.device_ram_total_memory,
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        Information(
                            "advertised_memory",
                            InformationValue.BytesValue(memoryInfo.advertisedMem),
                            R.string.device_ram_advertised_memory,
                        )
                    } else {
                        null
                    },
                    Information(
                        "available_memory",
                        InformationValue.BytesValue(memoryInfo.availMem),
                        R.string.device_ram_available_memory,
                    ),
                    Information(
                        "low_memory_threshold",
                        InformationValue.BytesValue(memoryInfo.threshold),
                        R.string.device_ram_low_memory_threshold,
                    ),
                    Information(
                        "currently_on_low_memory",
                        InformationValue.BooleanValue(memoryInfo.lowMemory),
                        R.string.device_ram_currently_on_low_memory,
                    ),
                ),
                R.string.device_ram,
            ),
        )
    }.asFlow()
}
