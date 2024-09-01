/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

object DeviceInfo {
    enum class EncryptionType {
        NONE,
        FDE,
        FBE,
    }

    // Build
    val buildCharacteristics = SystemProperties.getString("ro.build.characteristics")
    val buildFlavor = SystemProperties.getString("ro.build.flavor")
    val buildHeadless = SystemProperties.getString("ro.build.headless")
    val buildVersionMinSupportedTargetSdk =
        SystemProperties.getString("ro.build.version.min_supported_target_sdk")

    // Partitions
    val isDataEncrypted = when (SystemProperties.getString("ro.crypto.state", "unknown")) {
        "encrypted" -> true
        "unencrypted" -> false
        else -> null
    }
    val dataEncryptionType = when (SystemProperties.getString("ro.crypto.type", "unknown")) {
        "none" -> EncryptionType.NONE
        "block" -> EncryptionType.FDE
        "file" -> EncryptionType.FBE
        else -> null
    }
    val hasUpdatableApex = SystemProperties.getBoolean("ro.apex.updatable")
    val usesSystemAsRoot = SystemProperties.getBoolean("ro.build.system_root_image")
    val usesAb = SystemProperties.getBoolean("ro.build.ab_update")
    val abOtaPartitions = SystemProperties.getString("ro.product.ab_ota_partitions")
        ?.split(",")

    val usesDynamicPartitions = SystemProperties.getBoolean("ro.boot.dynamic_partitions")
    val usesRetrofittedDynamicPartitions =
        SystemProperties.getBoolean("ro.boot.dynamic_partitions_retrofit")
    val usesVab = SystemProperties.getBoolean("ro.virtual_ab.enabled")
    val usesRetrofittedVab = SystemProperties.getBoolean("ro.virtual_ab.retrofit")
    val usesVabc = SystemProperties.getBoolean("ro.virtual_ab.compression.enabled")

    // Product
    val productFirstApiLevel = SystemProperties.getString("ro.product.first_api_level")

    // Security
    val adbSecure = SystemProperties.getString("ro.adb.secure")
    val avbVersion = SystemProperties.getString("ro.boot.avb_version")
    val secure = SystemProperties.getString("ro.secure")
    val verifiedBootState = SystemProperties.getString("ro.boot.verifiedbootstate")

    // Treble/VNDK
    val trebleEnabled = SystemProperties.getBoolean("ro.treble.enabled")
    val vndkVersion = SystemProperties.getString("ro.vndk.version")
}
