/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.drm

import android.content.Context
import android.media.MediaDrm
import android.os.Build
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
import java.util.UUID

class DrmModule : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = DrmModule()
    }

    override val id = "drm"

    override val name = LocalizedString(R.string.section_drm_name)

    override val description = LocalizedString(R.string.section_drm_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_live_tv

    override val requiredPermissions = arrayOf<String>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.ItemListScreen(
                identifier = identifier,
                title = name,
                elements = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    MediaDrm.getSupportedCryptoSchemes()
                } else {
                    contentProtectionSchemes.keys
                }.mapNotNull { uuid ->
                    val mediaDrm = runCatching {
                        MediaDrm(uuid)
                    }.getOrNull()

                    val name = contentProtectionSchemes[
                        uuid
                    ] ?: "Unknown Content Protection Scheme $uuid"

                    val item = mediaDrm?.let { mediaDrm ->
                        Element.Item(
                            name = "$uuid",
                            title = LocalizedString(name),
                            navigateTo = identifier / "$uuid",
                            drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_live_tv,
                            value = Value("$uuid"),
                        )
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        mediaDrm?.close()
                    } else {
                        @Suppress("DEPRECATION")
                        mediaDrm?.release()
                    }

                    item
                },
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> when (identifier.path.getOrNull(1)) {
            null -> suspend {
                val uuid = runCatching {
                    UUID.fromString(identifier.path.first())
                }.getOrNull()

                val mediaDrm = uuid?.let { uuid ->
                    runCatching {
                        MediaDrm(uuid)
                    }.getOrNull()
                }

                val name = contentProtectionSchemes[
                    uuid
                ] ?: "Unknown Content Protection Scheme $uuid"

                val screen = mediaDrm?.let { mediaDrm ->
                    Screen.CardListScreen(
                        identifier = identifier,
                        title = LocalizedString(name),
                        elements = listOf(
                            Element.Card(
                                name = "general",
                                title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                                elements = listOfNotNull(
                                    Element.Item(
                                        name = "uuid",
                                        title = LocalizedString(R.string.drm_uuid),
                                        value = Value(uuid.toString()),
                                    ),
                                    Element.Item(
                                        name = "vendor",
                                        title = LocalizedString(R.string.drm_vendor),
                                        value = Value(mediaDrm.getPropertyString(MediaDrm.PROPERTY_VENDOR)),
                                    ),
                                    Element.Item(
                                        name = "version",
                                        title = LocalizedString(R.string.drm_version),
                                        value = Value(mediaDrm.getPropertyString(MediaDrm.PROPERTY_VERSION)),
                                    ),
                                    Element.Item(
                                        name = "description",
                                        title = LocalizedString(R.string.drm_description),
                                        value = Value(mediaDrm.getPropertyString(MediaDrm.PROPERTY_DESCRIPTION)),
                                    ),
                                    Element.Item(
                                        name = "algorithms",
                                        title = LocalizedString(R.string.drm_algorithms),
                                        value = Value(
                                            mediaDrm.getPropertyString(MediaDrm.PROPERTY_ALGORITHMS)
                                                .takeIf { algorithms -> algorithms.isNotEmpty() }
                                                ?.split(",")
                                                ?.toTypedArray() ?: emptyArray()
                                        ),
                                    ),
                                    runCatching {
                                        mediaDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
                                    }.getOrNull()?.let { deviceUniqueId ->
                                        Element.Item(
                                            name = "device_unique_id",
                                            title = LocalizedString(R.string.drm_device_unique_id),
                                            value = Value(deviceUniqueId.toHexString()),
                                        )
                                    },
                                    *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                        listOfNotNull(
                                            runCatching {
                                                mediaDrm.openSessionCount
                                            }.getOrNull()?.let { openSessionCount ->
                                                Element.Item(
                                                    name = "open_session_count",
                                                    title = LocalizedString(R.string.drm_open_session_count),
                                                    value = Value(openSessionCount),
                                                )
                                            },
                                            runCatching {
                                                mediaDrm.maxSessionCount
                                            }.getOrNull()?.let { maxSessionCount ->
                                                Element.Item(
                                                    name = "max_session_count",
                                                    title = LocalizedString(R.string.drm_max_session_count),
                                                    value = Value(maxSessionCount),
                                                )
                                            },
                                            runCatching {
                                                mediaDrm.connectedHdcpLevel
                                            }.getOrNull()?.let { connectedHdcpLevel ->
                                                Element.Item(
                                                    name = "connected_hdcp_level",
                                                    title = LocalizedString(R.string.drm_connected_hdcp_level),
                                                    value = Value(connectedHdcpLevel),
                                                )
                                            },
                                            runCatching {
                                                mediaDrm.maxHdcpLevel
                                            }.getOrNull()?.let { maxHdcpLevel ->
                                                Element.Item(
                                                    name = "max_hdcp_level",
                                                    title = LocalizedString(R.string.drm_max_hdcp_level),
                                                    value = Value(maxHdcpLevel),
                                                )
                                            },
                                        ).toTypedArray()
                                    } else {
                                        arrayOf()
                                    },
                                    runCatching {
                                        mediaDrm.getPropertyString("securityLevel")
                                    }.getOrNull()?.let { securityLevel ->
                                        Element.Item(
                                            name = "security_level",
                                            title = LocalizedString(R.string.drm_security_level),
                                            value = Value(securityLevel),
                                        )
                                    },
                                    runCatching {
                                        mediaDrm.getPropertyString("systemId")
                                    }.getOrNull()?.let { systemId ->
                                        Element.Item(
                                            name = "system_id",
                                            title = LocalizedString(R.string.drm_system_id),
                                            value = Value(systemId),
                                        )
                                    },
                                ),
                            ),
                        ),
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    mediaDrm?.close()
                } else {
                    @Suppress("DEPRECATION")
                    mediaDrm?.release()
                }

                screen?.let {
                    Result.Success<Resource, Error>(it)
                } ?: Result.Error(Error.NOT_FOUND)
            }.asFlow()

            else -> flowOf(Result.Error(Error.NOT_FOUND))
        }
    }

    companion object {
        /**
         * From [DASH-IF](https://dashif.org/identifiers/content_protection/)
         */
        private val contentProtectionSchemes = mapOf(
            "6dd8b3c3-45f4-4a68-bf3a-64168d01a4a6" to "ABV DRM (MoDRM)",
            "f239e769-efa3-4850-9c16-a903c6932efb" to "Adobe Primetime DRM version 4",
            "616c7469-6361-7374-2d50-726f74656374" to "Alticast",
            "94ce86fb-07ff-4f43-adb8-93d2fa968ca2" to "Apple FairPlay",
            "29701fe4-3cc7-4a34-8c5b-ae90c7439a47" to "Apple FairPlay (unofficial)", // Not on DASH-IF
            "279fe473-512c-48fe-ade8-d176fee6b40f" to "Arris Titanium",
            "3d5e6d35-9b9a-41e8-b843-dd3c6e72c42c" to "ChinaDRM",
            "3ea8778f-7742-4bf9-b18b-e834b2acbd47" to "ClearKey AES-128",
            "be58615b-19c4-4684-88b3-c8c57e99e957" to "ClearKey SAMPLE-AES",
            "e2719d58-a985-b3c9-781a-b030af78d30e" to "ClearKey DASH-IF",
            "644fe7b5-260f-4fad-949a-0762ffb054B4" to "CMLA (OMA DRM)",
            "37c33258-7b99-4c7e-b15d-19af74482154" to "Commscope Titanium V3",
            "45d481cb-8fe0-49c0-ada9-ab2d2455b2f2" to "CoreCrypt",
            "dcf4e3e3-62f1-5818-7ba6-0a6fe33ff3dd" to "DigiCAP SmartXess",
            "35bf197b-530e-42d7-8b65-1b4bf415070f" to "DivX DRM Series 5",
            "80a6be7e-1448-4c37-9e70-d5aebe04c8d2" to "Irdeto Content Protection",
            "5e629af5-38da-4063-8977-97ffbd9902d4" to "Marlin Adaptive Streaming Simple Profile V1.0",
            "9a04f079-9840-4286-ab92-e65be0885f95" to "Microsoft PlayReady",
            "6a99532d-869f-5922-9a91-113ab7b1e2f3" to "MobiTV DRM",
            "adb41c24-2dbf-4a6d-958b-4457c0d27b95" to "Nagra MediaAccess PRM 3.0",
            "1f83e1e8-6ee9-4f0d-ba2f-5ec4e3ed1a66" to "SecureMedia",
            "992c46e6-c437-4899-b6a0-50fa91ad0e39" to "SecureMedia SteelKnot",
            "a68129d3-575b-4f1a-9cba-3223846cf7c3" to "Synamedia/Cisco/NDS VideoGuard DRM",
            "aa11967f-cc01-4a4a-8e99-c5d3dddfea2d" to "Unitend DRM (UDRM)",
            "9a27dd82-fde2-4725-8cbc-4234aa06ec09" to "Verimatrix VCAS",
            "b4413586-c58c-ffb0-94a5-d4896c1af6c3" to "Viaccess-Orca DRM (VODRM)",
            "793b7956-9f94-4946-a942-23e7ef7e44b4" to "VisionCrypt",
            "1077efec-c0b2-4d02-ace3-3c1e52e2fb4b" to "W3C Common PSSH box",
            "edef8ba9-79d6-4ace-a3c8-27dcd51d21ed" to "Widevine Content Protection",
        ).mapKeys {
            UUID.fromString(it.key)
        }
    }
}
