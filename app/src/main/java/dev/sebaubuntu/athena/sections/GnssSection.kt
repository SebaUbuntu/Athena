/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
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
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import dev.sebaubuntu.athena.utils.FrequencyUtils
import kotlinx.coroutines.flow.asFlow

object GnssSection : Section(
    "gnss",
    R.string.section_gnss_name,
    R.string.section_gnss_description,
    R.drawable.ic_satellite_alt,
    arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ),
) {
    override fun dataFlow(context: Context) = {
        val locationManager = context.getSystemService(LocationManager::class.java)

        listOfNotNull(
            Subsection(
                "general",
                listOfNotNull(
                    *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        listOf(
                            Information(
                                "location_enabled",
                                InformationValue.BooleanValue(
                                    locationManager.isLocationEnabled
                                ),
                                R.string.gnss_location_enabled,
                            ),
                            Information(
                                "hardware_model_name",
                                locationManager.gnssHardwareModelName?.let {
                                    InformationValue.StringValue(it)
                                },
                                R.string.gnss_hardware_model_name,
                            ),
                            Information(
                                "year_of_hardware",
                                InformationValue.IntValue(locationManager.gnssYearOfHardware),
                                R.string.gnss_year_of_hardware,
                            ),
                        )
                    } else {
                        listOf()
                    }.toTypedArray(),
                    Information(
                        "providers",
                        InformationValue.StringArrayValue(
                            locationManager.allProviders.toTypedArray()
                        ),
                        R.string.gnss_providers,
                    ),
                ),
                R.string.gnss_general,
            ),
            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val gnssCapabilities = locationManager.gnssCapabilities

                listOf(
                    Subsection(
                        "gnss_capabilities",
                        listOfNotNull(
                            Information(
                                "supports_measurements",
                                InformationValue.BooleanValue(gnssCapabilities.hasMeasurements()),
                                R.string.gnss_capabilities_supports_measurements,
                            ),
                            Information(
                                "supports_navigation_messages",
                                InformationValue.BooleanValue(gnssCapabilities.hasNavigationMessages()),
                                R.string.gnss_capabilities_supports_navigation_messages,
                            ),
                            Information(
                                "supports_antenna_info",
                                InformationValue.BooleanValue(gnssCapabilities.hasAntennaInfo()),
                                R.string.gnss_capabilities_supports_antenna_info,
                            ),
                            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                listOf(
                                    Information(
                                        "has_scheduling",
                                        InformationValue.BooleanValue(gnssCapabilities.hasScheduling()),
                                        R.string.gnss_capabilities_has_scheduling,
                                    ),
                                    Information(
                                        "has_msb",
                                        InformationValue.BooleanValue(gnssCapabilities.hasMsb()),
                                        R.string.gnss_capabilities_has_msb,
                                    ),
                                    Information(
                                        "has_msa",
                                        InformationValue.BooleanValue(gnssCapabilities.hasMsa()),
                                        R.string.gnss_capabilities_has_msa,
                                    ),
                                    Information(
                                        "has_single_shot_fix",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasSingleShotFix()
                                        ),
                                        R.string.gnss_capabilities_has_single_shot_fix,
                                    ),
                                    Information(
                                        "has_on_demand_time",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasOnDemandTime()
                                        ),
                                        R.string.gnss_capabilities_has_on_demand_time,
                                    ),
                                    Information(
                                        "has_geofencing",
                                        InformationValue.BooleanValue(gnssCapabilities.hasGeofencing()),
                                        R.string.gnss_capabilities_has_geofencing,
                                    ),
                                    Information(
                                        "has_low_power_mode",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasLowPowerMode()
                                        ),
                                        R.string.gnss_capabilities_has_low_power_mode,
                                    ),
                                    Information(
                                        "has_satellite_blocklist",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasSatelliteBlocklist()
                                        ),
                                        R.string.gnss_capabilities_has_satellite_blocklist,
                                    ),
                                    Information(
                                        "has_satellite_pvt",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasSatellitePvt()
                                        ),
                                        R.string.gnss_capabilities_has_satellite_pvt,
                                    ),
                                    Information(
                                        "has_measurement_corrections",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasMeasurementCorrections()
                                        ),
                                        R.string.gnss_capabilities_has_measurement_corrections,
                                    ),
                                    Information(
                                        "has_measurement_correlation_vectors",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasMeasurementCorrelationVectors()
                                        ),
                                        R.string.gnss_capabilities_has_measurement_correlation_vectors,
                                    ),
                                    Information(
                                        "has_measurement_corrections_for_driving",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasMeasurementCorrectionsForDriving()
                                        ),
                                        R.string.gnss_capabilities_has_measurement_corrections_for_driving,
                                    ),
                                    Information(
                                        "has_accumulated_delta_range",
                                        InformationValue.IntValue(
                                            gnssCapabilities.hasAccumulatedDeltaRange(),
                                            capabilityToStringResId,
                                        ),
                                        R.string.gnss_capabilities_has_accumulated_delta_range,
                                    ),
                                    Information(
                                        "has_measurement_corrections_los_sats",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasMeasurementCorrectionsLosSats()
                                        ),
                                        R.string.gnss_capabilities_has_measurement_corrections_los_sats,
                                    ),
                                    Information(
                                        "has_measurement_corrections_excess_path_length",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasMeasurementCorrectionsExcessPathLength()
                                        ),
                                        R.string.gnss_capabilities_has_measurement_corrections_excess_path_length,
                                    ),
                                    Information(
                                        "has_measurement_corrections_reflecting_plane",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasMeasurementCorrectionsReflectingPlane()
                                        ),
                                        R.string.gnss_capabilities_has_measurement_corrections_reflecting_plane,
                                    ),
                                    Information(
                                        "has_power_total",
                                        InformationValue.BooleanValue(gnssCapabilities.hasPowerTotal()),
                                        R.string.gnss_capabilities_has_power_total,
                                    ),
                                    Information(
                                        "has_power_singleband_tracking",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasPowerSinglebandTracking()
                                        ),
                                        R.string.gnss_capabilities_has_power_singleband_tracking,
                                    ),
                                    Information(
                                        "has_power_multiband_tracking",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasPowerMultibandTracking()
                                        ),
                                        R.string.gnss_capabilities_has_power_multiband_tracking,
                                    ),
                                    Information(
                                        "has_power_singleband_acquisition",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasPowerSinglebandAcquisition()
                                        ),
                                        R.string.gnss_capabilities_has_power_singleband_acquisition,
                                    ),
                                    Information(
                                        "has_power_multiband_acquisition",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasPowerMultibandAcquisition()
                                        ),
                                        R.string.gnss_capabilities_has_power_multiband_acquisition,
                                    ),
                                    Information(
                                        "has_power_other_modes",
                                        InformationValue.BooleanValue(
                                            gnssCapabilities.hasPowerOtherModes()
                                        ),
                                        R.string.gnss_capabilities_has_power_other_modes,
                                    ),
                                ).toTypedArray()
                            } else {
                                arrayOf()
                            },
                        ),
                        R.string.gnss_capabilities,
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        val gnssSignalTypes = gnssCapabilities.gnssSignalTypes

                        if (gnssSignalTypes.isNotEmpty()) {
                            Subsection(
                                "signal_types",
                                gnssSignalTypes.withIndex().map { (i, signalType) ->
                                    Information(
                                        "${i + 1}",
                                        InformationValue.StringArrayValue(
                                            arrayOf(
                                                "Constellation: ${
                                                    constellationTypeToStringResId[signalType.constellationType]?.let { stringResId ->
                                                        context.getString(stringResId)
                                                    }
                                                }",
                                                "Carrier frequency: ${
                                                    FrequencyUtils.toHumanReadable(
                                                        signalType.carrierFrequencyHz.toLong()
                                                    )
                                                }",
                                                "Code type: ${signalType.codeType}",
                                            )
                                        )
                                    )
                                },
                                R.string.gnss_signal_types,
                            )
                        } else {
                            null
                        }
                    } else {
                        null
                    },
                ).toTypedArray()
            } else {
                arrayOf()
            },
            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                locationManager.gnssAntennaInfos?.withIndex()?.map { (i, antennaInfo) ->
                    val phaseCenterOffset = antennaInfo.phaseCenterOffset

                    Subsection(
                        "antenna_$i",
                        listOf(
                            Information(
                                "carrier_frequency",
                                InformationValue.FrequencyValue(
                                    (antennaInfo.carrierFrequencyMHz * FrequencyUtils.SizeUnit.MHz.unitBase).toLong()
                                ),
                                R.string.gnss_antenna_carrier_frequency,
                            ),
                            Information(
                                "phase_center_offset",
                                InformationValue.StringValue(
                                    "${phaseCenterOffset.xOffsetMm}mm x ${phaseCenterOffset.yOffsetMm}mm x ${phaseCenterOffset.zOffsetMm}mm"
                                ),
                                R.string.gnss_antenna_phase_center_offset,
                            ),
                            Information(
                                "phase_center_variation_corrections",
                                InformationValue.StringValue(
                                    "${antennaInfo.phaseCenterVariationCorrections}"
                                ),
                                R.string.gnss_antenna_phase_center_variation_corrections,
                            ),
                            Information(
                                "signal_gain_corrections",
                                InformationValue.StringValue(
                                    "${antennaInfo.signalGainCorrections}"
                                ),
                                R.string.gnss_antenna_signal_gain_corrections,
                            ),
                        ),
                        R.string.gnss_antenna_title,
                        arrayOf(i),
                    )
                }?.toTypedArray() ?: arrayOf()
            } else {
                arrayOf()
            }
        )
    }.asFlow()

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
