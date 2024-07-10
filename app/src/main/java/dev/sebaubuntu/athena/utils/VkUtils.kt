/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import dev.sebaubuntu.athena.R

object VkUtils {
    enum class VkPhysicalDeviceType(
        val value: ULong,
    ) {
        OTHER(0U),
        INTEGRATED_GPU(1U),
        DISCRETE_GPU(2U),
        VIRTUAL_GPU(3U),
        CPU(4U),
    }

    enum class VkVendorId(
        val value: ULong,
    ) {
        KHRONOS(0x10000U),
        VIV(0x10001U),
        VSI(0x10002U),
        KAZAN(0x10003U),
        CODEPLAY(0x10004U),
        MESA(0x10005U),
        POCL(0x10006U),
        MOBILEYE(0x10007U);

        companion object {
            fun fromValue(value: ULong) = VkVendorId.values().firstOrNull {
                it.value == value
            }
        }
    }

    data class VkApiVersion(
        /**
         * Variant number.
         */
        val variant: UInt,
        /**
         * Major version number.
         */
        val major: UInt,
        /**
         * Minor version number.
         */
        val minor: UInt,

        /**
         * Patch version number.
         */
        val patch: UInt,
    ) {
        val version = 0UL
            .or(variant.shl(29).toULong())
            .or(major.shl(22).toULong())
            .or(minor.shl(12).toULong())
            .or(patch.toULong())

        companion object {
            /**
             * The variant is a 3-bit integer packed into bits 31-29.
             * The major version is a 7-bit integer packed into bits 28-22.
             * The minor version number is a 10-bit integer packed into bits 21-12.
             * The patch version number is a 12-bit integer packed into bits 11-0.
             */
            fun fromVersion(version: ULong): VkApiVersion {
                val variant = version.shr(29).toUInt()
                val major = version.shr(22).and(0x7FU).toUInt()
                val minor = version.shr(12).and(0x3FFU).toUInt()
                val patch = version.and(0xFFFU).toUInt()

                return VkApiVersion(variant, major, minor, patch)
            }
        }
    }

    data class VkPhysicalDevice(
        val apiVersion: VkApiVersion,
        val driverVersion: ULong,
        val vendorId: ULong,
        val deviceId: ULong,
        val deviceType: ULong,
        val deviceName: String,
    ) {
        val registeredVendorId = VkVendorId.fromValue(vendorId)
    }

    class VkPhysicalDevices : ArrayList<VkPhysicalDevice>() {
        fun addDevice(
            apiVersion: Long,
            driverVersion: Long,
            vendorId: Long,
            deviceId: Long,
            deviceType: Long,
            deviceName: String,
        ) = add(
            VkPhysicalDevice(
                VkApiVersion.fromVersion(apiVersion.toULong()),
                driverVersion.toULong(),
                vendorId.toULong(),
                deviceId.toULong(),
                deviceType.toULong(),
                deviceName,
            )
        )
    }

    val vkPhysicalDeviceTypeToStringResId = mapOf(
        VkPhysicalDeviceType.OTHER.value to R.string.vulkan_physical_device_type_other,
        VkPhysicalDeviceType.INTEGRATED_GPU.value to
                R.string.vulkan_physical_device_type_integrated_gpu,
        VkPhysicalDeviceType.DISCRETE_GPU.value to
                R.string.vulkan_physical_device_type_discrete_gpu,
        VkPhysicalDeviceType.VIRTUAL_GPU.value to R.string.vulkan_physical_device_type_virtual_gpu,
        VkPhysicalDeviceType.CPU.value to R.string.vulkan_physical_device_type_cpu,
    )

    val vkVendorIdToStringResId = mapOf(
        VkVendorId.KHRONOS to R.string.vulkan_vendor_khronos,
        VkVendorId.VIV to R.string.vulkan_vendor_viv,
        VkVendorId.VSI to R.string.vulkan_vendor_vsi,
        VkVendorId.KAZAN to R.string.vulkan_vendor_kazan,
        VkVendorId.CODEPLAY to R.string.vulkan_vendor_codeplay,
        VkVendorId.MESA to R.string.vulkan_vendor_mesa,
        VkVendorId.POCL to R.string.vulkan_vendor_pocl,
        VkVendorId.MOBILEYE to R.string.vulkan_vendor_mobileye,
    )

    fun getVkInfo(): VkPhysicalDevices = VkPhysicalDevices().apply {
        getVkInfo(this)
    }

    private external fun getVkInfo(vkPhysicalDevices: VkPhysicalDevices)
}
