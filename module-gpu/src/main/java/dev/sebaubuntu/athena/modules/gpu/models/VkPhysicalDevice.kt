/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.gpu.models

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
