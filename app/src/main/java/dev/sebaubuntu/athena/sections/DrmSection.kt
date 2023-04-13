/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.media.MediaDrm
import android.os.Build
import android.util.Base64
import dev.sebaubuntu.athena.R
import java.util.UUID

object DrmSection : Section() {
    override val name = R.string.section_drm_name
    override val description = R.string.section_drm_description
    override val icon = R.drawable.ic_drm
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context): Map<String, Map<String, String>> {
        val clearkeyDrm = runCatching { MediaDrm(CLEARKEY_UUID) }.getOrNull()
        val widevineDrm = runCatching { MediaDrm(WIDEVINE_UUID) }.getOrNull()
        val playreadyDrm = runCatching { MediaDrm(PLAYREADY_UUID) }.getOrNull()

        return mapOf(
            "ClearKey" to getDrmInfo(clearkeyDrm),
            "Widevine" to getDrmInfo(widevineDrm),
            "PlayReady" to getDrmInfo(playreadyDrm),
        )
    }

    private fun getDrmInfo(mediaDrm: MediaDrm?) = mutableMapOf<String, String>().apply {
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
                Base64.encodeToString(
                    it.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID),
                    Base64.NO_WRAP
                )
            }.getOrNull()?.let { uniqueId ->
                this["Unique ID"] = uniqueId
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                this["Open session count"] = it.openSessionCount.toString()
                this["Max session count"] = it.maxSessionCount.toString()
                this["Connected HDCP level"] = it.connectedHdcpLevel.toString()
                this["Max HDCP level"] = it.maxHdcpLevel.toString()
            }

            runCatching {
                it.getPropertyString("securityLevel")
            }.getOrNull()?.let { securityLevel ->
                this["Security level"] = securityLevel
            }
            runCatching {
                it.getPropertyString("systemId")
            }.getOrNull()?.let { securityLevel ->
                this["System ID"] = securityLevel
            }
        } ?: run {
            this["Not supported"] = ""
        }
    }.toMap()

    private val CLEARKEY_UUID = UUID(-0x1d8e62a7567a4c37L, 0x781AB030AF78D30EL)
    private val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
    private val PLAYREADY_UUID = UUID(-0x65fb0f8667bfbd7aL, -0x546d19a41f77a06bL)
}
