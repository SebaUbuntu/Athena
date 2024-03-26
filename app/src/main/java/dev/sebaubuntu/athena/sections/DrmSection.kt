/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.media.MediaDrm
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.toHexString
import java.util.UUID

object DrmSection : Section() {
    override val title = R.string.section_drm_name
    override val description = R.string.section_drm_description
    override val icon = R.drawable.ic_drm

    override fun getInfo(context: Context) = contentProtectionSchemes.map {
        it.key to getDrmInfo(it.value)
    }.toMap()

    /**
     * From [DASH-IF](https://dashif.org/identifiers/content_protection/)
     */
    private val contentProtectionSchemes = mapOf(
        "ABV DRM (MoDRM)" to "6dd8b3c3-45f4-4a68-bf3a-64168d01a4a6",
        "Adobe Primetime DRM version 4" to "f239e769-efa3-4850-9c16-a903c6932efb",
        "Alticast" to "616c7469-6361-7374-2d50-726f74656374",
        "Apple FairPlay" to "94ce86fb-07ff-4f43-adb8-93d2fa968ca2",
        "Apple FairPlay (unofficial)" to "29701fe4-3cc7-4a34-8c5b-ae90c7439a47", // Not on DASH-IF
        "Arris Titanium" to "279fe473-512c-48fe-ade8-d176fee6b40f",
        "ChinaDRM" to "3d5e6d35-9b9a-41e8-b843-dd3c6e72c42c",
        "ClearKey AES-128" to "3ea8778f-7742-4bf9-b18b-e834b2acbd47",
        "ClearKey SAMPLE-AES" to "be58615b-19c4-4684-88b3-c8c57e99e957",
        "ClearKey DASH-IF" to "e2719d58-a985-b3c9-781a-b030af78d30e",
        "CMLA (OMA DRM)" to "644fe7b5-260f-4fad-949a-0762ffb054B4",
        "Commscope Titanium V3" to "37c33258-7b99-4c7e-b15d-19af74482154",
        "CoreCrypt" to "37c33258-7b99-4c7e-b15d-19af74482154",
        "DigiCAP SmartXess" to "dcf4e3e3-62f1-5818-7ba6-0a6fe33ff3dd",
        "DivX DRM Series 5" to "35bf197b-530e-42d7-8b65-1b4bf415070f",
        "Irdeto Content Protection" to "80a6be7e-1448-4c37-9e70-d5aebe04c8d2",
        "Marlin Adaptive Streaming Simple Profile V1.0" to "5e629af5-38da-4063-8977-97ffbd9902d4",
        "Microsoft PlayReady" to "9a04f079-9840-4286-ab92-e65be0885f95",
        "MobiTV DRM" to "6a99532d-869f-5922-9a91-113ab7b1e2f3",
        "Nagra MediaAccess PRM 3.0" to "adb41c24-2dbf-4a6d-958b-4457c0d27b95",
        "SecureMedia" to "1f83e1e8-6ee9-4f0d-ba2f-5ec4e3ed1a66",
        "SecureMedia SteelKnot" to "992c46e6-c437-4899-b6a0-50fa91ad0e39",
        "Synamedia/Cisco/NDS VideoGuard DRM" to "a68129d3-575b-4f1a-9cba-3223846cf7c3",
        "Unitend DRM (UDRM)" to "aa11967f-cc01-4a4a-8e99-c5d3dddfea2d",
        "Verimatrix VCAS" to "9a27dd82-fde2-4725-8cbc-4234aa06ec09",
        "Viaccess-Orca DRM (VODRM)" to "b4413586-c58c-ffb0-94a5-d4896c1af6c3",
        "VisionCrypt" to "793b7956-9f94-4946-a942-23e7ef7e44b4",
        "W3C Common PSSH box" to "1077efec-c0b2-4d02-ace3-3c1e52e2fb4b",
        "Widevine Content Protection" to "edef8ba9-79d6-4ace-a3c8-27dcd51d21ed",
    ).mapValues {
        UUID.fromString(it.value)
    }

    private fun getDrmInfo(uuid: UUID) = mutableMapOf<String, String>().apply {
        this["UUID"] = uuid.toString()

        val mediaDrm = runCatching {
            MediaDrm(uuid)
        }.getOrNull()

        this["Supported"] = mediaDrm?.let { "Yes" } ?: "No"

        mediaDrm?.also {
            this["Vendor"] = it.getPropertyString(MediaDrm.PROPERTY_VENDOR)
            this["Version"] = it.getPropertyString(MediaDrm.PROPERTY_VERSION)
            this["Description"] = it.getPropertyString(MediaDrm.PROPERTY_DESCRIPTION)

            it.getPropertyString(MediaDrm.PROPERTY_ALGORITHMS)
                .takeIf { algorithms -> algorithms.isNotEmpty() }
                ?.split(",")
                ?.takeIf { algorithms -> algorithms.isNotEmpty() }?.let { algorithms ->
                    this["Algorithms"] = algorithms.joinToString()
                }

            runCatching {
                it.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
            }.getOrNull()?.let { uniqueId ->
                this["Unique ID"] = uniqueId.toHexString()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                runCatching {
                    it.openSessionCount
                }.getOrNull()?.let {
                    this["Open session count"] = it.toString()
                }
                runCatching {
                    it.maxSessionCount
                }.getOrNull()?.let {
                    this["Max session count"] = it.toString()
                }
                runCatching {
                    it.connectedHdcpLevel
                }.getOrNull()?.let {
                    this["Connected HDCP level"] = it.toString()
                }
                runCatching {
                    it.maxHdcpLevel
                }.getOrNull()?.let {
                    this["Max HDCP level"] = it.toString()
                }
            }

            runCatching {
                it.getPropertyString("securityLevel")
            }.getOrNull()?.let { securityLevel ->
                this["Security level"] = securityLevel
            }
            runCatching {
                it.getPropertyString("systemId")
            }.getOrNull()?.let { systemId ->
                this["System ID"] = systemId
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                it.close()
            } else {
                @Suppress("DEPRECATION")
                it.release()
            }
        }
    }.toMap()
}
