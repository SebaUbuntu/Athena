/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.components.Component
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Permission
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.utils.VkUtils
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class GpuComponent : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = GpuComponent()
    }

    override val name = "gpu"

    override val title = LocalizedString(R.string.section_gpu_name)

    override val description = LocalizedString(R.string.section_gpu_description)

    override val drawableResId = R.drawable.ic_display_settings

    override val permissions = setOf<Permission>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = title,
                elements = VkUtils.getVkInfo().withIndex().map { (i, vkPhysicalDevice) ->
                    Element.Card(
                        identifier = identifier / "vulkan_${i}",
                        title = LocalizedString(R.string.gpu_vulkan, i),
                        elements = listOfNotNull(
                            Element.Item(
                                identifier = identifier / "vulkan_${i}" / "api_version",
                                title = LocalizedString(R.string.gpu_vulkan_api_version),
                                value = Value(
                                    "${vkPhysicalDevice.apiVersion.version}",
                                    R.string.gpu_vulkan_api_version_format,
                                    arrayOf(
                                        vkPhysicalDevice.apiVersion.major.toString(),
                                        vkPhysicalDevice.apiVersion.minor.toString(),
                                        vkPhysicalDevice.apiVersion.variant.toString(),
                                        vkPhysicalDevice.apiVersion.patch.toString(),
                                    ),
                                ),
                            ),
                            Element.Item(
                                identifier = identifier / "vulkan_${i}" / "driver_version",
                                title = LocalizedString(R.string.gpu_vulkan_driver_version),
                                value = Value(vkPhysicalDevice.driverVersion),
                            ),
                            Element.Item(
                                identifier = identifier / "vulkan_${i}" / "vendor_id",
                                title = LocalizedString(R.string.gpu_vulkan_vendor_id),
                                value = Value(vkPhysicalDevice.vendorId),
                            ),
                            vkPhysicalDevice.registeredVendorId?.let {
                                Element.Item(
                                    identifier = identifier / "vulkan_${i}" / "registered_vendor_id",
                                    title = LocalizedString(R.string.gpu_vulkan_registered_vendor_id),
                                    value = Value(
                                        it,
                                        VkUtils.vkVendorIdToStringResId,
                                    ),
                                )
                            },
                            Element.Item(
                                identifier = identifier / "vulkan_${i}" / "device_id",
                                title = LocalizedString(R.string.gpu_vulkan_device_id),
                                value = Value(vkPhysicalDevice.deviceId),
                            ),
                            Element.Item(
                                identifier = identifier / "vulkan_${i}" / "device_type",
                                title = LocalizedString(R.string.gpu_vulkan_device_type),
                                value = Value(
                                    vkPhysicalDevice.deviceType,
                                    VkUtils.vkPhysicalDeviceTypeToStringResId,
                                ),
                            ),
                            Element.Item(
                                identifier = identifier / "vulkan_${i}" / "device_name",
                                title = LocalizedString(R.string.gpu_vulkan_device_name),
                                value = Value(vkPhysicalDevice.deviceName),
                            ),
                        ),
                    )
                },
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }
}
