/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.Manifest
import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.flow.asFlow

object WifiSection : Section(
    "wifi",
    R.string.section_wifi_name,
    R.string.section_wifi_description,
    R.drawable.ic_wifi,
    arrayOf(
        Manifest.permission.ACCESS_WIFI_STATE,
    ),
) {
    override fun dataFlow(context: Context) = {
        val wifiManager: WifiManager? = context.getSystemService(WifiManager::class.java)

        listOf(
            Subsection(
                "general",
                listOf(
                    Information(
                        "supported",
                        InformationValue.BooleanValue(wifiManager != null),
                        R.string.wifi_supported,
                    ),
                    *wifiManager?.let {
                        arrayOf(
                            Information(
                                "enabled",
                                InformationValue.BooleanValue(it.isWifiEnabled),
                                R.string.wifi_enabled,
                            ),
                        )
                    } ?: arrayOf()
                ),
                R.string.wifi_general,
            ),
            *wifiManager?.let {
                listOfNotNull(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Subsection(
                            "supported_standards",
                            WifiStandard.all.map { wifiStandard ->
                                it.getStandardInfo(wifiStandard)
                            },
                            R.string.wifi_supported_standards,
                        )
                    } else {
                        null
                    },
                    Subsection(
                        "supported_bands",
                        WifiBand.all.map { wifiBand ->
                            it.getBandInfo(wifiBand)
                        },
                        R.string.wifi_supported_bands,
                    )
                ).toTypedArray()
            } ?: arrayOf()
        )
    }.asFlow()

    @RequiresApi(Build.VERSION_CODES.R)
    private fun WifiManager.getStandardInfo(standard: WifiStandard) = Information(
        standard.name,
        InformationValue.BooleanValue(isWifiStandardSupported(standard.value)),
        standard.resId,
    )

    private fun WifiManager.getBandInfo(band: WifiBand) = Information(
        band.name,
        InformationValue.BooleanValue(band.isSupportedGetter(this)),
        R.string.wifi_band_format,
        arrayOf(
            when (band.frequency == band.frequency.toLong().toDouble()) {
                true -> band.frequency.toLong()
                false -> band.frequency
            },
        )
    )

    private data class WifiStandard(
        val value: Int,
        val name: String,
        @StringRes val resId: Int,
    ) {
        companion object {
            @RequiresApi(Build.VERSION_CODES.R)
            val all = mutableListOf(
                WifiStandard(
                    ScanResult.WIFI_STANDARD_LEGACY,
                    "legacy",
                    R.string.wifi_standard_legacy,
                ),
                WifiStandard(
                    ScanResult.WIFI_STANDARD_11N,
                    "11n",
                    R.string.wifi_standard_11n,
                ),
                WifiStandard(
                    ScanResult.WIFI_STANDARD_11AC,
                    "11ac",
                    R.string.wifi_standard_11ac,
                ),
                WifiStandard(
                    ScanResult.WIFI_STANDARD_11AX,
                    "11ax",
                    R.string.wifi_standard_11ax,
                ),
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(
                        WifiStandard(
                            ScanResult.WIFI_STANDARD_11AD,
                            "11ad",
                            R.string.wifi_standard_11ad,
                        )
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(
                        WifiStandard(
                            ScanResult.WIFI_STANDARD_11BE,
                            "11ad",
                            R.string.wifi_standard_11be,
                        )
                    )
                }
            }
        }
    }

    private data class WifiBand(
        val frequency: Double,
        val isSupportedGetter: WifiManager.() -> Boolean,
    ) {
        val name = "$frequency GHz"

        companion object {
            val all = mutableListOf(
                WifiBand(
                    2.4,
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        is24GHzBandSupported
                    } else {
                        true
                    }
                },
                WifiBand(
                    5.0,
                    WifiManager::is5GHzBandSupported
                ),
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    add(
                        WifiBand(
                            6.0,
                            WifiManager::is6GHzBandSupported,
                        )
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(
                        WifiBand(
                            60.0,
                            WifiManager::is60GHzBandSupported,
                        )
                    )
                }
            }
        }
    }
}
