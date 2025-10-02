/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.gnss

import android.Manifest
import android.content.Context
import android.location.GnssCapabilities
import android.location.GnssStatus
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.core.utils.FrequencyUtils
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class GnssModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = GnssModule(context)
    }

    private val locationManager = context.getSystemService(LocationManager::class.java)

    override val id = "gnss"

    override val name = LocalizedString(R.string.section_gnss_name)

    override val description = LocalizedString(R.string.section_gnss_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_satellite_alt

    override val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOfNotNull(
                    Element.Card(
                        name = "general",
                        title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                        elements = listOfNotNull(
                            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                listOfNotNull(
                                    Element.Item(
                                        name = "location_enabled",
                                        title = LocalizedString(R.string.gnss_location_enabled),
                                        value = Value(locationManager.isLocationEnabled),
                                    ),
                                    locationManager.gnssHardwareModelName?.let {
                                        Element.Item(
                                            name = "hardware_model_name",
                                            title = LocalizedString(R.string.gnss_hardware_model_name),
                                            value = Value(it),
                                        )
                                    },
                                    Element.Item(
                                        name = "year_of_hardware",
                                        title = LocalizedString(R.string.gnss_year_of_hardware),
                                        value = Value(locationManager.gnssYearOfHardware),
                                    )
                                ).toTypedArray()
                            } else {
                                arrayOf()
                            },
                            Element.Item(
                                name = "providers",
                                title = LocalizedString(R.string.gnss_providers),
                                value = Value(locationManager.allProviders.toTypedArray()),
                            ),
                        ),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val gnssCapabilities = locationManager.gnssCapabilities

                        Element.Card(
                            name = "capabilities",
                            title = LocalizedString(R.string.gnss_capabilities),
                            elements = listOf(
                                Element.Item(
                                    name = "supports_measurements",
                                    title = LocalizedString(R.string.gnss_capabilities_supports_measurements),
                                    value = Value(gnssCapabilities.hasMeasurements()),
                                ),
                                Element.Item(
                                    name = "supports_navigation_messages",
                                    title = LocalizedString(R.string.gnss_capabilities_supports_navigation_messages),
                                    value = Value(gnssCapabilities.hasNavigationMessages()),
                                ),
                                Element.Item(
                                    name = "supports_antenna_info",
                                    title = LocalizedString(R.string.gnss_capabilities_supports_antenna_info),
                                    value = Value(gnssCapabilities.hasAntennaInfo()),
                                ),
                                *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                    arrayOf(
                                        Element.Item(
                                            name = "has_scheduling",
                                            title = LocalizedString(R.string.gnss_capabilities_has_scheduling),
                                            value = Value(gnssCapabilities.hasScheduling()),
                                        ),
                                        Element.Item(
                                            name = "has_msb",
                                            title = LocalizedString(R.string.gnss_capabilities_has_msb),
                                            value = Value(gnssCapabilities.hasMsb()),
                                        ),
                                        Element.Item(
                                            name = "has_msa",
                                            title = LocalizedString(R.string.gnss_capabilities_has_msa),
                                            value = Value(gnssCapabilities.hasMsa()),
                                        ),
                                        Element.Item(
                                            name = "has_single_shot_fix",
                                            title = LocalizedString(R.string.gnss_capabilities_has_single_shot_fix),
                                            value = Value(gnssCapabilities.hasSingleShotFix()),
                                        ),
                                        Element.Item(
                                            name = "has_on_demand_time",
                                            title = LocalizedString(R.string.gnss_capabilities_has_on_demand_time),
                                            value = Value(gnssCapabilities.hasOnDemandTime()),
                                        ),
                                        Element.Item(
                                            name = "has_geofencing",
                                            title = LocalizedString(R.string.gnss_capabilities_has_geofencing),
                                            value = Value(gnssCapabilities.hasGeofencing()),
                                        ),
                                        Element.Item(
                                            name = "has_low_power_mode",
                                            title = LocalizedString(R.string.gnss_capabilities_has_low_power_mode),
                                            value = Value(gnssCapabilities.hasLowPowerMode()),
                                        ),
                                        Element.Item(
                                            name = "has_satellite_blocklist",
                                            title = LocalizedString(R.string.gnss_capabilities_has_satellite_blocklist),
                                            value = Value(gnssCapabilities.hasSatelliteBlocklist()),
                                        ),
                                        Element.Item(
                                            name = "has_satellite_pvt",
                                            title = LocalizedString(R.string.gnss_capabilities_has_satellite_pvt),
                                            value = Value(gnssCapabilities.hasSatellitePvt()),
                                        ),
                                        Element.Item(
                                            name = "has_measurement_corrections",
                                            title = LocalizedString(R.string.gnss_capabilities_has_measurement_corrections),
                                            value = Value(gnssCapabilities.hasMeasurementCorrections()),
                                        ),
                                        Element.Item(
                                            name = "has_measurement_correlation_vectors",
                                            title = LocalizedString(R.string.gnss_capabilities_has_measurement_correlation_vectors),
                                            value = Value(gnssCapabilities.hasMeasurementCorrelationVectors()),
                                        ),
                                        Element.Item(
                                            name = "has_measurement_corrections_for_driving",
                                            title = LocalizedString(R.string.gnss_capabilities_has_measurement_corrections_for_driving),
                                            value = Value(gnssCapabilities.hasMeasurementCorrectionsForDriving()),
                                        ),
                                        Element.Item(
                                            name = "has_accumulated_delta_range",
                                            title = LocalizedString(R.string.gnss_capabilities_has_accumulated_delta_range),
                                            value = Value(
                                                gnssCapabilities.hasAccumulatedDeltaRange(),
                                                capabilityToStringResId,
                                            ),
                                        ),
                                        Element.Item(
                                            name = "has_measurement_corrections_los_sats",
                                            title = LocalizedString(R.string.gnss_capabilities_has_measurement_corrections_los_sats),
                                            value = Value(gnssCapabilities.hasMeasurementCorrectionsLosSats()),
                                        ),
                                        Element.Item(
                                            name = "has_measurement_corrections_excess_path_length",
                                            title = LocalizedString(R.string.gnss_capabilities_has_measurement_corrections_excess_path_length),
                                            value = Value(gnssCapabilities.hasMeasurementCorrectionsExcessPathLength()),
                                        ),
                                        Element.Item(
                                            name = "has_measurement_corrections_reflecting_plane",
                                            title = LocalizedString(R.string.gnss_capabilities_has_measurement_corrections_reflecting_plane),
                                            value = Value(gnssCapabilities.hasMeasurementCorrectionsReflectingPlane()),
                                        ),
                                        Element.Item(
                                            name = "has_power_total",
                                            title = LocalizedString(R.string.gnss_capabilities_has_power_total),
                                            value = Value(gnssCapabilities.hasPowerTotal()),
                                        ),
                                        Element.Item(
                                            name = "has_power_singleband_tracking",
                                            title = LocalizedString(R.string.gnss_capabilities_has_power_singleband_tracking),
                                            value = Value(gnssCapabilities.hasPowerSinglebandTracking()),
                                        ),
                                        Element.Item(
                                            name = "has_power_multiband_tracking",
                                            title = LocalizedString(R.string.gnss_capabilities_has_power_multiband_tracking),
                                            value = Value(gnssCapabilities.hasPowerMultibandTracking()),
                                        ),
                                        Element.Item(
                                            name = "has_power_singleband_acquisition",
                                            title = LocalizedString(R.string.gnss_capabilities_has_power_singleband_acquisition),
                                            value = Value(gnssCapabilities.hasPowerSinglebandAcquisition()),
                                        ),
                                        Element.Item(
                                            name = "has_power_multiband_acquisition",
                                            title = LocalizedString(R.string.gnss_capabilities_has_power_multiband_acquisition),
                                            value = Value(gnssCapabilities.hasPowerMultibandAcquisition()),
                                        ),
                                        Element.Item(
                                            name = "has_power_other_modes",
                                            title = LocalizedString(R.string.gnss_capabilities_has_power_other_modes),
                                            value = Value(gnssCapabilities.hasPowerOtherModes()),
                                        ),
                                    )
                                } else {
                                    arrayOf()
                                },
                            ),
                        )
                    } else {
                        null
                    },
                    *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        locationManager.gnssCapabilities.gnssSignalTypes.withIndex()
                            .map { (i, gnssSignalType) ->
                                Element.Card(
                                    name = "signal_type_${i}",
                                    title = LocalizedString(
                                        R.string.gnss_signal_type,
                                        i,
                                    ),
                                    elements = listOf(
                                        Element.Item(
                                            name = "constellation_type",
                                            title = LocalizedString(R.string.gnss_signal_type_constellation_type),
                                            value = Value(
                                                gnssSignalType.constellationType,
                                                constellationTypeToStringResId,
                                            )
                                        ),
                                        Element.Item(
                                            name = "carrier_frequency_hz",
                                            title = LocalizedString(R.string.gnss_signal_type_carrier_frequency_hz),
                                            value = Value(gnssSignalType.carrierFrequencyHz),
                                        ),
                                        Element.Item(
                                            name = "code_type",
                                            title = LocalizedString(R.string.gnss_signal_type_code_type),
                                            value = Value(gnssSignalType.codeType)
                                        ),
                                    ),
                                )
                            }.toTypedArray()
                    } else {
                        arrayOf()
                    },
                    *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        locationManager.gnssAntennaInfos?.withIndex()?.map { (i, antennaInfo) ->
                            val phaseCenterOffset = antennaInfo.phaseCenterOffset

                            Element.Card(
                                name = "antenna_$i",
                                title = LocalizedString(R.string.gnss_antenna_title, i),
                                elements = listOf(
                                    Element.Item(
                                        name = "carrier_frequency",
                                        title = LocalizedString(R.string.gnss_antenna_carrier_frequency),
                                        value = Value(
                                            (antennaInfo.carrierFrequencyMHz * FrequencyUtils.SizeUnit.MHz.unitBase).toLong()
                                        ),
                                    ),
                                    Element.Item(
                                        name = "phase_center_offset",
                                        title = LocalizedString(R.string.gnss_antenna_phase_center_offset),
                                        value = Value(
                                            "${phaseCenterOffset.xOffsetMm}mm x ${phaseCenterOffset.yOffsetMm}mm x ${phaseCenterOffset.zOffsetMm}mm"
                                        ),
                                    ),
                                    Element.Item(
                                        name = "phase_center_variation_corrections",
                                        title = LocalizedString(R.string.gnss_antenna_phase_center_variation_corrections),
                                        value = Value(
                                            "${antennaInfo.phaseCenterVariationCorrections}"
                                        ),
                                    ),
                                    Element.Item(
                                        name = "signal_gain_corrections",
                                        title = LocalizedString(R.string.gnss_antenna_signal_gain_corrections),
                                        value = Value(
                                            "${antennaInfo.signalGainCorrections}"
                                        ),
                                    ),
                                ),
                            )
                        }?.toTypedArray() ?: arrayOf()
                    } else {
                        arrayOf()
                    }
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        private val capabilityToStringResId = mapOf(
            GnssCapabilities.CAPABILITY_SUPPORTED to R.string.gnss_capability_supported,
            GnssCapabilities.CAPABILITY_UNSUPPORTED to R.string.gnss_capability_unsupported,
            GnssCapabilities.CAPABILITY_UNKNOWN to R.string.gnss_capability_unknown,
        )

        @RequiresApi(Build.VERSION_CODES.N)
        private val constellationTypeToStringResId = mutableMapOf(
            GnssStatus.CONSTELLATION_UNKNOWN to R.string.gnss_constellation_unknown,
            GnssStatus.CONSTELLATION_GPS to R.string.gnss_constellation_gps,
            GnssStatus.CONSTELLATION_SBAS to R.string.gnss_constellation_sbas,
            GnssStatus.CONSTELLATION_GLONASS to R.string.gnss_constellation_glonass,
            GnssStatus.CONSTELLATION_QZSS to R.string.gnss_constellation_qzss,
            GnssStatus.CONSTELLATION_BEIDOU to R.string.gnss_constellation_beidou,
            GnssStatus.CONSTELLATION_GALILEO to R.string.gnss_constellation_galileo,
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this[GnssStatus.CONSTELLATION_IRNSS] = R.string.gnss_constellation_irnss
            }
        }.toMap()
    }
}
