/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.Manifest
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.flow.asFlow

object RilSection : Section(
    "ril",
    R.string.section_ril_name,
    R.string.section_ril_description,
    R.drawable.ic_call,
    arrayOf(
        Manifest.permission.READ_PHONE_STATE,
    ),
) {
    @Suppress("MissingPermission")
    override fun dataFlow(context: Context) = {
        context.getSystemService(TelephonyManager::class.java)?.let { telephonyManager ->
            listOf(
                Subsection(
                    "general",
                    listOfNotNull(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Information(
                                "active_modem_count",
                                InformationValue.IntValue(telephonyManager.activeModemCount),
                                R.string.ril_active_modem_count,
                            )
                        } else {
                            null
                        },
                        Information(
                            "device_software_version",
                            telephonyManager.deviceSoftwareVersion?.let {
                                InformationValue.StringValue(it)
                            },
                            R.string.ril_device_software_version,
                        ),
                        Information(
                            "is_world_phone",
                            InformationValue.BooleanValue(telephonyManager.isWorldPhone),
                            R.string.ril_is_world_phone,
                        ),
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Information(
                                "manufacturer_code",
                                telephonyManager.manufacturerCode?.let {
                                    InformationValue.StringValue(it)
                                },
                                R.string.ril_manufacturer_code,
                            )
                        } else {
                            null
                        },
                        Information(
                            "phone_type",
                            InformationValue.IntValue(
                                telephonyManager.phoneType,
                                phoneTypeToStringResId,
                            ),
                            R.string.ril_phone_type,
                        )
                    ),
                    R.string.ril_general,
                )
            )
        } ?: listOf(
            Subsection(
                "not_supported",
                listOf(),
                R.string.ril_not_supported,
            )
        )
    }.asFlow()

    private val phoneTypeToStringResId = mapOf(
        TelephonyManager.PHONE_TYPE_NONE to R.string.ril_phone_type_none,
        TelephonyManager.PHONE_TYPE_GSM to R.string.ril_phone_type_gsm,
        TelephonyManager.PHONE_TYPE_CDMA to R.string.ril_phone_type_cdma,
        TelephonyManager.PHONE_TYPE_SIP to R.string.ril_phone_type_sip,
    )
}
