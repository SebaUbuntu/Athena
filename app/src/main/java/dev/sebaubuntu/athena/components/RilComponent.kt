/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.components.Component
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Permission
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class RilComponent(context: Context) : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = RilComponent(context)
    }

    private val telephonyManager: TelephonyManager? = context.getSystemService(
        TelephonyManager::class.java
    )

    override val name = "ril"

    override val title = LocalizedString(R.string.section_ril_name)

    override val description = LocalizedString(R.string.section_ril_description)

    override val drawableResId = R.drawable.ic_call

    override val permissions = setOf(Permission.RIL)

    override fun resolve(
        identifier: Resource.Identifier,
    ) = telephonyManager?.let { telephonyManager ->
        when (identifier.path.firstOrNull()) {
            null -> suspend {
                val screen = Screen.CardListScreen(
                    identifier = identifier,
                    title = title,
                    elements = listOf(
                        Element.Card(
                            identifier = identifier / "general",
                            title = LocalizedString(R.string.ril_general),
                            elements = listOfNotNull(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    Element.Item(
                                        identifier = identifier / "general" / "active_modem_count",
                                        title = LocalizedString(R.string.ril_active_modem_count),
                                        value = Value(telephonyManager.activeModemCount),
                                    )
                                } else {
                                    null
                                },
                                telephonyManager.deviceSoftwareVersion?.let { deviceSoftwareVersion ->
                                    Element.Item(
                                        identifier = identifier / "general" / "device_software_version",
                                        title = LocalizedString(R.string.ril_device_software_version),
                                        value = Value(deviceSoftwareVersion),
                                    )
                                },
                                Element.Item(
                                    identifier = identifier / "general" / "is_world_phone",
                                    title = LocalizedString(R.string.ril_is_world_phone),
                                    value = Value(telephonyManager.isWorldPhone),
                                ),
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    @Suppress("DEPRECATION")
                                    telephonyManager.manufacturerCode?.let { manufacturerCode ->
                                        Element.Item(
                                            identifier = identifier / "general" / "manufacturer_code",
                                            title = LocalizedString(R.string.ril_manufacturer_code),
                                            value = Value(manufacturerCode),
                                        )
                                    }
                                } else {
                                    null
                                },
                                Element.Item(
                                    identifier = identifier / "general" / "phone_type",
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

    @Suppress("DEPRECATION")
    private val phoneTypeToStringResId = mapOf(
        TelephonyManager.PHONE_TYPE_NONE to R.string.ril_phone_type_none,
        TelephonyManager.PHONE_TYPE_GSM to R.string.ril_phone_type_gsm,
        TelephonyManager.PHONE_TYPE_CDMA to R.string.ril_phone_type_cdma,
        TelephonyManager.PHONE_TYPE_SIP to R.string.ril_phone_type_sip,
    )
}
