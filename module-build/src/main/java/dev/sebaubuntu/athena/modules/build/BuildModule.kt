/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.build

import android.content.Context
import android.os.Build
import androidx.security.state.SecurityStateManagerCompat
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import java.util.Date

class BuildModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = BuildModule(context)
    }

    private val securityStateManager = SecurityStateManagerCompat(context)

    private val globalSecurityState = securityStateManager.getGlobalSecurityState()

    override val id = "build"

    override val name = LocalizedString(R.string.section_build_name)

    override val description = LocalizedString(R.string.section_build_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_build

    override val requiredPermissions = arrayOf<String>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOfNotNull(
                    Element.Card(
                        name = "information",
                        title = LocalizedString(R.string.build_information),
                        elements = listOfNotNull(
                            Element.Item(
                                name = "build_fingerprint",
                                title = LocalizedString(R.string.build_information_fingerprint),
                                value = Value(Build.FINGERPRINT),
                            ),
                            Element.Item(
                                name = "build_tags",
                                title = LocalizedString(R.string.build_tags),
                                value = Value(Build.TAGS),
                            ),
                            Element.Item(
                                name = "build_type",
                                title = LocalizedString(R.string.build_type),
                                value = Value(Build.TYPE),
                            ),
                            Element.Item(
                                name = "build_date",
                                title = LocalizedString(R.string.build_date),
                                value = Value(Date(Build.TIME)),
                            ),
                            Element.Item(
                                name = "build_host",
                                title = LocalizedString(R.string.build_host),
                                value = Value(Build.HOST),
                            ),
                            Element.Item(
                                name = "build_user",
                                title = LocalizedString(R.string.build_user),
                                value = Value(Build.USER),
                            ),
                            Element.Item(
                                name = "build_id",
                                title = LocalizedString(R.string.build_id),
                                value = Value(Build.ID),
                            ),
                            Element.Item(
                                name = "build_display",
                                title = LocalizedString(R.string.build_display),
                                value = Value(Build.DISPLAY),
                            ),
                            Element.Item(
                                name = "build_version_release",
                                title = LocalizedString(R.string.build_version_release),
                                value = Value(Build.VERSION.RELEASE),
                            ),
                            Element.Item(
                                name = "build_version_codename",
                                title = LocalizedString(R.string.build_version_codename),
                                value = Value(Build.VERSION.CODENAME),
                            ),
                            Element.Item(
                                name = "build_version_sdk_int",
                                title = LocalizedString(R.string.build_version_sdk_int),
                                value = Value(Build.VERSION.SDK_INT),
                            ),
                            Element.Item(
                                name = "build_version_preview_sdk_int",
                                title = LocalizedString(R.string.build_version_preview_sdk_int),
                                value = Value(Build.VERSION.PREVIEW_SDK_INT),
                            ),
                            Element.Item(
                                name = "build_security_patch",
                                title = LocalizedString(R.string.build_security_patch),
                                value = Value(Build.VERSION.SECURITY_PATCH),
                            ),
                            Element.Item(
                                name = "build_base_os",
                                title = LocalizedString(R.string.build_base_os),
                                value = Value(Build.VERSION.BASE_OS),
                            ),
                            Element.Item(
                                name = "build_version_incremental",
                                title = LocalizedString(R.string.build_incremental),
                                value = Value(Build.VERSION.INCREMENTAL),
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                Element.Item(
                                    name = "build_release_or_codename",
                                    title = LocalizedString(R.string.build_release_or_codename),
                                    value = Value(Build.VERSION.RELEASE_OR_CODENAME),
                                )
                            } else {
                                null
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                Element.Item(
                                    name = "media_performance_class",
                                    title = LocalizedString(R.string.build_media_performance_class),
                                    value = Value(Build.VERSION.MEDIA_PERFORMANCE_CLASS),
                                )
                            } else {
                                null
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Element.Item(
                                    name = "build_release_or_preview_display",
                                    title = LocalizedString(R.string.build_release_or_preview_display),
                                    value = Value(Build.VERSION.RELEASE_OR_PREVIEW_DISPLAY),
                                )
                            } else {
                                null
                            },
                        ),
                    ),
                    Element.Card(
                        name = "jvm",
                        title = LocalizedString(R.string.jvm),
                        elements = listOf(
                            Element.Item(
                                name = "name",
                                title = LocalizedString(R.string.jvm_name),
                                value = System.getProperty("java.vm.name")?.let {
                                    Value(it)
                                },
                            ),
                            Element.Item(
                                name = "vendor",
                                title = LocalizedString(R.string.jvm_vendor),
                                value = System.getProperty("java.vm.vendor")?.let {
                                    Value(it)
                                },
                            ),
                            Element.Item(
                                name = "version",
                                title = LocalizedString(R.string.jvm_version),
                                value = System.getProperty("java.vm.version")?.let {
                                    Value(it)
                                },
                            ),
                            Element.Item(
                                name = "class_version",
                                title = LocalizedString(R.string.jvm_class_version),
                                value = System.getProperty("java.class.version")?.let {
                                    Value(it)
                                },
                            ),
                            Element.Item(
                                name = "specification_name",
                                title = LocalizedString(R.string.jvm_specification_name),
                                value = System.getProperty("java.specification.name")?.let {
                                    Value(it)
                                },
                            ),
                            Element.Item(
                                name = "specification_vendor",
                                title = LocalizedString(R.string.jvm_specification_vendor),
                                value = System.getProperty("java.specification.vendor")?.let {
                                    Value(it)
                                },
                            ),
                            Element.Item(
                                name = "specification_version",
                                title = LocalizedString(R.string.jvm_specification_version),
                                value = System.getProperty("java.specification.version")?.let {
                                    Value(it)
                                },
                            ),
                        ),
                    ),
                    Element.Card(
                        name = "vendor",
                        title = LocalizedString(R.string.build_vendor),
                        elements = listOf(
                            Element.Item(
                                name = "security_patch_level",
                                title = LocalizedString(R.string.build_vendor_security_patch_level),
                                value = globalSecurityState.getString(
                                    SecurityStateManagerCompat.Companion.KEY_VENDOR_SPL
                                )?.let {
                                    Value(it)
                                },
                            ),
                        ),
                    ),
                    Element.Card(
                        name = "kernel",
                        title = LocalizedString(R.string.kernel),
                        elements = listOfNotNull(
                            Element.Item(
                                name = "version",
                                title = LocalizedString(R.string.kernel_version),
                                value = globalSecurityState.getString(
                                    SecurityStateManagerCompat.Companion.KEY_KERNEL_VERSION
                                )?.let {
                                    Value(it)
                                },
                            ),
                            Element.Item(
                                name = "complete_version",
                                title = LocalizedString(R.string.kernel_complete_version),
                                value = System.getProperty("os.version")?.let {
                                    Value(it)
                                },
                            ),
                        ),
                    ),
                    Element.Card(
                        name = "firmware",
                        title = LocalizedString(R.string.firmware),
                        elements = listOfNotNull(
                            Element.Item(
                                name = "bootloader_version",
                                title = LocalizedString(R.string.firmware_bootloader_version),
                                value = Value(Build.BOOTLOADER),
                            ),
                            Build.getRadioVersion()?.let {
                                Element.Item(
                                    name = "radio_version",
                                    title = LocalizedString(R.string.firmware_radio_version),
                                    value = Value(it),
                                )
                            },
                        ),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Build.getFingerprintedPartitions().takeIf {
                            it.isNotEmpty()
                        }?.let { fingerprintedPartitions ->
                            Element.Card(
                                name = "fingerprinted_partitions",
                                title = LocalizedString(R.string.fingerprinted_partitions),
                                elements = fingerprintedPartitions.map {
                                    Element.Item(
                                        name = it.name,
                                        title = LocalizedString(it.name),
                                        value = Value(it.fingerprint),
                                    )
                                },
                            )
                        }
                    } else {
                        null
                    },
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }
}
