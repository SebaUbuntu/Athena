/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.health

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import dev.sebaubuntu.athena.core.ext.getBooleanOrNull
import dev.sebaubuntu.athena.core.ext.getIntOrDefault
import dev.sebaubuntu.athena.core.ext.getIntOrNull
import dev.sebaubuntu.athena.core.ext.getStringOrDefault
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.modules.health.ext.batteryStatusFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class HealthModule(private val context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = HealthModule(context)
    }

    override val id = "health"

    override val name = LocalizedString(R.string.section_health_name)

    override val description = LocalizedString(R.string.section_health_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_battery_profile

    override val requiredPermissions = arrayOf<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> context.batteryStatusFlow().mapLatest { batteryStatus ->
            val extras = batteryStatus.extras

            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOf(
                    Element.Card(
                        name = "battery",
                        title = LocalizedString(R.string.health_battery),
                        elements = listOfNotNull(
                            extras?.getBooleanOrNull(BatteryManager.EXTRA_PRESENT)?.let {
                                Element.Item(
                                    name = "present",
                                    title = LocalizedString(R.string.health_battery_present),
                                    value = Value(it),
                                )
                            },
                            extras?.getStringOrDefault(BatteryManager.EXTRA_TECHNOLOGY)?.let {
                                Element.Item(
                                    name = "technology",
                                    title = LocalizedString(R.string.health_battery_technology),
                                    value = Value(it),
                                )
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                extras?.getIntOrNull(BatteryManager.EXTRA_CYCLE_COUNT)?.let {
                                    Element.Item(
                                        name = "cycle_count",
                                        title = LocalizedString(R.string.health_battery_cycle_count),
                                        value = Value(it),
                                    )
                                }
                            } else {
                                null
                            },
                            extras?.getIntOrNull(BatteryManager.EXTRA_STATUS)?.let {
                                Element.Item(
                                    name = "status",
                                    title = LocalizedString(R.string.health_battery_status),
                                    value = Value(
                                        it,
                                        batteryStatusToStringResId,
                                    ),
                                )
                            },
                            extras?.getIntOrNull(BatteryManager.EXTRA_PLUGGED)?.let {
                                Element.Item(
                                    name = "plugged",
                                    title = LocalizedString(R.string.health_battery_plugged),
                                    value = Value(
                                        it,
                                        batteryPluggedToStringResId,
                                    ),
                                )
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                extras?.getIntOrDefault(BatteryManager.EXTRA_CHARGING_STATUS, 0)
                                    ?.let {
                                        Element.Item(
                                            name = "charging_status",
                                            title = LocalizedString(R.string.health_battery_charging_status),
                                            value = Value(
                                                it,
                                                batteryChargingStateToStringResId,
                                            ),
                                        )
                                    }
                            } else {
                                null
                            },
                            extras?.getIntOrDefault(BatteryManager.EXTRA_HEALTH, 0)?.let {
                                Element.Item(
                                    name = "health",
                                    title = LocalizedString(R.string.health_battery_health),
                                    value = Value(
                                        it,
                                        batteryHealthToStringResId,
                                    ),
                                )
                            },
                            extras?.getIntOrDefault(BatteryManager.EXTRA_LEVEL, 0)?.let {
                                Element.Item(
                                    name = "level",
                                    title = LocalizedString(R.string.health_battery_level),
                                    value = Value(it),
                                )
                            },
                            extras?.getIntOrDefault(BatteryManager.EXTRA_SCALE, 0)?.let {
                                Element.Item(
                                    name = "scale",
                                    title = LocalizedString(R.string.health_battery_scale),
                                    value = Value(it),
                                )
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                extras?.getBooleanOrNull(BatteryManager.EXTRA_BATTERY_LOW)?.let {
                                    Element.Item(
                                        name = "battery_low",
                                        title = LocalizedString(R.string.health_battery_battery_low),
                                        value = Value(it),
                                    )
                                }
                            } else {
                                null
                            },
                            extras?.getIntOrNull(BatteryManager.EXTRA_TEMPERATURE)?.let {
                                Element.Item(
                                    name = "temperature",
                                    title = LocalizedString(R.string.health_battery_temperature),
                                    value = Value(
                                        "$it",
                                        R.string.health_battery_temperature_format,
                                        it.toFloat() / 10,
                                    ),
                                )
                            },
                            extras?.getIntOrNull(BatteryManager.EXTRA_VOLTAGE)?.let {
                                Element.Item(
                                    name = "voltage",
                                    title = LocalizedString(R.string.health_battery_voltage),
                                    value = Value(
                                        "$it",
                                        R.string.health_battery_voltage_format,
                                        it,
                                    ),
                                )
                            },
                        ),
                    ),
                ),
            )

            Result.Success<Resource, Error>(screen)
        }

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }

    companion object {
        private val batteryStatusToStringResId = mapOf(
            BatteryManager.BATTERY_STATUS_UNKNOWN to R.string.battery_status_unknown,
            BatteryManager.BATTERY_STATUS_CHARGING to R.string.battery_status_charging,
            BatteryManager.BATTERY_STATUS_DISCHARGING to R.string.battery_status_discharging,
            BatteryManager.BATTERY_STATUS_NOT_CHARGING to R.string.battery_status_not_charging,
            BatteryManager.BATTERY_STATUS_FULL to R.string.battery_status_full,
        )

        private val batteryPluggedToStringResId = mutableMapOf(
            0 to R.string.battery_plugged_none,
            BatteryManager.BATTERY_PLUGGED_AC to R.string.battery_plugged_ac,
            BatteryManager.BATTERY_PLUGGED_USB to R.string.battery_plugged_usb,
            BatteryManager.BATTERY_PLUGGED_WIRELESS to R.string.battery_plugged_wireless,
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                put(BatteryManager.BATTERY_PLUGGED_DOCK, R.string.battery_plugged_dock)
            }
        }

        /**
         * From frameworks/native/services/batteryservice/include/batteryservice/BatteryServiceConstants.h
         */
        private val batteryChargingStateToStringResId = mapOf(
            1 to R.string.battery_charging_state_normal,
            2 to R.string.battery_charging_state_too_cold,
            3 to R.string.battery_charging_state_too_hot,
            4 to R.string.battery_charging_state_long_life,
            5 to R.string.battery_charging_state_adaptive,
        )

        private val batteryHealthToStringResId = mapOf(
            BatteryManager.BATTERY_HEALTH_UNKNOWN to R.string.battery_health_unknown,
            BatteryManager.BATTERY_HEALTH_GOOD to R.string.battery_health_good,
            BatteryManager.BATTERY_HEALTH_OVERHEAT to R.string.battery_health_overheat,
            BatteryManager.BATTERY_HEALTH_DEAD to R.string.battery_health_dead,
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE to R.string.battery_health_over_voltage,
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE to R.string.battery_health_unspecified_failure,
            BatteryManager.BATTERY_HEALTH_COLD to R.string.battery_health_cold,
        )
    }
}
