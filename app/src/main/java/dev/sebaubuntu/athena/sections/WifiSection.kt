/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
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

object WifiSection : Section() {
    override val title = R.string.section_wifi_name
    override val description = R.string.section_wifi_description
    override val icon = R.drawable.ic_wifi
    override val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_WIFI_STATE,
    )

    override fun dataFlow(context: Context) = {
        context.getSystemService(
            WifiManager::class.java
        )?.let { wifiManager ->
            listOfNotNull(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Subsection(
                        "supported_standards",
                        WifiStandard.all.map {
                            wifiManager.getStandardInfo(it)
                        },
                        R.string.wifi_supported_standards,
                    )
                } else {
                    null
                },
                Subsection(
                    "supported_bands",
                    WifiBand.all.map {
                        wifiManager.getBandInfo(it)
                    },
                    R.string.wifi_supported_bands,
                )
            )
        } ?: listOf(
            Subsection(
                "wifi_not_supported",
                listOf(),
                R.string.wifi_not_supported,
            )
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
        band.resId,
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
        val name: String,
        @StringRes val resId: Int,
        val isSupportedGetter: WifiManager.() -> Boolean,
    ) {
        companion object {
            val all = mutableListOf(
                WifiBand(
                    "2.4GHz",
                    R.string.wifi_band_24ghz,
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        is24GHzBandSupported
                    } else {
                        true
                    }
                },
                WifiBand(
                    "5GHz",
                    R.string.wifi_band_5ghz,
                    WifiManager::is5GHzBandSupported
                ),
                WifiBand(
                    "6GHz",
                    R.string.wifi_band_6ghz,
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        is6GHzBandSupported
                    } else {
                        false
                    }
                },
                WifiBand(
                    "60GHz",
                    R.string.wifi_band_60ghz,
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        is60GHzBandSupported
                    } else {
                        false
                    }
                },
            )
        }
    }
}
