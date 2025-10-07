/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.uwb.UwbControllerSessionScope
import androidx.core.uwb.UwbManager
import androidx.core.uwb.exceptions.UwbHardwareNotAvailableException
import androidx.core.uwb.exceptions.UwbServiceNotAvailableException
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.flow.asFlow

object UwbSection : Section(
    "uwb",
    R.string.section_uwb_name,
    R.string.section_uwb_description,
    R.drawable.ic_communication,
    mutableListOf<String>().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.UWB_RANGING)
        }
    }.toTypedArray(),
) {
    override fun dataFlow(context: Context) = suspend {
        val uwbManager = UwbManager.createInstance(context)

        var isSupported = true
        var isEnabled = true
        var sessionScope: UwbControllerSessionScope? = null

        try {
            sessionScope = uwbManager.controllerSessionScope()
        } catch (_: UwbHardwareNotAvailableException) {
            isSupported = false
            isEnabled = false
        } catch (_: UwbServiceNotAvailableException) {
            isEnabled = false
        }

        listOfNotNull(
            Subsection(
                "general",
                listOfNotNull(
                    Information(
                        "supported",
                        InformationValue.BooleanValue(isSupported),
                        R.string.uwb_supported,
                    ),
                    *if (isSupported) {
                        listOfNotNull(
                            Information(
                                "enabled",
                                InformationValue.BooleanValue(isEnabled),
                                R.string.uwb_enabled,
                            ),
                            if (!isEnabled) {
                                Information(
                                    "enable_uwb",
                                    null,
                                    R.string.uwb_enable_notice,
                                )
                            } else {
                                null
                            }
                        ).toTypedArray()
                    } else {
                        arrayOf()
                    },
                ),
                R.string.uwb_general,
            ),
            sessionScope?.rangingCapabilities?.let { rangingCapabilities ->
                Subsection(
                    "ranging_capabilities",
                    listOf(
                        Information(
                            "is_distance_supported",
                            InformationValue.BooleanValue(rangingCapabilities.isDistanceSupported),
                            R.string.uwb_is_distance_supported,
                        ),
                        Information(
                            "is_azimuthal_angle_supported",
                            InformationValue.BooleanValue(
                                rangingCapabilities.isAzimuthalAngleSupported
                            ),
                            R.string.uwb_is_azimuthal_angle_supported,
                        ),
                        Information(
                            "is_elevation_angle_supported",
                            InformationValue.BooleanValue(
                                rangingCapabilities.isElevationAngleSupported
                            ),
                            R.string.uwb_is_elevation_angle_supported,
                        ),
                        Information(
                            "min_ranging_interval",
                            InformationValue.IntValue(rangingCapabilities.minRangingInterval),
                            R.string.uwb_min_ranging_interval,
                        ),
                        Information(
                            "supported_channels",
                            InformationValue.IntArrayValue(
                                rangingCapabilities.supportedChannels.toTypedArray()
                            ),
                            R.string.uwb_supported_channels,
                        ),
                        Information(
                            "supported_ntf_configs",
                            InformationValue.IntArrayValue(
                                rangingCapabilities.supportedNtfConfigs.toTypedArray()
                            ),
                            R.string.uwb_supported_ntf_configs,
                        ),
                        Information(
                            "supported_config_ids",
                            InformationValue.IntArrayValue(
                                rangingCapabilities.supportedConfigIds.toTypedArray()
                            ),
                            R.string.uwb_supported_config_ids,
                        ),
                        Information(
                            "supported_slot_durations",
                            InformationValue.IntArrayValue(
                                rangingCapabilities.supportedSlotDurations.toTypedArray()
                            ),
                            R.string.uwb_supported_slot_durations,
                        ),
                        Information(
                            "supported_ranging_update_rates",
                            InformationValue.IntArrayValue(
                                rangingCapabilities.supportedRangingUpdateRates.toTypedArray()
                            ),
                            R.string.uwb_supported_ranging_update_rates,
                        ),
                        Information(
                            "is_ranging_interval_reconfigure_supported",
                            InformationValue.BooleanValue(
                                rangingCapabilities.isRangingIntervalReconfigureSupported
                            ),
                            R.string.uwb_is_ranging_interval_reconfigure_supported,
                        ),
                        Information(
                            "is_background_ranging_supported",
                            InformationValue.BooleanValue(
                                rangingCapabilities.isBackgroundRangingSupported
                            ),
                            R.string.uwb_is_background_ranging_supported,
                        ),
                    ),
                    R.string.uwb_ranging_capabilities,
                )
            }
        )
    }.asFlow()
}
