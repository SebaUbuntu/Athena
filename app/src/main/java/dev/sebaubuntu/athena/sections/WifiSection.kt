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
import dev.sebaubuntu.athena.R

object WifiSection : Section() {
    override val name = R.string.section_wifi_name
    override val description = R.string.section_wifi_description
    override val icon = R.drawable.ic_wifi
    override val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_WIFI_STATE,
    )

    override fun getInfo(context: Context): Map<String, Map<String, String>> {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        return mapOf(
            "Supported bands" to getSupportedBands(wifiManager),
            "Supported standards" to getSupportedStandards(wifiManager),
        )
    }

    private fun getSupportedBands(wifiManager: WifiManager): Map<String, String> {
        return mutableMapOf<String, String>().apply {
            this["2.4GHz"] = "${
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    wifiManager.is24GHzBandSupported
                } else {
                    true
                }
            }"
            this["5GHz"] = "${wifiManager.is5GHzBandSupported}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this["6GHz"] = "${wifiManager.is6GHzBandSupported}"
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this["60GHz"] = "${wifiManager.is60GHzBandSupported}"
            }
        }
    }

    private fun getSupportedStandards(wifiManager: WifiManager): Map<String, String> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return mapOf()
        }

        return mutableMapOf<String, String>().apply {
            this["802.11a/b/g"] = "${
                wifiManager.isWifiStandardSupported(
                    ScanResult.WIFI_STANDARD_LEGACY
                )
            }"
            this["802.11n"] = "${
                wifiManager.isWifiStandardSupported(
                    ScanResult.WIFI_STANDARD_11N
                )
            }"
            this["802.11ac"] = "${
                wifiManager.isWifiStandardSupported(
                    ScanResult.WIFI_STANDARD_11AC
                )
            }"
            this["802.11ax"] = "${
                wifiManager.isWifiStandardSupported(
                    ScanResult.WIFI_STANDARD_11AX
                )
            }"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this["802.11ad"] = "${
                    wifiManager.isWifiStandardSupported(
                        ScanResult.WIFI_STANDARD_11AD
                    )
                }"
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this["802.11be"] = "${
                    wifiManager.isWifiStandardSupported(
                        ScanResult.WIFI_STANDARD_11BE
                    )
                }"
            }
        }
    }
}
