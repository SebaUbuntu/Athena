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
import dev.sebaubuntu.athena.modules.gpu.models.EglInformation
import dev.sebaubuntu.athena.modules.gpu.models.GlInformation
import dev.sebaubuntu.athena.modules.gpu.models.VkPhysicalDevice
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
            val vkPhysicalDevices = VkUtils.getVkInfo()
            val eglInformation = EglUtils.getEglInformation()

            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = buildList {
                    vkPhysicalDevices?.withIndex()?.forEach { (i, vkPhysicalDevice) ->
                        add(vkPhysicalDevice.getCard(i))
                    }

                    eglInformation?.let { eglInformation ->
                        add(eglInformation.getCard())

                        eglInformation.glInformation?.let {
                            add(it.getCard())
                        }
                    }
                },
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }

    private fun VkPhysicalDevice.getCard(index: Int) = Element.Card(
        name = "vulkan_${index}",
        title = LocalizedString(R.string.gpu_vulkan_device, index),
        elements = listOfNotNull(
            Element.Item(
                name = "api_version",
                title = LocalizedString(R.string.gpu_vulkan_api_version),
                value = Value(
                    "${apiVersion.version}",
                    R.string.gpu_vulkan_api_version_format,
                    apiVersion.major.toString(),
                    apiVersion.minor.toString(),
                    apiVersion.variant.toString(),
                    apiVersion.patch.toString(),
                ),
            ),
            Element.Item(
                name = "driver_version",
                title = LocalizedString(R.string.gpu_vulkan_driver_version),
                value = Value(driverVersion),
            ),
            Element.Item(
                name = "vendor_id",
                title = LocalizedString(R.string.gpu_vulkan_vendor_id),
                value = Value(vendorId),
            ),
            registeredVendorId?.let {
                Element.Item(
                    name = "registered_vendor_id",
                    title = LocalizedString(R.string.gpu_vulkan_registered_vendor_id),
                    value = Value(
                        it,
                        vkVendorIdToStringResId,
                    ),
                )
            },
            Element.Item(
                name = "device_id",
                title = LocalizedString(R.string.gpu_vulkan_device_id),
                value = Value(deviceId),
            ),
            Element.Item(
                name = "device_type",
                title = LocalizedString(R.string.gpu_vulkan_device_type),
                value = Value(
                    deviceType,
                    vkPhysicalDeviceTypeToStringResId,
                ),
            ),
            Element.Item(
                name = "device_name",
                title = LocalizedString(R.string.gpu_vulkan_device_name),
                value = Value(deviceName),
            ),
        ),
    )

    private fun EglInformation.getCard() = Element.Card(
        name = "egl",
        title = LocalizedString(R.string.gpu_egl),
        elements = listOfNotNull(
            eglVendor?.let { vendor ->
                Element.Item(
                    name = "vendor",
                    title = LocalizedString(R.string.gpu_egl_vendor),
                    value = Value(vendor),
                )
            },
            eglVersion?.let { version ->
                Element.Item(
                    name = "version",
                    title = LocalizedString(R.string.gpu_egl_version),
                    value = Value(version),
                )
            },
            eglExtensions?.let { extensions ->
                Element.Item(
                    name = "extensions",
                    title = LocalizedString(R.string.gpu_egl_extensions),
                    value = Value(extensions.toTypedArray()),
                )
            },
            eglClientApi?.let { clientApi ->
                Element.Item(
                    name = "client_api",
                    title = LocalizedString(R.string.gpu_egl_client_api),
                    value = Value(clientApi.toTypedArray()),
                )
            },
        ),
    )

    private fun GlInformation.getCard() = Element.Card(
        name = "opengl",
        title = LocalizedString(R.string.gpu_opengl),
        elements = listOfNotNull(
            glVendor?.let { glVendor ->
                Element.Item(
                    name = "vendor",
                    title = LocalizedString(R.string.gpu_opengl_vendor),
                    value = Value(glVendor),
                )
            },
            glRenderer?.let { glRenderer ->
                Element.Item(
                    name = "renderer",
                    title = LocalizedString(R.string.gpu_opengl_renderer),
                    value = Value(glRenderer),
                )
            },
            glVersion?.let { glVersion ->
                Element.Item(
                    name = "version",
                    title = LocalizedString(R.string.gpu_opengl_version),
                    value = Value(glVersion),
                )
            },
            glExtensions?.let { glExtensions ->
                Element.Item(
                    name = "extensions",
                    title = LocalizedString(R.string.gpu_opengl_extensions),
                    value = Value(glExtensions),
                )
            },
        ),
    )

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
