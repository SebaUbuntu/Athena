/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.uwb

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.uwb.UwbControllerSessionScope
import androidx.core.uwb.UwbManager
import androidx.core.uwb.exceptions.UwbHardwareNotAvailableException
import androidx.core.uwb.exceptions.UwbServiceNotAvailableException
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

class UwbModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = UwbModule(context)
    }

    private val uwbManager = UwbManager.createInstance(context)

    override val id = "uwb"

    override val name = LocalizedString(R.string.section_uwb_name)

    override val description = LocalizedString(R.string.section_uwb_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_communication

    override val requiredPermissions = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.UWB_RANGING)
        }
    }.toTypedArray()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
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

            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOfNotNull(
                    Element.Card(
                        identifier = identifier / "general",
                        title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                        elements = listOfNotNull(
                            Element.Item(
                                identifier = identifier / "general" / "supported",
                                title = LocalizedString(R.string.uwb_supported),
                                value = Value(isSupported),
                            ),
                            if (isSupported) {
                                Element.Item(
                                    identifier = identifier / "general" / "enabled",
                                    title = LocalizedString(R.string.uwb_enabled),
                                    value = Value(isEnabled),
                                )
                            } else {
                                null
                            },
                            if (isSupported && !isEnabled) {
                                Element.Item(
                                    identifier = identifier / "general" / "enable_uwb",
                                    title = LocalizedString(R.string.uwb_enable_notice),
                                )
                            } else {
                                null
                            }
                        ),
                    ),
                    sessionScope?.rangingCapabilities?.let {
                        Element.Card(
                            identifier = identifier / "ranging_capabilities",
                            title = LocalizedString(R.string.uwb_ranging_capabilities),
                            elements = listOf(
                                Element.Item(
                                    identifier = identifier / "ranging_capabilities" / "is_distance_supported",
                                    title = LocalizedString(R.string.uwb_is_distance_supported),
                                    value = Value(it.isDistanceSupported),
                                ),
                                Element.Item(
                                    identifier = identifier / "ranging_capabilities" / "is_azimuthal_angle_supported",
                                    title = LocalizedString(R.string.uwb_is_azimuthal_angle_supported),
                                    value = Value(it.isAzimuthalAngleSupported),
                                ),
                                Element.Item(
                                    identifier = identifier / "ranging_capabilities" / "is_elevation_angle_supported",
                                    title = LocalizedString(R.string.uwb_is_elevation_angle_supported),
                                    value = Value(it.isElevationAngleSupported),
                                ),
                                Element.Item(
                                    identifier = identifier / "ranging_capabilities" / "min_ranging_interval",
                                    title = LocalizedString(R.string.uwb_min_ranging_interval),
                                    value = Value(it.minRangingInterval),
                                ),
                                Element.Item(
                                    identifier = identifier / "ranging_capabilities" / "supported_channels",
                                    title = LocalizedString(R.string.uwb_supported_channels),
                                    value = Value(it.supportedChannels.toTypedArray()),
                                ),
                                Element.Item(
                                    identifier = identifier / "ranging_capabilities" / "supported_ntf_configs",
                                    title = LocalizedString(R.string.uwb_supported_ntf_configs),
                                    value = Value(it.supportedNtfConfigs.toTypedArray()),
                                ),
                                Element.Item(
                                    identifier = identifier / "ranging_capabilities" / "supported_config_ids",
                                    title = LocalizedString(R.string.uwb_supported_config_ids),
                                    value = Value(it.supportedConfigIds.toTypedArray()),
                                ),
                                Element.Item(
                                    identifier = identifier / "ranging_capabilities" / "supported_slot_durations",
                                    title = LocalizedString(R.string.uwb_supported_slot_durations),
                                    value = Value(it.supportedSlotDurations.toTypedArray()),
                                ),
                                Element.Item(
                                    identifier = identifier / "ranging_capabilities" / "supported_ranging_update_rates",
                                    title = LocalizedString(R.string.uwb_supported_ranging_update_rates),
                                    value = Value(it.supportedRangingUpdateRates.toTypedArray()),
                                ),
                                Element.Item(
                                    identifier = identifier / "ranging_capabilities" / "is_ranging_interval_reconfigure_supported",
                                    title = LocalizedString(R.string.uwb_is_ranging_interval_reconfigure_supported),
                                    value = Value(it.isRangingIntervalReconfigureSupported),
                                ),
                                Element.Item(
                                    identifier = identifier / "ranging_capabilities" / "is_background_ranging_supported",
                                    title = LocalizedString(R.string.uwb_is_background_ranging_supported),
                                    value = Value(it.isBackgroundRangingSupported),
                                ),
                            ),
                        )
                    },
                    sessionScope?.localAddress?.let {
                        Element.Card(
                            identifier = identifier / "local_address",
                            title = LocalizedString(R.string.uwb_local_address),
                            elements = listOf(
                                Element.Item(
                                    identifier = identifier / "local_address" / "address",
                                    title = LocalizedString(R.string.uwb_local_address_address),
                                    value = Value(it.address.toHexString()),
                                )
                            )
                        )
                    },
                    sessionScope?.uwbComplexChannel?.let {
                        Element.Card(
                            identifier = identifier / "complex_channel",
                            title = LocalizedString(R.string.uwb_complex_channel),
                            elements = listOf(
                                Element.Item(
                                    identifier = identifier / "complex_channel" / "channel",
                                    title = LocalizedString(R.string.uwb_complex_channel_channel),
                                    value = Value(it.channel),
                                ),
                                Element.Item(
                                    identifier = identifier / "complex_channel" / "preamble_index",
                                    title = LocalizedString(R.string.uwb_complex_channel_preamble_index),
                                    value = Value(it.preambleIndex),
                                ),
                            ),
                        )
                    },
                )
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }
}
