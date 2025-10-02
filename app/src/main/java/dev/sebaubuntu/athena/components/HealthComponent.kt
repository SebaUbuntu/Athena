/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.components.Component
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Permission
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.ext.batteryStatusFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class HealthComponent(private val context: Context) : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = HealthComponent(context)
    }

    override val name = "health"

    override val title = LocalizedString(R.string.section_health_name)

    override val description = LocalizedString(R.string.section_health_description)

    override val drawableResId = R.drawable.ic_battery_profile

    override val permissions = setOf<Permission>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> context.batteryStatusFlow().mapLatest { batteryStatus ->
            val present = batteryStatus.hasExtra(BatteryManager.EXTRA_PRESENT).takeIf { it }?.let {
                batteryStatus.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
            }

            val technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)

            val cycleCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                batteryStatus.hasExtra(BatteryManager.EXTRA_CYCLE_COUNT).takeIf { it }?.let {
                    batteryStatus.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, 0)
                }
            } else {
                null
            }

            val status = batteryStatus.hasExtra(BatteryManager.EXTRA_STATUS).takeIf { it }?.let {
                batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, 0)
            }

            val plugged = batteryStatus.hasExtra(BatteryManager.EXTRA_PLUGGED).takeIf { it }?.let {
                batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
            }

            val chargingStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                batteryStatus.hasExtra(BatteryManager.EXTRA_CHARGING_STATUS).takeIf { it }?.let {
                    batteryStatus.getIntExtra(
                        BatteryManager.EXTRA_CHARGING_STATUS, 0
                    ).takeIf { it != 0 }
                }
            } else {
                null
            }

            val health = batteryStatus.hasExtra(BatteryManager.EXTRA_HEALTH).takeIf { it }?.let {
                batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
            }

            val level = batteryStatus.hasExtra(BatteryManager.EXTRA_LEVEL).takeIf { it }?.let {
                batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            }

            val scale = batteryStatus.hasExtra(BatteryManager.EXTRA_SCALE).takeIf { it }?.let {
                batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
            }

            val batteryLow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                batteryStatus.hasExtra(BatteryManager.EXTRA_BATTERY_LOW).takeIf { it }?.let {
                    batteryStatus.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, false)
                }
            } else {
                null
            }

            val temperature = batteryStatus.hasExtra(
                BatteryManager.EXTRA_TEMPERATURE
            ).takeIf { it }?.let {
                batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            }

            val voltage = batteryStatus.hasExtra(BatteryManager.EXTRA_VOLTAGE).takeIf { it }?.let {
                batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            }

            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = title,
                elements = listOf(
                    Element.Card(
                        identifier = identifier / "battery",
                        title = LocalizedString(R.string.health_battery),
                        elements = listOfNotNull(
                            present?.let {
                                Element.Item(
                                    identifier = identifier / "present",
                                    title = LocalizedString(R.string.health_battery_present),
                                    value = Value(it),
                                )
                            },
                            technology?.let {
                                Element.Item(
                                    identifier = identifier / "technology",
                                    title = LocalizedString(R.string.health_battery_technology),
                                    value = Value(it),
                                )
                            },
                            cycleCount?.let {
                                Element.Item(
                                    identifier = identifier / "cycle_count",
                                    title = LocalizedString(R.string.health_battery_cycle_count),
                                    value = Value(it),
                                )
                            },
                            status?.let {
                                Element.Item(
                                    identifier = identifier / "status",
                                    title = LocalizedString(R.string.health_battery_status),
                                    value = Value(
                                        it,
                                        batteryStatusToStringResId,
                                    ),
                                )
                            },
                            plugged?.let {
                                Element.Item(
                                    identifier = identifier / "plugged",
                                    title = LocalizedString(R.string.health_battery_plugged),
                                    value = Value(
                                        it,
                                        batteryPluggedToStringResId,
                                    ),
                                )
                            },
                            chargingStatus?.let {
                                Element.Item(
                                    identifier = identifier / "charging_status",
                                    title = LocalizedString(R.string.health_battery_charging_status),
                                    value = Value(
                                        it,
                                        batteryChargingStateToStringResId,
                                    ),
                                )
                            },
                            health?.let {
                                Element.Item(
                                    identifier = identifier / "health",
                                    title = LocalizedString(R.string.health_battery_health),
                                    value = Value(
                                        it,
                                        batteryHealthToStringResId,
                                    ),
                                )
                            },
                            level?.let {
                                Element.Item(
                                    identifier = identifier / "level",
                                    title = LocalizedString(R.string.health_battery_level),
                                    value = Value(it),
                                )
                            },
                            scale?.let {
                                Element.Item(
                                    identifier = identifier / "scale",
                                    title = LocalizedString(R.string.health_battery_scale),
                                    value = Value(it),
                                )
                            },
                            batteryLow?.let {
                                Element.Item(
                                    identifier = identifier / "battery_low",
                                    title = LocalizedString(R.string.health_battery_battery_low),
                                    value = Value(it),
                                )
                            },
                            temperature?.let {
                                Element.Item(
                                    identifier = identifier / "temperature",
                                    title = LocalizedString(R.string.health_battery_temperature),
                                    value = Value(
                                        "$it",
                                        R.string.health_battery_temperature_format,
                                        arrayOf(it.toFloat() / 10),
                                    ),
                                )
                            },
                            voltage?.let {
                                Element.Item(
                                    identifier = identifier / "voltage",
                                    title = LocalizedString(R.string.health_battery_voltage),
                                    value = Value(
                                        "$it",
                                        R.string.health_battery_voltage_format,
                                        arrayOf(it),
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
                this[BatteryManager.BATTERY_PLUGGED_DOCK] = R.string.battery_plugged_dock
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
