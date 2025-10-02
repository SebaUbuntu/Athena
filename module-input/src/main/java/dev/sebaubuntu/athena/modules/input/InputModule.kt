/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.input

import android.content.Context
import android.hardware.BatteryState
import android.hardware.Sensor
import android.hardware.input.InputManager
import android.os.Build
import android.view.InputDevice
import android.view.KeyCharacterMap
import androidx.annotation.RequiresApi
import androidx.core.view.InputDeviceCompat
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.modules.input.ext.inputDeviceFlow
import dev.sebaubuntu.athena.modules.input.ext.inputDevicesFlow
import dev.sebaubuntu.athena.modules.lights.utils.LightsUtils
import dev.sebaubuntu.athena.modules.sensors.models.SensorType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class InputModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = InputModule(context)
    }

    private val inputManager = context.getSystemService(InputManager::class.java)

    override val id = "input"

    override val name = LocalizedString(R.string.section_input_name)

    override val description = LocalizedString(R.string.section_input_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_trackpad_input

    override val requiredPermissions = arrayOf<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOf(
                    Element.Card(
                        name = "general",
                        title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                        elements = listOfNotNull(
                            Element.Item(
                                name = "devices",
                                title = LocalizedString(R.string.input_devices),
                                navigateTo = identifier / "devices",
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                Element.Item(
                                    name = "maximum_obscuring_opacity_for_touch",
                                    title = LocalizedString(R.string.input_maximum_obscuring_opacity_for_touch),
                                    value = Value(inputManager.maximumObscuringOpacityForTouch),
                                )
                            } else {
                                null
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                Element.Item(
                                    name = "is_stylus_pointer_icon_enabled",
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
                            name = "${it.id}",
                            title = LocalizedString(
                                R.string.input_device_title,
                                it.id,
                            ),
                            navigateTo = identifier / "${it.id}",
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
                name = "general",
                title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                elements = listOfNotNull(
                    Element.Item(
                        name = "id",
                        title = LocalizedString(R.string.input_device_id),
                        value = Value(id),
                    ),
                    Element.Item(
                        name = "controller_number",
                        title = LocalizedString(R.string.input_device_controller_number),
                        value = Value(controllerNumber),
                    ),
                    Element.Item(
                        name = "vendor_id",
                        title = LocalizedString(R.string.input_device_vendor_id),
                        value = Value(vendorId),
                    ),
                    Element.Item(
                        name = "product_id",
                        title = LocalizedString(R.string.input_device_product_id),
                        value = Value(productId),
                    ),
                    Element.Item(
                        name = "descriptor",
                        title = LocalizedString(R.string.input_device_descriptor),
                        value = Value(descriptor),
                    ),
                    Element.Item(
                        name = "is_virtual",
                        title = LocalizedString(R.string.input_device_is_virtual),
                        value = Value(isVirtual),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Element.Item(
                            name = "is_external",
                            title = LocalizedString(R.string.input_device_is_external),
                            value = Value(isExternal),
                        )
                    } else {
                        null
                    },
                    Element.Item(
                        name = "name",
                        title = LocalizedString(R.string.input_device_name),
                        value = Value(name),
                    ),
                    Element.Item(
                        name = "source_classes",
                        title = LocalizedString(R.string.input_device_source_classes),
                        value = Value(
                            sources.let { sources ->
                                sourceClassToStringResId.keys.filter {
                                    (sources.and(it)) == it
                                }.toTypedArray()
                            },
                            sourceClassToStringResId,
                        ),
                    ),
                    Element.Item(
                        name = "sources",
                        title = LocalizedString(R.string.input_device_sources),
                        value = Value(
                            sources.let { sources ->
                                sourceToStringResId.keys.filter {
                                    (sources.and(it)) == it
                                }.toTypedArray()
                            },
                            sourceToStringResId,
                        ),
                    ),
                    Element.Item(
                        name = "keyboard_type",
                        title = LocalizedString(R.string.input_device_keyboard_type),
                        value = Value(
                            keyboardType,
                            keyboardTypeToStringResId,
                        ),
                    ),
                    Element.Item(
                        name = "key_character_map_keyboard_type",
                        title = LocalizedString(R.string.input_device_key_character_map_keyboard_type),
                        value = Value(
                            keyCharacterMap.keyboardType,
                            keyCharacterMapKeyboardTypeToStringResId,
                        ),
                    ),
                    Element.Item(
                        name = "motion_ranges_count",
                        title = LocalizedString(R.string.input_device_motion_ranges_count),
                        value = Value(motionRanges.size),
                    ),
                    Element.Item(
                        name = "has_vibrator",
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
                            name = "is_enabled",
                            title = LocalizedString(R.string.input_device_is_enabled),
                            value = Value(isEnabled),
                        )
                    } else {
                        null
                    },
                    Element.Item(
                        name = "has_microphone",
                        title = LocalizedString(R.string.input_device_has_microphone),
                        value = Value(hasMicrophone()),
                    ),
                ),
            ),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                batteryState.takeIf { it.isPresent }?.let { batteryState ->
                    Element.Card(
                        name = "battery",
                        title = LocalizedString(R.string.input_device_battery),
                        elements = listOf(
                            Element.Item(
                                name = "status",
                                title = LocalizedString(R.string.input_device_battery_status),
                                value = Value(
                                    batteryState.status,
                                    batteryStatusToStringResId,
                                ),
                            ),
                            Element.Item(
                                name = "capacity",
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
                            name = "light_${light.id}",
                            title = LocalizedString(
                                dev.sebaubuntu.athena.modules.lights.R.string.light,
                                light.id,
                            ),
                            elements = listOf(
                                Element.Item(
                                    name = "name",
                                    title = LocalizedString(dev.sebaubuntu.athena.modules.lights.R.string.light_name),
                                    value = Value(light.name),
                                ),
                                Element.Item(
                                    name = "ordinal",
                                    title = LocalizedString(dev.sebaubuntu.athena.modules.lights.R.string.light_ordinal),
                                    value = Value(light.ordinal),
                                ),
                                Element.Item(
                                    name = "type",
                                    title = LocalizedString(dev.sebaubuntu.athena.modules.lights.R.string.light_type),
                                    value = Value(
                                        light.type,
                                        LightsUtils.lightTypeToStringResId,
                                    ),
                                ),
                                Element.Item(
                                    name = "has_brightness_control",
                                    title = LocalizedString(dev.sebaubuntu.athena.modules.lights.R.string.light_has_brightness_control),
                                    value = Value(light.hasBrightnessControl()),
                                ),
                                Element.Item(
                                    name = "has_rgb_control",
                                    title = LocalizedString(dev.sebaubuntu.athena.modules.lights.R.string.light_has_rgb_control),
                                    value = Value(light.hasRgbControl()),
                                ),
                                Element.Item(
                                    name = "state",
                                    title = LocalizedString(dev.sebaubuntu.athena.modules.lights.R.string.light_state),
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
                        name = "sensor_${sensor.id}",
                        title = LocalizedString(
                            R.string.input_device_sensor,
                            sensor.id,
                        ),
                        elements = listOf(
                            Element.Item(
                                name = "name",
                                title = LocalizedString(dev.sebaubuntu.athena.modules.sensors.R.string.sensor_name),
                                value = Value(sensor.name),
                            ),
                            Element.Item(
                                name = "vendor",
                                title = LocalizedString(dev.sebaubuntu.athena.modules.sensors.R.string.sensor_vendor),
                                value = Value(sensor.vendor),
                            ),
                            Element.Item(
                                name = "type",
                                title = LocalizedString(dev.sebaubuntu.athena.modules.sensors.R.string.sensor_type),
                                value = Value(
                                    sensor.type,
                                    SensorType.sensorTypeToStringResId,
                                ),
                            ),
                            Element.Item(
                                name = "version",
                                title = LocalizedString(dev.sebaubuntu.athena.modules.sensors.R.string.sensor_version),
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
            BatteryState.STATUS_UNKNOWN to dev.sebaubuntu.athena.modules.health.R.string.battery_status_unknown,
            BatteryState.STATUS_CHARGING to dev.sebaubuntu.athena.modules.health.R.string.battery_status_charging,
            BatteryState.STATUS_DISCHARGING to dev.sebaubuntu.athena.modules.health.R.string.battery_status_discharging,
            BatteryState.STATUS_NOT_CHARGING to dev.sebaubuntu.athena.modules.health.R.string.battery_status_not_charging,
            BatteryState.STATUS_FULL to dev.sebaubuntu.athena.modules.health.R.string.battery_status_full,
        )

        private val keyboardTypeToStringResId = mapOf(
            InputDevice.KEYBOARD_TYPE_NONE to R.string.input_device_keyboard_type_none,
            InputDevice.KEYBOARD_TYPE_NON_ALPHABETIC to
                    R.string.input_device_keyboard_type_non_alphabetic,
            InputDevice.KEYBOARD_TYPE_ALPHABETIC to R.string.input_device_keyboard_type_alphabetic,
        )

        private val keyCharacterMapKeyboardTypeToStringResId = mapOf(
            KeyCharacterMap.NUMERIC to R.string.key_character_map_keyboard_type_numeric,
            KeyCharacterMap.PREDICTIVE to R.string.key_character_map_keyboard_type_predictive,
            KeyCharacterMap.ALPHA to R.string.key_character_map_keyboard_type_alpha,
            KeyCharacterMap.FULL to R.string.key_character_map_keyboard_type_full,
            KeyCharacterMap.SPECIAL_FUNCTION to
                    R.string.key_character_map_keyboard_type_special_function,
        )

        private val sourceClassToStringResId = mapOf(
            InputDeviceCompat.SOURCE_CLASS_BUTTON to R.string.input_device_source_class_button,
            InputDeviceCompat.SOURCE_CLASS_POINTER to R.string.input_device_source_class_pointer,
            InputDeviceCompat.SOURCE_CLASS_TRACKBALL to R.string.input_device_source_class_trackball,
            InputDeviceCompat.SOURCE_CLASS_POSITION to R.string.input_device_source_class_position,
            InputDeviceCompat.SOURCE_CLASS_JOYSTICK to R.string.input_device_source_class_joystick,
        )

        private val sourceToStringResId = mapOf(
            InputDeviceCompat.SOURCE_KEYBOARD to R.string.input_device_source_keyboard,
            InputDeviceCompat.SOURCE_DPAD to R.string.input_device_source_dpad,
            InputDeviceCompat.SOURCE_GAMEPAD to R.string.input_device_source_gamepad,
            InputDeviceCompat.SOURCE_TOUCHSCREEN to R.string.input_device_source_touchscreen,
            InputDeviceCompat.SOURCE_MOUSE to R.string.input_device_source_mouse,
            InputDeviceCompat.SOURCE_STYLUS to R.string.input_device_source_stylus,
            InputDeviceCompat.SOURCE_TRACKBALL to R.string.input_device_source_trackball,
            InputDeviceCompat.SOURCE_TOUCHPAD to R.string.input_device_source_touchpad,
            InputDeviceCompat.SOURCE_TOUCH_NAVIGATION to R.string.input_device_source_touch_navigation,
            InputDeviceCompat.SOURCE_ROTARY_ENCODER to R.string.input_device_source_rotary_encoder,
            InputDeviceCompat.SOURCE_JOYSTICK to R.string.input_device_source_joystick,
            InputDeviceCompat.SOURCE_HDMI to R.string.input_device_source_hdmi,
        )
    }
}
