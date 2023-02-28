/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category

object RilCategory : Category {
    override val name = R.string.section_ril_name
    override val description = R.string.section_ril_description
    override val icon = R.drawable.ic_ril
    override val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
    )

    @SuppressLint("MissingPermission")
    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
        val telephonyManager = context.getSystemService(TelephonyManager::class.java)

        this["General"] = mutableMapOf<String, String>().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this["Active modem count"] = "${telephonyManager.activeModemCount}"
            }
            this["Device software version"] = "${telephonyManager.deviceSoftwareVersion}"
            this["Is world phone"] = "${telephonyManager.isWorldPhone}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this["Manufacturer code"] = "${telephonyManager.manufacturerCode}"
            }
            this["Phone type"] = when (telephonyManager.phoneType) {
                TelephonyManager.PHONE_TYPE_CDMA -> "CDMA"
                TelephonyManager.PHONE_TYPE_GSM -> "GSM"
                TelephonyManager.PHONE_TYPE_NONE -> "None"
                TelephonyManager.PHONE_TYPE_SIP -> "SIP"
                else -> "Unknown"
            }
        }
    }
}
