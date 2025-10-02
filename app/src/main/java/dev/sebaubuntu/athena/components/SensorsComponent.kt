/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
import android.hardware.SensorManager
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
import dev.sebaubuntu.athena.ext.sensorsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class SensorsComponent(context: Context) : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = SensorsComponent(context)
    }

    private val sensorManager = context.getSystemService(SensorManager::class.java)

    override val name = "sensors"

    override val title = LocalizedString(R.string.section_sensors_name)

    override val description = LocalizedString(R.string.section_sensors_description)

    override val drawableResId = R.drawable.ic_sensors

    override val permissions = setOf(Permission.SENSORS)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = title,
                elements = listOfNotNull(
                    Element.Card(
                        identifier = identifier / "general",
                        title = LocalizedString(R.string.section_sensors_name),
                        elements = listOfNotNull(
                            Element.Item(
                                identifier = identifier / "sensors",
                                title = LocalizedString(R.string.sensors),
                                isNavigable = true,
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Element.Item(
                                    identifier = identifier / "general" / "is_dynamic_sensor_discovery_supported",
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
                    elements = sensors.map { sensor ->
                        Element.Item(
                            identifier = identifier / sensor.name,
                            title = LocalizedString(sensor.name),
                            value = Value(sensor.stringType),
                        )
                    }
                )

                Result.Success<Resource, Error>(screen)
            }

            else -> when (identifier.path.getOrNull(2)) {
                null -> TODO()

                else -> flowOf(Result.Error(Error.NOT_FOUND))
            }
        }

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }
}
