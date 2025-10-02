/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.device

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class DeviceModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = DeviceModule(context)
    }

    private val activityManager = context.getSystemService(ActivityManager::class.java)

    override val id = "device"

    override val name = LocalizedString(R.string.section_device_name)

    override val description = LocalizedString(R.string.section_device_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_perm_device_information

    override val requiredPermissions = arrayOf<String>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOfNotNull(
                    Element.Card(
                        name = "general",
                        title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                        elements = listOf(
                            Element.Item(
                                name = "device",
                                title = LocalizedString(R.string.device_device),
                                value = Value(Build.DEVICE),
                            ),
                            Element.Item(
                                name = "brand",
                                title = LocalizedString(R.string.device_brand),
                                value = Value(Build.BRAND),
                            ),
                            Element.Item(
                                name = "model",
                                title = LocalizedString(R.string.device_model),
                                value = Value(Build.MODEL),
                            ),
                            Element.Item(
                                name = "manufacturer",
                                title = LocalizedString(R.string.device_manufacturer),
                                value = Value(Build.MANUFACTURER),
                            ),
                            Element.Item(
                                name = "product_name",
                                title = LocalizedString(R.string.device_product_name),
                                value = Value(Build.PRODUCT),
                            ),
                            Element.Item(
                                name = "hardware_name",
                                title = LocalizedString(R.string.device_hardware_name),
                                value = Value(Build.HARDWARE),
                            ),
                            Element.Item(
                                name = "board_name",
                                title = LocalizedString(R.string.device_board_name),
                                value = Value(Build.BOARD),
                            ),
                        ),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Element.Card(
                            name = "sku",
                            title = LocalizedString(R.string.device_sku),
                            elements = listOf(
                                Element.Item(
                                    name = "hardware_sku",
                                    title = LocalizedString(R.string.device_hardware_sku),
                                    value = Value(Build.SKU),
                                ),
                                Element.Item(
                                    name = "odm_sku",
                                    title = LocalizedString(R.string.device_odm_sku),
                                    value = Value(Build.ODM_SKU),
                                ),
                            ),
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Element.Card(
                            name = "soc",
                            title = LocalizedString(R.string.device_soc),
                            elements = listOf(
                                Element.Item(
                                    name = "manufacturer",
                                    title = LocalizedString(R.string.device_soc_manufacturer),
                                    value = Value(Build.SOC_MANUFACTURER),
                                ),
                                Element.Item(
                                    name = "model",
                                    title = LocalizedString(R.string.device_soc_model),
                                    value = Value(Build.SOC_MODEL),
                                ),
                            ),
                        )
                    } else {
                        null
                    },
                    ActivityManager.MemoryInfo().apply {
                        activityManager.getMemoryInfo(this)
                    }.let { memoryInfo ->
                        Element.Card(
                            name = "ram",
                            title = LocalizedString(R.string.device_ram),
                            elements = listOfNotNull(
                                Element.Item(
                                    name = "total_memory",
                                    title = LocalizedString(R.string.device_ram_total_memory),
                                    value = Value.BytesValue(memoryInfo.totalMem),
                                ),
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                    Element.Item(
                                        name = "advertised_memory",
                                        title = LocalizedString(R.string.device_ram_advertised_memory),
                                        value = Value.BytesValue(memoryInfo.advertisedMem),
                                    )
                                } else {
                                    null
                                },
                                Element.Item(
                                    name = "available_memory",
                                    title = LocalizedString(R.string.device_ram_available_memory),
                                    value = Value.BytesValue(memoryInfo.availMem),
                                ),
                                Element.Item(
                                    name = "low_memory_threshold",
                                    title = LocalizedString(R.string.device_ram_low_memory_threshold),
                                    value = Value.BytesValue(memoryInfo.threshold),
                                ),
                                Element.Item(
                                    name = "currently_on_low_memory",
                                    title = LocalizedString(R.string.device_ram_currently_on_low_memory),
                                    value = Value(memoryInfo.lowMemory),
                                ),
                            ),
                        )
                    },
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }
}
