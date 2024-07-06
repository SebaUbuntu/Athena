/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.Manifest
import android.content.Context
import android.nfc.NfcAdapter
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.flow.asFlow

object NfcSection : Section(
    "nfc",
    R.string.section_nfc_name,
    R.string.section_nfc_description,
    R.drawable.ic_nfc,
    arrayOf(
        Manifest.permission.NFC,
    ),
) {
    override fun dataFlow(context: Context) = {
        val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

        listOfNotNull(
            Subsection(
                "general",
                listOfNotNull(
                    Information(
                        "supported",
                        InformationValue.BooleanValue(nfcAdapter != null),
                        R.string.nfc_supported,
                    ),
                    nfcAdapter?.let {
                        Information(
                            "enabled",
                            InformationValue.BooleanValue(it.isEnabled),
                            R.string.nfc_enabled,
                        )
                    }
                ),
                R.string.nfc_general,
            ),
            *nfcAdapter?.let {
                listOfNotNull(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val isSecureNfcSupported = it.isSecureNfcSupported

                        Subsection(
                            "secure_nfc",
                            listOfNotNull(
                                Information(
                                    "supported",
                                    InformationValue.BooleanValue(isSecureNfcSupported),
                                    R.string.nfc_secure_nfc_supported,
                                ),
                                if (isSecureNfcSupported) {
                                    Information(
                                        "enabled",
                                        InformationValue.BooleanValue(it.isSecureNfcEnabled),
                                        R.string.nfc_secure_nfc_enabled,
                                    )
                                } else {
                                    null
                                },
                            ),
                            R.string.nfc_secure_nfc,
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        it.nfcAntennaInfo?.let { nfcAntennaInfo ->
                            Subsection(
                                "antenna_info",
                                listOf(
                                    Information(
                                        "device_dimensions",
                                        InformationValue.StringValue(
                                            "${nfcAntennaInfo.deviceWidth}x${nfcAntennaInfo.deviceHeight}",
                                            R.string.nfc_antenna_info_device_dimensions_format,
                                            arrayOf(
                                                nfcAntennaInfo.deviceWidth,
                                                nfcAntennaInfo.deviceHeight,
                                            ),
                                        ),
                                        R.string.nfc_antenna_info_device_dimensions,
                                    ),
                                    Information(
                                        "is_device_foldable",
                                        InformationValue.BooleanValue(
                                            nfcAntennaInfo.isDeviceFoldable
                                        ),
                                        R.string.nfc_antenna_info_is_device_foldable,
                                    ),
                                    *nfcAntennaInfo.availableNfcAntennas
                                        .withIndex()
                                        .map { (i, availableNfcAntenna) ->
                                            Information(
                                                "antenna_$i",
                                                InformationValue.StringValue(
                                                    "${availableNfcAntenna.locationX}x${availableNfcAntenna.locationY}",
                                                    R.string.nfc_antenna_info_antenna_dimensions_format,
                                                    arrayOf(
                                                        availableNfcAntenna.locationX,
                                                        availableNfcAntenna.locationY,
                                                    ),
                                                ),
                                                R.string.nfc_antenna_info_antenna,
                                                arrayOf(i),
                                            )
                                        }.toTypedArray()
                                ),
                                R.string.nfc_antenna_info,
                            )
                        }
                    } else {
                        null
                    }
                ).toTypedArray()
            } ?: arrayOf()
        )
    }.asFlow()
}
