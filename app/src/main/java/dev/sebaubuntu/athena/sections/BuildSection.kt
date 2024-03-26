/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.DeviceInfo
import dev.sebaubuntu.athena.utils.KernelUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BuildSection : Section() {
    override val title = R.string.section_build_name
    override val description = R.string.section_build_description
    override val icon = R.drawable.ic_build

    override fun getInfo(context: Context) = mutableMapOf(
        "Build info" to mapOf(
            "Fingerprint" to Build.FINGERPRINT,
            "Build tags" to Build.TAGS,
            "Build type" to Build.TYPE,
            "Build date" to dateFormatter.format(Date(Build.TIME)),
            "Build host" to Build.HOST,
            "Build user" to Build.USER,
            "Build ID" to Build.ID,
            "Build display ID" to Build.DISPLAY,
        ),
        "Android" to mutableMapOf(
            "Version" to Build.VERSION.RELEASE,
            "Version codename" to Build.VERSION.CODENAME,
            "SDK version" to "${Build.VERSION.SDK_INT}",
            "Security patch level" to Build.VERSION.SECURITY_PATCH,
            "Base OS" to Build.VERSION.BASE_OS,
            "Incremental" to Build.VERSION.INCREMENTAL,
        ).apply {
            Build.VERSION.PREVIEW_SDK_INT.takeIf { it != 0 }?.let {
                this["Preview SDK int"] = it.toString()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this["Release or codename"] = Build.VERSION.RELEASE_OR_CODENAME
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this["Media performance class"] = Build.VERSION.MEDIA_PERFORMANCE_CLASS.toString()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this["Release or preview display"] = Build.VERSION.RELEASE_OR_PREVIEW_DISPLAY
            }
        },
        "JVM" to mapOf(
            "Name" to System.getProperty("java.vm.name"),
            "Vendor" to System.getProperty("java.vm.vendor"),
            "Version" to System.getProperty("java.vm.version"),
            "Class version" to System.getProperty("java.class.version"),
            "Specification name" to System.getProperty("java.specification.name"),
            "Specification vendor" to System.getProperty("java.specification.vendor"),
            "Specification version" to System.getProperty("java.specification.version"),
        ),
        "Kernel" to mapOf(
            "Version" to DeviceInfo.kernelVersion,
            "Complete version" to KernelUtils.formattedKernelVersion,
        ),
        "Firmware" to mapOf(
            "Bootloader version" to Build.BOOTLOADER,
            "Radio version" to Build.getRadioVersion(),
        ),
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val fingerprintedPartitions = Build.getFingerprintedPartitions()
            if (fingerprintedPartitions.isNotEmpty()) {
                this["Fingerprinted partitions"] = fingerprintedPartitions.associate {
                    it.name to it.fingerprint
                }
            }
        }
    }.toMap()

    private val dateFormatter = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.US)
}
