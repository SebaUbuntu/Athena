/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.sensors

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorDirectChannel
import android.hardware.SensorManager
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
import dev.sebaubuntu.athena.modules.sensors.ext.sensorFlow
import dev.sebaubuntu.athena.modules.sensors.ext.sensorType
import dev.sebaubuntu.athena.modules.sensors.ext.sensorsFlow
import dev.sebaubuntu.athena.modules.sensors.ext.uniqueId
import dev.sebaubuntu.athena.modules.sensors.models.SensorType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class SensorsModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = SensorsModule(context)
    }

    private val sensorManager = context.getSystemService(SensorManager::class.java)

    override val id = "sensors"

    override val name = LocalizedString(R.string.section_sensors_name)

    override val description = LocalizedString(R.string.section_sensors_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_sensors

    override val requiredPermissions = buildList {
        //add(Manifest.permission.BODY_SENSORS) TODO

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.HIGH_SAMPLING_RATE_SENSORS)
        }
    }.toTypedArray()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOfNotNull(
                    Element.Card(
                        name = "general",
                        title = LocalizedString(R.string.section_sensors_name),
                        elements = listOfNotNull(
                            Element.Item(
                                name = "sensors",
                                title = LocalizedString(R.string.sensors),
                                navigateTo = identifier / "sensors",
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Element.Item(
                                    name = "is_dynamic_sensor_discovery_supported",
                                    title = LocalizedString(R.string.sensors_is_dynamic_sensor_discovery_supported),
                                    value = Value(sensorManager.isDynamicSensorDiscoverySupported),
                                )
                            } else {
                                null
                            }
                        )
                    )
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        "sensors" -> when (identifier.path.getOrNull(1)) {
            null -> sensorManager.sensorsFlow().mapLatest { sensors ->
                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.sensors),
                    elements = sensors.sortedWith(
                        compareBy(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Sensor::getId
                            } else {
                                { 0 }
                            },
                            Sensor::getType,
                            Sensor::getName,
                        )
                    ).map { sensor ->
                        Element.Item(
                            name = "${sensor.uniqueId}",
                            title = LocalizedString(sensor.name),
                            navigateTo = identifier / "${sensor.uniqueId}",
                            drawableResId = sensor.sensorType?.drawableResId
                                ?: dev.sebaubuntu.athena.core.R.drawable.ic_sensors,
                            value = Value(sensor.stringType),
                        )
                    }
                )

                Result.Success<Resource, Error>(screen)
            }

            else -> when (identifier.path.getOrNull(2)) {
                null -> identifier.path[1].toIntOrNull()?.let { uniqueId ->
                    sensorManager.sensorFlow(uniqueId).mapLatest { sensor ->
                        val screen = sensor?.getScreen(
                            identifier = identifier,
                        )

                        screen?.let {
                            Result.Success<Resource, Error>(it)
                        } ?: Result.Error(Error.NOT_FOUND)
                    }
                } ?: flowOf(Result.Error(Error.NOT_FOUND))

                else -> flowOf(Result.Error(Error.NOT_FOUND))
            }
        }

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }

    private fun Sensor.getScreen(
        identifier: Resource.Identifier,
    ) = Screen.CardListScreen(
        identifier = identifier,
        title = LocalizedString(name),
        elements = listOf(
            Element.Card(
                name = "general",
                title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                elements = listOfNotNull(
                    Element.Item(
                        name = "name",
                        title = LocalizedString(R.string.sensor_name),
                        value = Value(name),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        id.takeIf { it > 0 }?.let {
                            Element.Item(
                                name = "id",
                                title = LocalizedString(R.string.sensor_id),
                                value = Value(it),
                            )
                        }
                    } else {
                        null
                    },
                    Element.Item(
                        name = "string_type",
                        title = LocalizedString(R.string.sensor_string_type),
                        value = Value(stringType),
                    ),
                    Element.Item(
                        name = "type",
                        title = LocalizedString(R.string.sensor_type),
                        value = Value(
                            type,
                            SensorType.sensorTypeToStringResId,
                        ),
                    ),
                    Element.Item(
                        name = "vendor",
                        title = LocalizedString(R.string.sensor_vendor),
                        value = Value(vendor),
                    ),
                    Element.Item(
                        name = "version",
                        title = LocalizedString(R.string.sensor_version),
                        value = Value(version),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Element.Item(
                            name = "is_dynamic_sensor",
                            title = LocalizedString(R.string.sensor_is_dynamic_sensor),
                            value = Value(isDynamicSensor),
                        )
                    } else {
                        null
                    },
                    Element.Item(
                        name = "is_wake_up_sensor",
                        title = LocalizedString(R.string.sensor_is_wakeup_sensor),
                        value = Value(isWakeUpSensor),
                    ),
                    Element.Item(
                        name = "fifo_max_event_count",
                        title = LocalizedString(R.string.sensor_fifo_max_event_count),
                        value = Value(fifoMaxEventCount),
                    ),
                    Element.Item(
                        name = "fifo_reserved_event_count",
                        title = LocalizedString(R.string.sensor_fifo_reserved_event_count),
                        value = Value(fifoReservedEventCount),
                    ),
                    Element.Item(
                        name = "min_delay",
                        title = LocalizedString(R.string.sensor_min_delay),
                        value = Value(minDelay),
                    ),
                    Element.Item(
                        name = "max_delay",
                        title = LocalizedString(R.string.sensor_max_delay),
                        value = Value(maxDelay),
                    ),
                    Element.Item(
                        name = "maximum_range",
                        title = LocalizedString(R.string.sensor_maximum_range),
                        value = Value(maximumRange),
                    ),
                    Element.Item(
                        name = "power",
                        title = LocalizedString(R.string.sensor_power),
                        value = Value(power),
                    ),
                    Element.Item(
                        name = "reporting_mode",
                        title = LocalizedString(R.string.sensor_reporting_mode),
                        value = Value(
                            reportingMode,
                            sensorReportingModeToStringResId,
                        ),
                    ),
                    Element.Item(
                        name = "resolution",
                        title = LocalizedString(R.string.sensor_resolution),
                        value = Value(resolution),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Element.Item(
                            name = "is_additional_info_supported",
                            title = LocalizedString(R.string.sensor_is_additional_info_supported),
                            value = Value(isAdditionalInfoSupported),
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Element.Item(
                            name = "highest_direct_report_rate_level",
                            title = LocalizedString(R.string.sensor_highest_direct_report_rate_level),
                            value = Value(
                                highestDirectReportRateLevel,
                                sensorDirectReportModeRatesToStringResId,
                            ),
                        )
                    } else {
                        null
                    },
                ),
            ),
        ),
    )

    companion object {
        private val sensorReportingModeToStringResId = mapOf(
            Sensor.REPORTING_MODE_CONTINUOUS to R.string.sensor_reporting_mode_continuous,
            Sensor.REPORTING_MODE_ON_CHANGE to R.string.sensor_reporting_mode_on_change,
            Sensor.REPORTING_MODE_ONE_SHOT to R.string.sensor_reporting_mode_one_shot,
            Sensor.REPORTING_MODE_SPECIAL_TRIGGER to R.string.sensor_reporting_mode_special_trigger,
        )

        @RequiresApi(Build.VERSION_CODES.O)
        private val sensorDirectReportModeRatesToStringResId = mapOf(
            SensorDirectChannel.RATE_STOP to R.string.sensor_direct_report_mode_rate_stop,
            SensorDirectChannel.RATE_NORMAL to R.string.sensor_direct_report_mode_rate_normal,
            SensorDirectChannel.RATE_FAST to R.string.sensor_direct_report_mode_rate_fast,
            SensorDirectChannel.RATE_VERY_FAST to R.string.sensor_direct_report_mode_rate_very_fast,
        )
    }
}
