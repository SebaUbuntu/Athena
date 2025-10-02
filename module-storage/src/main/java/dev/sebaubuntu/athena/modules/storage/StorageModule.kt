/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.storage

import android.content.Context
import android.os.Environment
import android.os.StatFs
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.modules.storage.models.EncryptionType
import dev.sebaubuntu.athena.modules.systemproperties.utils.SystemProperties
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class StorageModule : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = StorageModule()
    }

    override val id = "storage"

    override val name = LocalizedString(R.string.section_storage_name)

    override val description = LocalizedString(R.string.section_storage_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_storage

    override val requiredPermissions = arrayOf<String>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOfNotNull(
                    Element.Card(
                        name = "internal_storage",
                        title = LocalizedString(R.string.storage_internal_storage),
                        elements = StatFs(Environment.getDataDirectory().absolutePath).let { statFs ->
                            val total = statFs.blockCountLong * statFs.blockSizeLong
                            val free = statFs.availableBlocksLong * statFs.blockSizeLong

                            listOfNotNull(
                                Element.Item(
                                    name = "total_space",
                                    title = LocalizedString(R.string.storage_total_space),
                                    value = Value.BytesValue(total),
                                ),
                                Element.Item(
                                    name = "available_space",
                                    title = LocalizedString(R.string.storage_available_space),
                                    value = Value.BytesValue(free),
                                ),
                                Element.Item(
                                    name = "used_space",
                                    title = LocalizedString(R.string.storage_used_space),
                                    value = Value.BytesValue(total - free),
                                ),
                                when (SystemProperties.getString("ro.crypto.state", "unknown")) {
                                    "encrypted" -> true
                                    "unencrypted" -> false
                                    else -> null
                                }?.let {
                                    Element.Item(
                                        name = "is_encrypted",
                                        title = LocalizedString(R.string.storage_is_encrypted),
                                        value = Value(it),
                                    )
                                },
                                when (SystemProperties.getString("ro.crypto.type", "unknown")) {
                                    "none" -> EncryptionType.NONE
                                    "block" -> EncryptionType.FDE
                                    "file" -> EncryptionType.FBE
                                    else -> null
                                }?.let {
                                    Element.Item(
                                        name = "encryption_type",
                                        title = LocalizedString(R.string.storage_encryption_type),
                                        value = Value(it, encryptionTypeToStringResId),
                                    )
                                },
                            )
                        },
                    ),
                    runCatching {
                        Element.Card(
                            name = "external_storage",
                            title = LocalizedString(R.string.storage_external_storage),
                            elements = StatFs(Environment.getExternalStorageDirectory().absolutePath).let { statFs ->
                                val total = statFs.blockCountLong * statFs.blockSizeLong
                                val free = statFs.availableBlocksLong * statFs.blockSizeLong

                                listOf(
                                    Element.Item(
                                        name = "total_space",
                                        title = LocalizedString(R.string.storage_total_space),
                                        value = Value.BytesValue(total),
                                    ),
                                    Element.Item(
                                        name = "available_space",
                                        title = LocalizedString(R.string.storage_available_space),
                                        value = Value.BytesValue(free),
                                    ),
                                    Element.Item(
                                        name = "used_space",
                                        title = LocalizedString(R.string.storage_used_space),
                                        value = Value.BytesValue(total - free),
                                    ),
                                    Element.Item(
                                        name = "is_emulated",
                                        title = LocalizedString(R.string.storage_is_emulated),
                                        value = Value(Environment.isExternalStorageEmulated()),
                                    ),
                                    Element.Item(
                                        name = "is_removable",
                                        title = LocalizedString(R.string.storage_is_removable),
                                        value = Value(Environment.isExternalStorageRemovable()),
                                    ),
                                )
                            },
                        )
                    }.getOrNull(),
                    Element.Card(
                        name = "system_partitions",
                        title = LocalizedString(R.string.storage_system_partitions),
                        elements = listOfNotNull(
                            SystemProperties.getBoolean("ro.apex.updatable")?.let {
                                Element.Item(
                                    name = "has_updatable_apex",
                                    title = LocalizedString(R.string.storage_has_updatable_apex),
                                    value = Value(it),
                                )
                            },
                            SystemProperties.getBoolean("ro.build.system_root_image")?.let {
                                Element.Item(
                                    name = "uses_system_as_root",
                                    title = LocalizedString(R.string.storage_uses_system_as_root),
                                    value = Value(it),
                                )
                            },
                            SystemProperties.getBoolean("ro.build.ab_update")?.let {
                                Element.Item(
                                    name = "uses_ab",
                                    title = LocalizedString(R.string.storage_uses_ab),
                                    value = Value(it),
                                )
                            },
                            SystemProperties.getString(
                                "ro.product.ab_ota_partitions"
                            )?.split(",")?.let {
                                Element.Item(
                                    name = "ab_ota_partitions",
                                    title = LocalizedString(R.string.storage_ab_ota_partitions),
                                    value = Value(it.toTypedArray()),
                                )
                            },
                            SystemProperties.getBoolean("ro.boot.dynamic_partitions")?.let {
                                Element.Item(
                                    name = "uses_dynamic_partitions",
                                    title = LocalizedString(R.string.storage_uses_dynamic_partitions),
                                    value = Value(it),
                                )
                            },
                            SystemProperties.getBoolean("ro.boot.dynamic_partitions_retrofit")
                                ?.let {
                                    Element.Item(
                                        name = "uses_retrofitted_dynamic_partitions",
                                        title = LocalizedString(R.string.storage_uses_retrofitted_dynamic_partitions),
                                        value = Value(it),
                                    )
                                },
                            SystemProperties.getBoolean("ro.virtual_ab.enabled")?.let {
                                Element.Item(
                                    name = "uses_virtual_ab",
                                    title = LocalizedString(R.string.storage_uses_virtual_ab),
                                    value = Value(it),
                                )
                            },
                            SystemProperties.getBoolean("ro.virtual_ab.retrofit")?.let {
                                Element.Item(
                                    name = "uses_retrofitted_virtual_ab",
                                    title = LocalizedString(R.string.storage_uses_retrofitted_virtual_ab),
                                    value = Value(it),
                                )
                            },
                            SystemProperties.getBoolean("ro.virtual_ab.compression.enabled")?.let {
                                Element.Item(
                                    name = "uses_compressed_virtual_ab",
                                    title = LocalizedString(R.string.storage_uses_compressed_virtual_ab),
                                    value = Value(it),
                                )
                            },
                        ),
                    ),
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }

    companion object {
        private val encryptionTypeToStringResId = mapOf(
            EncryptionType.NONE to R.string.encryption_type_none,
            EncryptionType.FDE to R.string.encryption_type_fde,
            EncryptionType.FBE to R.string.encryption_type_fbe,
        )
    }
}
