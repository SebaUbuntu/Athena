/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.gpu.utils

import dev.sebaubuntu.athena.modules.gpu.models.VkApiVersion
import dev.sebaubuntu.athena.modules.gpu.models.VkPhysicalDevice

object VkUtils {
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

    external fun getVkInfo(): VkPhysicalDevices?
}
