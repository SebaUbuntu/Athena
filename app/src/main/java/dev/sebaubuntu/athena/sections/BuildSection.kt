/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import dev.sebaubuntu.athena.utils.DeviceInfo
import dev.sebaubuntu.athena.utils.KernelUtils
import kotlinx.coroutines.flow.asFlow
import java.util.Date

object BuildSection : Section(
    "build",
    R.string.section_build_name,
    R.string.section_build_description,
    R.drawable.ic_build,
) {
    override fun dataFlow(context: Context) = {
        listOfNotNull(
            Subsection(
                "information",
                mutableListOf(
                    Information(
                        "build_fingerprint",
                        InformationValue.StringValue(Build.FINGERPRINT),
                        R.string.build_information_fingerprint,
                    ),
                    Information(
                        "build_tags",
                        InformationValue.StringValue(Build.TAGS),
                        R.string.build_tags,
                    ),
                    Information(
                        "build_type",
                        InformationValue.StringValue(Build.TYPE),
                        R.string.build_type,
                    ),
                    Information(
                        "build_date",
                        InformationValue.DateValue(Date(Build.TIME)),
                        R.string.build_date,
                    ),
                    Information(
                        "build_host",
                        InformationValue.StringValue(Build.HOST),
                        R.string.build_host,
                    ),
                    Information(
                        "build_user",
                        InformationValue.StringValue(Build.USER),
                        R.string.build_user,
                    ),
                    Information(
                        "build_id",
                        InformationValue.StringValue(Build.ID),
                        R.string.build_id,
                    ),
                    Information(
                        "build_display",
                        InformationValue.StringValue(Build.DISPLAY),
                        R.string.build_display,
                    ),
                    Information(
                        "build_version_release",
                        InformationValue.StringValue(Build.VERSION.RELEASE),
                        R.string.build_version_release,
                    ),
                    Information(
                        "build_version_codename",
                        InformationValue.StringValue(Build.VERSION.CODENAME),
                        R.string.build_version_codename,
                    ),
                    Information(
                        "build_version_sdk_int",
                        InformationValue.StringValue(Build.VERSION.SDK_INT.toString()),
                        R.string.build_version_sdk_int,
                    ),
                    Information(
                        "build_version_preview_sdk_int",
                        InformationValue.StringValue(Build.VERSION.PREVIEW_SDK_INT.toString()),
                        R.string.build_version_preview_sdk_int,
                    ),
                    Information(
                        "build_security_patch",
                        InformationValue.StringValue(Build.VERSION.SECURITY_PATCH),
                        R.string.build_security_patch,
                    ),
                    Information(
                        "build_base_os",
                        InformationValue.StringValue(Build.VERSION.BASE_OS),
                        R.string.build_base_os,
                    ),
                    Information(
                        "build_version_incremental",
                        InformationValue.StringValue(Build.VERSION.INCREMENTAL),
                        R.string.build_incremental,
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Information(
                            "build_release_or_codename",
                            InformationValue.StringValue(Build.VERSION.RELEASE_OR_CODENAME),
                            R.string.build_release_or_codename,
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Information(
                            "build_release_or_codename",
                            InformationValue.StringValue(
                                Build.VERSION.MEDIA_PERFORMANCE_CLASS.toString()
                            ),
                            R.string.build_release_or_codename,
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Information(
                            "build_release_or_preview_display",
                            InformationValue.StringValue(Build.VERSION.RELEASE_OR_PREVIEW_DISPLAY),
                            R.string.build_release_or_preview_display,
                        )
                    } else {
                        null
                    },
                ).filterNotNull(),
                R.string.build_information,
            ),
            Subsection(
                "jvm",
                listOf(
                    Information(
                        "name",
                        System.getProperty("java.vm.name")?.let {
                            InformationValue.StringValue(it)
                        },
                        R.string.jvm_name,
                    ),
                    Information(
                        "vendor",
                        System.getProperty("java.vm.vendor")?.let {
                            InformationValue.StringValue(it)
                        },
                        R.string.jvm_vendor,
                    ),
                    Information(
                        "version",
                        System.getProperty("java.vm.version")?.let {
                            InformationValue.StringValue(it)
                        },
                        R.string.jvm_version,
                    ),
                    Information(
                        "class_version",
                        System.getProperty("java.class.version")?.let {
                            InformationValue.StringValue(it)
                        },
                        R.string.jvm_class_version,
                    ),
                    Information(
                        "specification_name",
                        System.getProperty("java.specification.name")?.let {
                            InformationValue.StringValue(it)
                        },
                        R.string.jvm_specification_name,
                    ),
                    Information(
                        "specification_vendor",
                        System.getProperty("java.specification.vendor")?.let {
                            InformationValue.StringValue(it)
                        },
                        R.string.jvm_specification_vendor,
                    ),
                    Information(
                        "specification_version",
                        System.getProperty("java.specification.version")?.let {
                            InformationValue.StringValue(it)
                        },
                        R.string.jvm_specification_version,
                    ),
                ),
                R.string.jvm,
            ),
            Subsection(
                "kernel",
                listOf(
                    Information(
                        "version",
                        InformationValue.StringValue(DeviceInfo.kernelVersion),
                        R.string.kernel_version,
                    ),
                    Information(
                        "complete_version",
                        InformationValue.StringValue(KernelUtils.formattedKernelVersion),
                        R.string.kernel_complete_version,
                    ),
                ),
                R.string.kernel,
            ),
            Subsection(
                "firmware",
                listOf(
                    Information(
                        "bootloader_version",
                        InformationValue.StringValue(Build.BOOTLOADER),
                        R.string.firmware_bootloader_version,
                    ),
                    Information(
                        "radio_version",
                        InformationValue.StringValue(Build.getRadioVersion()),
                        R.string.firmware_radio_version,
                    ),
                ),
                R.string.firmware,
            ),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Build.getFingerprintedPartitions().takeIf {
                    it.isNotEmpty()
                }?.let { fingerprintedPartitions ->
                    Subsection(
                        "fingerprinted_partitions",
                        fingerprintedPartitions.map {
                            Information(
                                it.name,
                                InformationValue.StringValue(it.fingerprint)
                            )
                        },
                        R.string.fingerprinted_partitions,
                    )
                }
            } else {
                null
            }
        )
    }.asFlow()
}
