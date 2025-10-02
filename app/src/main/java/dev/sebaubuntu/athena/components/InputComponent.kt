/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
import android.hardware.BatteryState
import android.hardware.Sensor
import android.hardware.input.InputManager
import android.os.Build
import android.view.InputDevice
import androidx.annotation.RequiresApi
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
import dev.sebaubuntu.athena.ext.inputDeviceFlow
import dev.sebaubuntu.athena.ext.inputDevicesFlow
import dev.sebaubuntu.athena.models.sensors.SensorType
import dev.sebaubuntu.athena.utils.InputDeviceUtils
import dev.sebaubuntu.athena.utils.LightsUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class InputComponent(context: Context) : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = InputComponent(context)
    }

    private val inputManager = context.getSystemService(InputManager::class.java)

    override val name = "input"

    override val title = LocalizedString(R.string.section_input_name)

    override val description = LocalizedString(R.string.section_input_description)

    override val drawableResId = R.drawable.ic_trackpad_input

    override val permissions = setOf<Permission>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = title,
                elements = listOf(
                    Element.Card(
                        identifier = identifier / "general",
                        title = LocalizedString(R.string.input_general),
                        elements = listOfNotNull(
                            Element.Item(
                                identifier = identifier / "devices",
                                title = LocalizedString(R.string.input_devices),
                                isNavigable = true,
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                Element.Item(
                                    identifier = identifier / "general" / "maximum_obscuring_opacity_for_touch",
                                    title = LocalizedString(R.string.input_maximum_obscuring_opacity_for_touch),
                                    value = Value(inputManager.maximumObscuringOpacityForTouch),
                                )
                            } else {
                                null
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                Element.Item(
                                    identifier = identifier / "general" / "is_stylus_pointer_icon_enabled",
                                    title = LocalizedString(R.string.input_is_stylus_pointer_icon_enabled),
                                    value = Value(inputManager.isStylusPointerIconEnabled),
                                )
                            } else {
                                null
                            },
                        ),
                    ),
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        "devices" -> when (identifier.path.getOrNull(1)) {
            null -> inputManager.inputDevicesFlow().mapLatest { inputDevices ->
                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.input_devices),
                    elements = inputDevices.map {
                        Element.Item(
                            identifier = identifier / it.id.toString(),
                            title = LocalizedString(
                                R.string.input_device_title,
                                it.id,
                            ),
                            isNavigable = true,
                            value = Value(it.name),
                        )
                    },
                )

                Result.Success<Resource, Error>(screen)
            }

            else -> when (identifier.path.getOrNull(2)) {
                null -> identifier.path[1].toIntOrNull()?.let { deviceId ->
                    inputManager.inputDeviceFlow(deviceId).mapLatest { inputDevice ->
                        val screen = inputDevice?.getScreen(
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

    private fun InputDevice.getScreen(
        identifier: Resource.Identifier,
    ) = Screen.CardListScreen(
        identifier = identifier,
        title = LocalizedString(R.string.input_device_title, id.toString()),
        elements = listOfNotNull(
            Element.Card(
                identifier = identifier / "general",
                title = LocalizedString(R.string.input_device_general),
                elements = listOfNotNull(
                    Element.Item(
                        identifier = identifier / "general" / "id",
                        title = LocalizedString(R.string.input_device_id),
                        value = Value(id),
                    ),
                    Element.Item(
                        identifier = identifier / "general" / "controller_number",
                        title = LocalizedString(R.string.input_device_controller_number),
                        value = Value(controllerNumber),
                    ),
                    Element.Item(
                        identifier = identifier / "general" / "vendor_id",
                        title = LocalizedString(R.string.input_device_vendor_id),
                        value = Value(vendorId),
                    ),
                    Element.Item(
                        identifier = identifier / "general" / "product_id",
                        title = LocalizedString(R.string.input_device_product_id),
                        value = Value(productId),
                    ),
                    Element.Item(
                        identifier = identifier / "general" / "descriptor",
                        title = LocalizedString(R.string.input_device_descriptor),
                        value = Value(descriptor),
                    ),
                    Element.Item(
                        identifier = identifier / "general" / "is_virtual",
                        title = LocalizedString(R.string.input_device_is_virtual),
                        value = Value(isVirtual),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Element.Item(
                            identifier = identifier / "general" / "is_external",
                            title = LocalizedString(R.string.input_device_is_external),
                            value = Value(isExternal),
                        )
                    } else {
                        null
                    },
                    Element.Item(
                        identifier = identifier / "general" / "name",
                        title = LocalizedString(R.string.input_device_name),
                        value = Value(name),
                    ),
                    Element.Item(
                        identifier = identifier / "general" / "source_classes",
                        title = LocalizedString(R.string.input_device_source_classes),
                        value = Value(
                            sources.let { sources ->
                                InputDeviceUtils.sourceClassToStringResId.keys.filter {
                                    (sources.and(it)) == it
                                }.toTypedArray()
                            },
                            InputDeviceUtils.sourceClassToStringResId,
                        ),
                    ),
                    Element.Item(
                        identifier = identifier / "general" / "sources",
                        title = LocalizedString(R.string.input_device_sources),
                        value = Value(
                            sources.let { sources ->
                                InputDeviceUtils.sourceToStringResId.keys.filter {
                                    (sources.and(it)) == it
                                }.toTypedArray()
                            },
                            InputDeviceUtils.sourceToStringResId,
                        ),
                    ),
                    Element.Item(
                        identifier = identifier / "general" / "keyboard_type",
                        title = LocalizedString(R.string.input_device_keyboard_type),
                        value = Value(
                            keyboardType,
                            InputDeviceUtils.keyboardTypeToStringResId,
                        ),
                    ),
                    Element.Item(
                        identifier = identifier / "general" / "key_character_map_keyboard_type",
                        title = LocalizedString(R.string.input_device_key_character_map_keyboard_type),
                        value = Value(
                            keyCharacterMap.keyboardType,
                            InputDeviceUtils.keyCharacterMapKeyboardTypeToStringResId,
                        ),
                    ),
                    Element.Item(
                        identifier = identifier / "general" / "motion_ranges_count",
                        title = LocalizedString(R.string.input_device_motion_ranges_count),
                        value = Value(motionRanges.size),
                    ),
                    Element.Item(
                        identifier = identifier / "general" / "has_vibrator",
                        title = LocalizedString(R.string.input_device_has_vibrator),
                        value = Value(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                vibratorManager.vibratorIds.isNotEmpty()
                            } else {
                                @Suppress("DEPRECATION")
                                vibrator.hasVibrator()
                            }
                        ),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        Element.Item(
                            identifier = identifier / "general" / "is_enabled",
                            title = LocalizedString(R.string.input_device_is_enabled),
                            value = Value(isEnabled),
                        )
                    } else {
                        null
                    },
                    Element.Item(
                        identifier = identifier / "general" / "has_microphone",
                        title = LocalizedString(R.string.input_device_has_microphone),
                        value = Value(hasMicrophone()),
                    ),
                ),
            ),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                batteryState.takeIf { it.isPresent }?.let { batteryState ->
                    Element.Card(
                        identifier = identifier / "battery",
                        title = LocalizedString(R.string.input_device_battery),
                        elements = listOf(
                            Element.Item(
                                identifier = identifier / "battery" / "status",
                                title = LocalizedString(R.string.input_device_battery_status),
                                value = Value(
                                    batteryState.status,
                                    batteryStatusToStringResId,
                                ),
                            ),
                            Element.Item(
                                identifier = identifier / "battery" / "capacity",
                                title = LocalizedString(R.string.input_device_battery_capacity),
                                value = batteryState.capacity.takeUnless { it.isNaN() }?.let {
                                    Value((it * 100).toInt())
                                },
                            ),
                        ),
                    )
                }
            } else {
                null
            },
            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                lightsManager.let { lightsManager ->
                    lightsManager.lights.map { light ->
                        Element.Card(
                            identifier = identifier / "light_${light.id}",
                            title = LocalizedString(
                                R.string.input_device_light,
                                light.id,
                            ),
                            elements = listOf(
                                Element.Item(
                                    identifier = identifier / "light_${light.id}" / "name",
                                    title = LocalizedString(R.string.input_device_light_name),
                                    value = Value(light.name),
                                ),
                                Element.Item(
                                    identifier = identifier / "light_${light.id}" / "ordinal",
                                    title = LocalizedString(R.string.input_device_light_ordinal),
                                    value = Value(light.ordinal),
                                ),
                                Element.Item(
                                    identifier = identifier / "light_${light.id}" / "type",
                                    title = LocalizedString(R.string.input_device_light_type),
                                    value = Value(
                                        light.type,
                                        LightsUtils.lightTypeToStringResId,
                                    ),
                                ),
                                Element.Item(
                                    identifier = identifier / "light_${light.id}" / "has_brightness_control",
                                    title = LocalizedString(R.string.input_device_light_has_brightness_control),
                                    value = Value(light.hasBrightnessControl()),
                                ),
                                Element.Item(
                                    identifier = identifier / "light_${light.id}" / "has_rgb_control",
                                    title = LocalizedString(R.string.input_device_light_has_rgb_control),
                                    value = Value(light.hasRgbControl()),
                                ),
                                Element.Item(
                                    identifier = identifier / "light_${light.id}" / "state",
                                    title = LocalizedString(R.string.input_device_light_state),
                                    value = Value(lightsManager.getLightState(light).toString()),
                                ),
                            ),
                        )
                    }.toTypedArray()
                }
            } else {
                arrayOf()
            },
            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                sensorManager.getSensorList(Sensor.TYPE_ALL).map { sensor ->
                    Element.Card(
                        identifier = identifier / "sensor_${sensor.id}",
                        title = LocalizedString(
                            R.string.input_device_sensor,
                            sensor.id,
                        ),
                        elements = listOf(
                            Element.Item(
                                identifier = identifier / "sensor_${sensor.id}" / "name",
                                title = LocalizedString(R.string.sensor_name),
                                value = Value(sensor.name),
                            ),
                            Element.Item(
                                identifier = identifier / "sensor_${sensor.id}" / "vendor",
                                title = LocalizedString(R.string.sensor_vendor),
                                value = Value(sensor.vendor),
                            ),
                            Element.Item(
                                identifier = identifier / "sensor_${sensor.id}" / "type",
                                title = LocalizedString(R.string.sensor_type),
                                value = Value(
                                    sensor.type,
                                    SensorType.sensorTypeToStringResId,
                                ),
                            ),
                            Element.Item(
                                identifier = identifier / "sensor_${sensor.id}" / "version",
                                title = LocalizedString(R.string.sensor_version),
                                value = Value(sensor.version),
                            ),
                        ),
                    )
                }.toTypedArray()
            } else {
                arrayOf()
            }
        ),
    )

    companion object {
        @RequiresApi(Build.VERSION_CODES.S)
        private val batteryStatusToStringResId = mapOf(
            BatteryState.STATUS_UNKNOWN to R.string.battery_status_unknown,
            BatteryState.STATUS_CHARGING to R.string.battery_status_charging,
            BatteryState.STATUS_DISCHARGING to R.string.battery_status_discharging,
            BatteryState.STATUS_NOT_CHARGING to R.string.battery_status_not_charging,
            BatteryState.STATUS_FULL to R.string.battery_status_full,
        )
    }
}
