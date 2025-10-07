/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import dev.sebaubuntu.athena.utils.VkUtils
import kotlinx.coroutines.flow.asFlow

object GpuSection : Section(
    "gpu",
    R.string.section_gpu_name,
    R.string.section_gpu_description,
    R.drawable.ic_display_settings,
    navigationActionId = R.id.action_mainFragment_to_gpuFragment,
) {
    override fun dataFlow(context: Context) = {
        listOf(
            *VkUtils.getVkInfo().takeIf { it.isNotEmpty() }?.withIndex()?.map { (i, vkDevice) ->
                Subsection(
                    "vulkan_device_$i",
                    listOfNotNull(
                        Information(
                            "api_version",
                            InformationValue.StringValue(
                                "${vkDevice.apiVersion.version}",
                                R.string.gpu_vulkan_api_version_format,
                                arrayOf(
                                    vkDevice.apiVersion.major.toString(),
                                    vkDevice.apiVersion.minor.toString(),
                                    vkDevice.apiVersion.variant.toString(),
                                    vkDevice.apiVersion.patch.toString(),
                                ),
                            ),
                            R.string.gpu_vulkan_api_version,
                        ),
                        Information(
                            "driver_version",
                            InformationValue.ULongValue(vkDevice.driverVersion),
                            R.string.gpu_vulkan_driver_version,
                        ),
                        Information(
                            "vendor_id",
                            InformationValue.ULongValue(vkDevice.vendorId),
                            R.string.gpu_vulkan_vendor_id,
                        ),
                        vkDevice.registeredVendorId?.let {
                            Information(
                                "registered_vendor_id",
                                InformationValue.EnumValue(
                                    it,
                                    VkUtils.vkVendorIdToStringResId,
                                ),
                                R.string.gpu_vulkan_registered_vendor_id,
                            )
                        },
                        Information(
                            "device_id",
                            InformationValue.ULongValue(vkDevice.deviceId),
                            R.string.gpu_vulkan_device_id,
                        ),
                        Information(
                            "device_type",
                            InformationValue.ULongValue(
                                vkDevice.deviceType,
                                VkUtils.vkPhysicalDeviceTypeToStringResId,
                            ),
                            R.string.gpu_vulkan_device_type,
                        ),
                        Information(
                            "device_name",
                            InformationValue.StringValue(vkDevice.deviceName),
                            R.string.gpu_vulkan_device_name,
                        ),
                    ),
                    R.string.gpu_vulkan_device,
                    arrayOf(i),
                )
            }?.toTypedArray() ?: listOf(
                Subsection(
                    "vulkan",
                    listOf(
                        Information(
                            "supported",
                            InformationValue.BooleanValue(false),
                            R.string.gpu_vulkan_supported,
                        )
                    ),
                    R.string.gpu_vulkan,
                )
            ).toTypedArray()
        )
    }.asFlow()
}
