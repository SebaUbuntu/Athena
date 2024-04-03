/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.Manifest
import android.content.Context
import android.location.GnssCapabilities
import android.location.GnssStatus
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Section

object GnssSection : Section() {
    override val title = R.string.section_gnss_name
    override val description = R.string.section_gnss_description
    override val icon = R.drawable.ic_satellite_alt
    override val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    override fun getInfoOld(context: Context) = mutableMapOf<String, Map<String, String?>>().apply {
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
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val gnssCapabilities = locationManager.gnssCapabilities

            this["GNSS capabilities"] = mutableMapOf<String, String?>(
                "Supports measurements" to "${gnssCapabilities.hasMeasurements()}",
                "Supports navigation messages" to "${gnssCapabilities.hasNavigationMessages()}",
                "Supports antenna info" to "${gnssCapabilities.hasAntennaInfo()}",
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    this["Has scheduling"] = "${gnssCapabilities.hasScheduling()}"
                    this["Supports Mobile Station Based assistance"] =
                        "${gnssCapabilities.hasMsb()}"
                    this["Supports Mobile Station Assisted assistance"] =
                        "${gnssCapabilities.hasMsa()}"
                    this["Supports single shot locating"] = "${gnssCapabilities.hasSingleShotFix()}"
                    this["Requests periodic time signal injection from the platform"] =
                        "${gnssCapabilities.hasOnDemandTime()}"
                    this["Supports geofencing"] = "${gnssCapabilities.hasGeofencing()}"
                    this["Supports low power mode"] = "${gnssCapabilities.hasLowPowerMode()}"
                    this["Supports satellite blocklists"] =
                        "${gnssCapabilities.hasSatelliteBlocklist()}"
                    this["Supports satellite PVT"] = "${gnssCapabilities.hasSatellitePvt()}"

                    putAll(
                        mapOf(
                            "Supports measurement corrections" to
                                    "${gnssCapabilities.hasMeasurementCorrections()}",
                            "Supports correlation vectors" to
                                    "${gnssCapabilities.hasMeasurementCorrelationVectors()}",
                            "Benefit from measurement corrections for driving use case" to
                                    "${gnssCapabilities.hasMeasurementCorrectionsForDriving()}",
                            "Supports accumulated delta range" to
                                    capabilityToString[gnssCapabilities.hasAccumulatedDeltaRange()],
                            "Supports line-of-sight satellite identification measurement corrections" to
                                    "${gnssCapabilities.hasMeasurementCorrectionsLosSats()}",
                            "Supports per satellite excess-path-length measurement corrections" to
                                    "${gnssCapabilities.hasMeasurementCorrectionsExcessPathLength()}",
                            "Supports reflecting plane measurement corrections" to
                                    "${gnssCapabilities.hasMeasurementCorrectionsReflectingPlane()}",
                            "Supports measuring power totals" to
                                    "${gnssCapabilities.hasPowerTotal()}",
                            "Supports measuring single-band tracking power" to
                                    "${gnssCapabilities.hasPowerSinglebandTracking()}",
                            "Supports measuring multi-band tracking power" to
                                    "${gnssCapabilities.hasPowerMultibandTracking()}",
                            "Supports measuring single-band acquisition power" to
                                    "${gnssCapabilities.hasPowerSinglebandAcquisition()}",
                            "Supports measuring multi-band acquisition power" to
                                    "${gnssCapabilities.hasPowerMultibandAcquisition()}",
                            "Supports measuring OEM defined mode power" to
                                    "${gnssCapabilities.hasPowerOtherModes()}",
                        )
                    )
                }
            }.toMap()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                this["Signal types"] = gnssCapabilities.gnssSignalTypes.withIndex().associate {
                    "${it.index + 1}" to listOf(
                        "Constellation: ${constellationTypeToString[it.value.constellationType]}",
                        "Carrier frequency: ${it.value.carrierFrequencyHz} hz",
                        "Code type: ${it.value.codeType}",
                    ).joinToString()
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val antennaInfos = locationManager.gnssAntennaInfos ?: listOf()
            if (antennaInfos.isNotEmpty()) {
                for ((i, antennaInfo) in antennaInfos.withIndex()) {
                    val phaseCenterOffset = antennaInfo.phaseCenterOffset
                    this["Antenna $i"] = mapOf(
                        "Carrier frequency" to "${antennaInfo.carrierFrequencyMHz} MHz",
                        "Phase center offset" to
                                "${phaseCenterOffset.xOffsetMm}mm x ${phaseCenterOffset.yOffsetMm}mm x ${phaseCenterOffset.zOffsetMm}mm",
                        "Phase center variation corrections" to
                                "${antennaInfo.phaseCenterVariationCorrections}",
                        "Signal gain corrections" to "${antennaInfo.signalGainCorrections}",
                    )
                }
            }
        }
    }.toMap()

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private val capabilityToString = mapOf(
        GnssCapabilities.CAPABILITY_SUPPORTED to "Supported",
        GnssCapabilities.CAPABILITY_UNSUPPORTED to "Unsupported",
        GnssCapabilities.CAPABILITY_UNKNOWN to "Unknown",
    )

    @RequiresApi(Build.VERSION_CODES.N)
    private val constellationTypeToString = mutableMapOf(
        GnssStatus.CONSTELLATION_UNKNOWN to "Unknown",
        GnssStatus.CONSTELLATION_GPS to "GPS",
        GnssStatus.CONSTELLATION_SBAS to "SBAS",
        GnssStatus.CONSTELLATION_GLONASS to "Glonass",
        GnssStatus.CONSTELLATION_QZSS to "QZSS",
        GnssStatus.CONSTELLATION_BEIDOU to "Beidou",
        GnssStatus.CONSTELLATION_GALILEO to "Galileo",
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this[GnssStatus.CONSTELLATION_IRNSS] = "IRNSS"
        }
    }.toMap()
}
