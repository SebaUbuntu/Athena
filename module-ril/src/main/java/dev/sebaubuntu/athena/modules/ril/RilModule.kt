/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.ril

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
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

class RilModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = RilModule(context)
    }

    private val telephonyManager: TelephonyManager? = context.getSystemService(
        TelephonyManager::class.java
    )

    override val id = "ril"

    override val name = LocalizedString(R.string.section_ril_name)

    override val description = LocalizedString(R.string.section_ril_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_call

    override val requiredPermissions = buildList {
        add(Manifest.permission.READ_PHONE_STATE)
    }.toTypedArray()

    @SuppressLint("MissingPermission")
    override fun resolve(
        identifier: Resource.Identifier,
    ) = telephonyManager?.let { telephonyManager ->
        when (identifier.path.firstOrNull()) {
            null -> suspend {
                val screen = Screen.CardListScreen(
                    identifier = identifier,
                    title = name,
                    elements = listOf(
                        Element.Card(
                            name = "general",
                            title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                            elements = listOfNotNull(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    Element.Item(
                                        name = "active_modem_count",
                                        title = LocalizedString(R.string.ril_active_modem_count),
                                        value = Value(telephonyManager.activeModemCount),
                                    )
                                } else {
                                    null
                                },
                                telephonyManager.deviceSoftwareVersion?.let { deviceSoftwareVersion ->
                                    Element.Item(
                                        name = "device_software_version",
                                        title = LocalizedString(R.string.ril_device_software_version),
                                        value = Value(deviceSoftwareVersion),
                                    )
                                },
                                Element.Item(
                                    name = "is_world_phone",
                                    title = LocalizedString(R.string.ril_is_world_phone),
                                    value = Value(telephonyManager.isWorldPhone),
                                ),
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    @Suppress("DEPRECATION")
                                    telephonyManager.manufacturerCode?.let { manufacturerCode ->
                                        Element.Item(
                                            name = "manufacturer_code",
                                            title = LocalizedString(R.string.ril_manufacturer_code),
                                            value = Value(manufacturerCode),
                                        )
                                    }
                                } else {
                                    null
                                },
                                Element.Item(
                                    name = "phone_type",
                                    title = LocalizedString(R.string.ril_phone_type),
                                    value = Value(
                                        telephonyManager.phoneType,
                                        phoneTypeToStringResId,
                                    ),
                                )
                            ),
                        ),
                    ),
                )

                Result.Success<Resource, Error>(screen)
            }.asFlow()

            else -> flowOf(Result.Error(Error.NOT_FOUND))
        }
    } ?: flowOf(Result.Error(Error.NOT_IMPLEMENTED))

    companion object {
        @Suppress("DEPRECATION")
        private val phoneTypeToStringResId = mapOf(
            TelephonyManager.PHONE_TYPE_NONE to R.string.ril_phone_type_none,
            TelephonyManager.PHONE_TYPE_GSM to R.string.ril_phone_type_gsm,
            TelephonyManager.PHONE_TYPE_CDMA to R.string.ril_phone_type_cdma,
            TelephonyManager.PHONE_TYPE_SIP to R.string.ril_phone_type_sip,
        )
    }
}
