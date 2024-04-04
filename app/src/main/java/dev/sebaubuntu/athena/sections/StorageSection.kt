/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.os.Environment
import android.os.StatFs
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import dev.sebaubuntu.athena.utils.DeviceInfo
import kotlinx.coroutines.flow.asFlow

object StorageSection : Section(
    "storage",
    R.string.section_storage_name,
    R.string.section_storage_description,
    R.drawable.ic_storage,
) {
    override fun dataFlow(context: Context) = {
        listOfNotNull(
            Subsection(
                "internal_storage",
                StatFs(Environment.getDataDirectory().absolutePath).let { statFs ->
                    val total = statFs.blockCountLong * statFs.blockSizeLong
                    val free = statFs.availableBlocksLong * statFs.blockSizeLong

                    listOf(
                        Information(
                            "total_space",
                            InformationValue.BytesValue(total),
                            R.string.storage_total_space,
                        ),
                        Information(
                            "available_space",
                            InformationValue.BytesValue(free),
                            R.string.storage_available_space,
                        ),
                        Information(
                            "used_space",
                            InformationValue.BytesValue(total - free),
                            R.string.storage_used_space,
                        ),
                        Information(
                            "is_encrypted",
                            DeviceInfo.isDataEncrypted?.let {
                                InformationValue.BooleanValue(it)
                            },
                            R.string.storage_is_encrypted,
                        ),
                        Information(
                            "encryption_type",
                            DeviceInfo.dataEncryptionType?.let {
                                InformationValue.EnumValue(it, encryptionTypeToStringResId)
                            },
                            R.string.storage_encryption_type,
                        ),
                    )
                },
                R.string.storage_internal_storage,
            ),
            runCatching {
                Subsection(
                    "external_storage",
                    StatFs(Environment.getExternalStorageDirectory().absolutePath).let { statFs ->
                        val total = statFs.blockCountLong * statFs.blockSizeLong
                        val free = statFs.availableBlocksLong * statFs.blockSizeLong

                        listOf(
                            Information(
                                "total_space",
                                InformationValue.BytesValue(total),
                                R.string.storage_total_space,
                            ),
                            Information(
                                "available_space",
                                InformationValue.BytesValue(free),
                                R.string.storage_available_space,
                            ),
                            Information(
                                "used_space",
                                InformationValue.BytesValue(total - free),
                                R.string.storage_used_space,
                            ),
                            Information(
                                "is_emulated",
                                InformationValue.BooleanValue(
                                    Environment.isExternalStorageEmulated()
                                ),
                                R.string.storage_is_emulated,
                            ),
                            Information(
                                "is_removable",
                                InformationValue.BooleanValue(
                                    Environment.isExternalStorageRemovable()
                                ),
                                R.string.storage_is_removable,
                            ),
                        )
                    },
                    R.string.storage_external_storage,
                )
            }.getOrNull(),
            Subsection(
                "system_partitions",
                listOf(
                    Information(
                        "has_updatable_apex",
                        DeviceInfo.hasUpdatableApex?.let {
                            InformationValue.BooleanValue(it)
                        },
                        R.string.storage_has_updatable_apex,
                    ),
                    Information(
                        "uses_system_as_root",
                        DeviceInfo.usesSystemAsRoot?.let {
                            InformationValue.BooleanValue(it)
                        },
                        R.string.storage_uses_system_as_root,
                    ),
                    *DeviceInfo.usesAb.let { usesAb ->
                        listOfNotNull(
                            Information(
                                "uses_ab",
                                usesAb?.let {
                                    InformationValue.BooleanValue(it)
                                },
                                R.string.storage_uses_ab,
                            ),
                            usesAb?.let {
                                Information(
                                    "ab_ota_partitions",
                                    InformationValue.StringArrayValue(
                                        DeviceInfo.abOtaPartitions.toTypedArray()
                                    ),
                                    R.string.storage_ab_ota_partitions,
                                )
                            },
                        ).toTypedArray()
                    },
                    Information(
                        "uses_dynamic_partitions",
                        DeviceInfo.usesDynamicPartitions?.let {
                            InformationValue.BooleanValue(it)
                        },
                        R.string.storage_uses_dynamic_partitions,
                    ),
                    Information(
                        "uses_retrofitted_dynamic_partitions",
                        DeviceInfo.usesRetrofittedDynamicPartitions?.let {
                            InformationValue.BooleanValue(it)
                        },
                        R.string.storage_uses_retrofitted_dynamic_partitions,
                    ),
                    Information(
                        "uses_virtual_ab",
                        DeviceInfo.usesVab?.let {
                            InformationValue.BooleanValue(it)
                        },
                        R.string.storage_uses_virtual_ab,
                    ),
                    Information(
                        "uses_retrofitted_virtual_ab",
                        DeviceInfo.usesRetrofittedVab?.let {
                            InformationValue.BooleanValue(it)
                        },
                        R.string.storage_uses_retrofitted_virtual_ab,
                    ),
                    Information(
                        "uses_compressed_virtual_ab",
                        DeviceInfo.usesVabc?.let {
                            InformationValue.BooleanValue(it)
                        },
                        R.string.storage_uses_compressed_virtual_ab,
                    ),
                ),
                R.string.storage_system_partitions,
            ),
        )
    }.asFlow()

    private val encryptionTypeToStringResId = mapOf(
        DeviceInfo.EncryptionType.NONE to R.string.encryption_type_none,
        DeviceInfo.EncryptionType.FDE to R.string.encryption_type_fde,
        DeviceInfo.EncryptionType.FBE to R.string.encryption_type_fbe,
    )
}
