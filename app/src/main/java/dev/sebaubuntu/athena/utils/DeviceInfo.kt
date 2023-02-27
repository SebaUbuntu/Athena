/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

object DeviceInfo {
    // Bluetooth
    val a2dpOffloadDisabled =
        SystemProperties.getProp<Boolean?>("persist.bluetooth.a2dp_offload.disabled")
    val a2dpOffloadSupported =
        SystemProperties.getProp<Boolean?>("ro.bluetooth.a2dp_offload.supported")

    // Build
    val buildCharacteristics = SystemProperties.getProp<String>("ro.build.characteristics")
    val buildFlavor = SystemProperties.getProp<String>("ro.build.flavor")
    val buildHeadless = SystemProperties.getProp<String>("ro.build.headless")
    val buildVersionMinSupportedTargetSdk =
        SystemProperties.getProp<String>("ro.build.version.min_supported_target_sdk")

    // Kernel
    val kernelVersion = SystemProperties.getProp<String>("ro.kernel.version", "unknown")

    // Partitions
    val dynamicPartitions = SystemProperties.getProp<String>("ro.boot.dynamic_partitions")
    val updatableApex = SystemProperties.getProp<String>("ro.apex.updatable")

    // Product
    val productFirstApiLevel = SystemProperties.getProp<String>("ro.product.first_api_level")

    // Security
    val adbSecure = SystemProperties.getProp<String>("ro.adb.secure")
    val avbVersion = SystemProperties.getProp<String>("ro.boot.avb_version")
    val secure = SystemProperties.getProp<String>("ro.secure")
    val verifiedBootState = SystemProperties.getProp<String>("ro.boot.verifiedbootstate")

    // Treble/VNDK
    val trebleEnabled = SystemProperties.getProp<String>("ro.treble.enabled")
    val vndkVersion = SystemProperties.getProp<String>("ro.vndk.version")
}
