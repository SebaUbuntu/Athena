/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.gpu

import android.content.Context
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.modules.gpu.models.VkPhysicalDeviceType
import dev.sebaubuntu.athena.modules.gpu.models.VkVendorId
import dev.sebaubuntu.athena.modules.gpu.utils.EglUtils
import dev.sebaubuntu.athena.modules.gpu.utils.VkUtils
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class GpuModule : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = GpuModule()
    }

    override val id = "gpu"

    override val name = LocalizedString(R.string.section_gpu_name)

    override val description = LocalizedString(R.string.section_gpu_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_display_settings

    override val requiredPermissions = arrayOf<String>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = VkUtils.getVkInfo().withIndex().map { (i, vkPhysicalDevice) ->
                    EglUtils.getEglInformation()
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
                                        vkVendorIdToStringResId,
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
                                    vkPhysicalDeviceTypeToStringResId,
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

    companion object {
        private val vkPhysicalDeviceTypeToStringResId = mapOf(
            VkPhysicalDeviceType.OTHER.value to R.string.vulkan_physical_device_type_other,
            VkPhysicalDeviceType.INTEGRATED_GPU.value to
                    R.string.vulkan_physical_device_type_integrated_gpu,
            VkPhysicalDeviceType.DISCRETE_GPU.value to
                    R.string.vulkan_physical_device_type_discrete_gpu,
            VkPhysicalDeviceType.VIRTUAL_GPU.value to R.string.vulkan_physical_device_type_virtual_gpu,
            VkPhysicalDeviceType.CPU.value to R.string.vulkan_physical_device_type_cpu,
        )

        private val vkVendorIdToStringResId = mapOf(
            VkVendorId.KHRONOS to R.string.vulkan_vendor_khronos,
            VkVendorId.VIV to R.string.vulkan_vendor_viv,
            VkVendorId.VSI to R.string.vulkan_vendor_vsi,
            VkVendorId.KAZAN to R.string.vulkan_vendor_kazan,
            VkVendorId.CODEPLAY to R.string.vulkan_vendor_codeplay,
            VkVendorId.MESA to R.string.vulkan_vendor_mesa,
            VkVendorId.POCL to R.string.vulkan_vendor_pocl,
            VkVendorId.MOBILEYE to R.string.vulkan_vendor_mobileye,
        )

        init {
            System.loadLibrary("athena_gpu")
        }
    }
}
