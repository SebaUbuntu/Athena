/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.batteryStatusFlow
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

object HealthSection : Section(
    "health",
    R.string.section_health_name,
    R.string.section_health_description,
    R.drawable.ic_battery_profile,
) {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun dataFlow(
        context: Context
    ) = context.batteryStatusFlow().mapLatest { batteryStatus ->
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

        listOf(
            Subsection(
                "battery",
                listOfNotNull(
                    present?.let {
                        Information(
                            "present",
                            InformationValue.BooleanValue(it),
                            R.string.health_battery_present,
                        )
                    },
                    technology?.let {
                        Information(
                            "technology",
                            InformationValue.StringValue(it),
                            R.string.health_battery_technology,
                        )
                    },
                    cycleCount?.let {
                        Information(
                            "cycle_count",
                            InformationValue.IntValue(it),
                            R.string.health_battery_cycle_count,
                        )
                    },
                    status?.let {
                        Information(
                            "status",
                            InformationValue.IntValue(
                                it,
                                batteryStatusToStringResId,
                            ),
                            R.string.health_battery_status,
                        )
                    },
                    plugged?.let {
                        Information(
                            "plugged",
                            InformationValue.IntValue(
                                it,
                                batteryPluggedToStringResId,
                            ),
                            R.string.health_battery_plugged,
                        )
                    },
                    chargingStatus?.let {
                        Information(
                            "charging_status",
                            InformationValue.IntValue(
                                it,
                                batteryChargingStateToStringResId,
                            ),
                            R.string.health_battery_charging_status,
                        )
                    },
                    health?.let {
                        Information(
                            "health",
                            InformationValue.IntValue(
                                it,
                                batteryHealthToStringResId,
                            ),
                            R.string.health_battery_health,
                        )
                    },
                    level?.let {
                        Information(
                            "level",
                            InformationValue.IntValue(it),
                            R.string.health_battery_level,
                        )
                    },
                    scale?.let {
                        Information(
                            "scale",
                            InformationValue.IntValue(it),
                            R.string.health_battery_scale,
                        )
                    },
                    batteryLow?.let {
                        Information(
                            "battery_low",
                            InformationValue.BooleanValue(it),
                            R.string.health_battery_battery_low,
                        )
                    },
                    temperature?.let {
                        Information(
                            "temperature",
                            InformationValue.StringValue(
                                "$it",
                                R.string.health_battery_temperature_format,
                                arrayOf(it.toFloat() / 10),
                            ),
                            R.string.health_battery_temperature,
                        )
                    },
                    voltage?.let {
                        Information(
                            "voltage",
                            InformationValue.StringValue(
                                "$it",
                                R.string.health_battery_voltage_format,
                                arrayOf(it),
                            ),
                            R.string.health_battery_voltage,
                        )
                    },
                ),
                R.string.health_battery,
            )
        )
    }
}
