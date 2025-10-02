/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
import android.nfc.NfcAdapter
import android.os.Build
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

class NfcComponent(context: Context) : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = NfcComponent(context)
    }

    val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    override val name = "nfc"

    override val title = LocalizedString(R.string.section_nfc_name)

    override val description = LocalizedString(R.string.section_nfc_description)

    override val drawableResId = R.drawable.ic_nfc

    override val permissions = setOf(Permission.NFC)

    override fun resolve(identifier: Resource.Identifier) = nfcAdapter?.let { nfcAdapter ->
        when (identifier.path.firstOrNull()) {
            null -> suspend {
                val screen = Screen.CardListScreen(
                    identifier = identifier,
                    title = title,
                    elements = listOfNotNull(
                        Element.Card(
                            identifier = identifier / "general",
                            title = LocalizedString(R.string.nfc_general),
                            elements = listOf(
                                Element.Item(
                                    identifier = identifier / "general" / "enabled",
                                    title = LocalizedString(R.string.nfc_enabled),
                                    value = Value.Companion(nfcAdapter.isEnabled),
                                ),
                            ),
                        ),
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Element.Card(
                                identifier = identifier / "secure_nfc",
                                title = LocalizedString(R.string.nfc_secure_nfc),
                                elements = listOfNotNull(
                                    Element.Item(
                                        identifier = identifier / "secure_nfc" / "supported",
                                        title = LocalizedString(R.string.nfc_secure_nfc_supported),
                                        value = Value.Companion(nfcAdapter.isSecureNfcSupported),
                                    ),
                                    if (nfcAdapter.isSecureNfcSupported) {
                                        Element.Item(
                                            identifier = identifier / "secure_nfc" / "enabled",
                                            title = LocalizedString(R.string.nfc_secure_nfc_enabled),
                                            value = Value.Companion(nfcAdapter.isSecureNfcEnabled),
                                        )
                                    } else {
                                        null
                                    },
                                ),
                            )
                        } else {
                            null
                        },
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            nfcAdapter.nfcAntennaInfo?.let { nfcAntennaInfo ->
                                Element.Card(
                                    identifier = identifier / "antenna_info",
                                    title = LocalizedString(R.string.nfc_antenna_info),
                                    elements = listOfNotNull(
                                        Element.Item(
                                            identifier = identifier / "antenna_info" / "device_dimensions",
                                            title = LocalizedString(R.string.nfc_antenna_info_device_dimensions),
                                            value = Value(
                                                "${nfcAdapter.nfcAntennaInfo?.deviceWidth}x${nfcAdapter.nfcAntennaInfo?.deviceHeight}",
                                                R.string.nfc_antenna_info_device_dimensions_format,
                                                arrayOf(
                                                    nfcAntennaInfo.deviceWidth,
                                                    nfcAntennaInfo.deviceHeight,
                                                ),
                                            ),
                                        ),
                                        Element.Item(
                                            identifier = identifier / "antenna_info" / "is_device_foldable",
                                            title = LocalizedString(R.string.nfc_antenna_info_is_device_foldable),
                                            value = Value(nfcAntennaInfo.isDeviceFoldable),
                                        ),
                                        *nfcAntennaInfo.availableNfcAntennas.withIndex()
                                            .map { (i, availableNfcAntenna) ->
                                                Element.Item(
                                                    identifier = identifier / "antenna_info" / "antenna_${i}",
                                                    title = LocalizedString(
                                                        R.string.nfc_antenna_info_antenna,
                                                        i,
                                                    ),
                                                    value = Value(
                                                        "${availableNfcAntenna.locationX}x${availableNfcAntenna.locationY}",
                                                        R.string.nfc_antenna_info_antenna_dimensions_format,
                                                        arrayOf(
                                                            availableNfcAntenna.locationX,
                                                            availableNfcAntenna.locationY,
                                                        ),
                                                    ),
                                                )
                                            }.toTypedArray(),
                                    ),
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
    } ?: flowOf(Result.Error(Error.NOT_IMPLEMENTED))
}
