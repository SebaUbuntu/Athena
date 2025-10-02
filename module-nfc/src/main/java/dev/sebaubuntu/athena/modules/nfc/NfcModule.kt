/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.nfc

import android.Manifest
import android.content.Context
import android.nfc.NfcAdapter
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

class NfcModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = NfcModule(context)
    }

    val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    override val id = "nfc"

    override val name = LocalizedString(R.string.section_nfc_name)

    override val description = LocalizedString(R.string.section_nfc_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_nfc

    override val requiredPermissions = arrayOf(
        Manifest.permission.NFC,
    )

    override fun resolve(identifier: Resource.Identifier) = nfcAdapter?.let { nfcAdapter ->
        when (identifier.path.firstOrNull()) {
            null -> suspend {
                val screen = Screen.CardListScreen(
                    identifier = identifier,
                    title = name,
                    elements = listOfNotNull(
                        Element.Card(
                            name = "general",
                            title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                            elements = listOf(
                                Element.Item(
                                    name = "enabled",
                                    title = LocalizedString(R.string.nfc_enabled),
                                    value = Value(nfcAdapter.isEnabled),
                                ),
                            ),
                        ),
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Element.Card(
                                name = "secure_nfc",
                                title = LocalizedString(R.string.nfc_secure_nfc),
                                elements = listOfNotNull(
                                    Element.Item(
                                        name = "supported",
                                        title = LocalizedString(R.string.nfc_secure_nfc_supported),
                                        value = Value(nfcAdapter.isSecureNfcSupported),
                                    ),
                                    if (nfcAdapter.isSecureNfcSupported) {
                                        Element.Item(
                                            name = "enabled",
                                            title = LocalizedString(R.string.nfc_secure_nfc_enabled),
                                            value = Value(nfcAdapter.isSecureNfcEnabled),
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
                                    name = "antenna_info",
                                    title = LocalizedString(R.string.nfc_antenna_info),
                                    elements = listOfNotNull(
                                        Element.Item(
                                            name = "device_dimensions",
                                            title = LocalizedString(R.string.nfc_antenna_info_device_dimensions),
                                            value = Value(
                                                "${nfcAdapter.nfcAntennaInfo?.deviceWidth}x${nfcAdapter.nfcAntennaInfo?.deviceHeight}",
                                                R.string.nfc_antenna_info_device_dimensions_format,
                                                nfcAntennaInfo.deviceWidth,
                                                nfcAntennaInfo.deviceHeight,
                                            ),
                                        ),
                                        Element.Item(
                                            name = "is_device_foldable",
                                            title = LocalizedString(R.string.nfc_antenna_info_is_device_foldable),
                                            value = Value(nfcAntennaInfo.isDeviceFoldable),
                                        ),
                                        *nfcAntennaInfo.availableNfcAntennas.withIndex()
                                            .map { (i, availableNfcAntenna) ->
                                                Element.Item(
                                                    name = "antenna_${i}",
                                                    title = LocalizedString(
                                                        R.string.nfc_antenna_info_antenna,
                                                        i,
                                                    ),
                                                    value = Value(
                                                        "${availableNfcAntenna.locationX}x${availableNfcAntenna.locationY}",
                                                        R.string.nfc_antenna_info_antenna_dimensions_format,
                                                        availableNfcAntenna.locationX,
                                                        availableNfcAntenna.locationY,
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
