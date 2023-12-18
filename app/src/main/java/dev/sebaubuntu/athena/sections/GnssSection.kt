/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Build
import dev.sebaubuntu.athena.R

object GnssSection : Section() {
    override val name = R.string.section_gnss_name
    override val description = R.string.section_gnss_description
    override val icon = R.drawable.ic_gnss
    override val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String?>>().apply {
        val locationManager = context.getSystemService(LocationManager::class.java)

        this["General"] = mutableMapOf<String, String?>().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                this["Is location enabled"] = "${locationManager.isLocationEnabled}"

                this["Hardware model name"] = locationManager.gnssHardwareModelName
                this["Year of hardware"] = locationManager.gnssYearOfHardware.takeUnless {
                    it <= 0
                }?.toString() ?: "< 2016"
            }

            val allProviders = locationManager.allProviders
            if (allProviders.isNotEmpty()) {
                this["Providers"] = allProviders.joinToString()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this["GNSS capabilities"] = locationManager.gnssCapabilities.toString()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this["Has antenna info"] = "${locationManager.gnssCapabilities.hasAntennaInfo()}"
                this["Supports measurements"] =
                    "${locationManager.gnssCapabilities.hasMeasurements()}"
                this["Has navigation messages"] =
                    "${locationManager.gnssCapabilities.hasNavigationMessages()}"
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val antennaInfos = locationManager.gnssAntennaInfos ?: listOf()
            if (antennaInfos.isNotEmpty()) {
                this["Antenna infos"] = mapOf()
                for ((i, antennaInfo) in antennaInfos.withIndex()) {
                    this["Antenna $i"] = mapOf(
                        "Carrier frequency" to "${antennaInfo.carrierFrequencyMHz} MHz",
                        "Phase center offset" to "${antennaInfo.phaseCenterOffset.xOffsetMm}mm x ${antennaInfo.phaseCenterOffset.yOffsetMm}mm x ${antennaInfo.phaseCenterOffset.zOffsetMm}mm",
                        "Phase center variation corrections" to "${antennaInfo.phaseCenterVariationCorrections}",
                        "Signal gain corrections" to "${antennaInfo.signalGainCorrections}",
                    )
                }
            }
        }
    }.toMap()
}
