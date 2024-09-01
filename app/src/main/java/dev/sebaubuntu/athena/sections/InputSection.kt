/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.hardware.BatteryState
import android.hardware.Sensor
import android.hardware.input.InputManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.InputDevice
import androidx.annotation.RequiresApi
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import dev.sebaubuntu.athena.utils.InputDeviceUtils
import dev.sebaubuntu.athena.utils.LightsUtils
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

object InputSection : Section(
    "input",
    R.string.section_input_name,
    R.string.section_input_description,
    R.drawable.ic_trackpad_input,
) {
    private val LOG_TAG = this::class.simpleName!!

    @RequiresApi(Build.VERSION_CODES.S)
    private val batteryStatusToStringResId = mapOf(
        BatteryState.STATUS_UNKNOWN to R.string.battery_status_unknown,
        BatteryState.STATUS_CHARGING to R.string.battery_status_charging,
        BatteryState.STATUS_DISCHARGING to R.string.battery_status_discharging,
        BatteryState.STATUS_NOT_CHARGING to R.string.battery_status_not_charging,
        BatteryState.STATUS_FULL to R.string.battery_status_full,
    )

    override fun dataFlow(context: Context) = channelFlow {
        val inputManager = context.getSystemService(InputManager::class.java)

        val fixedData = listOfNotNull(
            Subsection(
                "general",
                listOfNotNull(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Information(
                            "maximum_obscuring_opacity_for_touch",
                            InformationValue.FloatValue(
                                inputManager.maximumObscuringOpacityForTouch
                            ),
                            R.string.input_maximum_obscuring_opacity_for_touch,
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        Information(
                            "is_stylus_pointer_icon_enabled",
                            InformationValue.BooleanValue(inputManager.isStylusPointerIconEnabled),
                            R.string.input_is_stylus_pointer_icon_enabled,
                        )
                    } else {
                        null
                    },
                ),
                R.string.input_general,
            )
        )

        inputManager.inputDevicesFlow(Handler(Looper.getMainLooper())).collectLatest {
            trySend(
                fixedData + it.map { inputDevice ->
                    formatInputDevice(inputDevice)
                }
            )
        }
    }

    private fun InputManager.inputDevicesFlow(handler: Handler) = callbackFlow {
        val inputDevices = mutableMapOf<Int, InputDevice>().apply {
            for (inputDeviceId in inputDeviceIds) {
                getInputDevice(inputDeviceId)?.also {
                    this[inputDeviceId] = it
                } ?: run {
                    Log.w(
                        LOG_TAG,
                        "Device $inputDeviceId got from getInputDeviceIds() doesn't exist"
                    )
                }
            }
        }

        val onInputDeviceUpdated = { deviceId: Int, removed: Boolean ->
            if (removed) {
                inputDevices.remove(deviceId)
            } else {
                getInputDevice(deviceId)?.also {
                    inputDevices[deviceId] = it
                } ?: run {
                    Log.w(LOG_TAG, "Device $deviceId doesn't exist, assuming removed")
                    inputDevices.remove(deviceId)
                }
            }

            trySend(inputDevices.values)
        }

        val listener = object : InputManager.InputDeviceListener {
            override fun onInputDeviceAdded(deviceId: Int) {
                onInputDeviceUpdated(deviceId, false)
            }

            override fun onInputDeviceRemoved(deviceId: Int) {
                onInputDeviceUpdated(deviceId, true)
            }

            override fun onInputDeviceChanged(deviceId: Int) {
                onInputDeviceUpdated(deviceId, false)
            }
        }

        trySend(inputDevices.values)

        registerInputDeviceListener(listener, handler)

        awaitClose {
            unregisterInputDeviceListener(listener)
        }
    }

    private fun formatInputDevice(inputDevice: InputDevice) = Subsection(
        "${inputDevice.id}",
        listOfNotNull(
            Information(
                "id",
                InformationValue.IntValue(inputDevice.id),
                R.string.input_device_id,
            ),
            Information(
                "controller_number",
                InformationValue.IntValue(inputDevice.controllerNumber),
                R.string.input_device_controller_number,
            ),
            Information(
                "vendor_id",
                InformationValue.IntValue(inputDevice.vendorId),
                R.string.input_device_vendor_id,
            ),
            Information(
                "product_id",
                InformationValue.IntValue(inputDevice.productId),
                R.string.input_device_product_id,
            ),
            Information(
                "descriptor",
                InformationValue.StringValue(inputDevice.descriptor),
                R.string.input_device_descriptor,
            ),
            Information(
                "is_virtual",
                InformationValue.BooleanValue(inputDevice.isVirtual),
                R.string.input_device_is_virtual,
            ),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Information(
                    "is_external",
                    InformationValue.BooleanValue(inputDevice.isExternal),
                    R.string.input_device_is_external,
                )
            } else {
                null
            },
            Information(
                "name",
                InformationValue.StringValue(inputDevice.name),
                R.string.input_device_name,
            ),
            Information(
                "source_classes",
                InformationValue.IntArrayValue(
                    inputDevice.sources.let { sources ->
                        InputDeviceUtils.sourceClassToStringResId.keys.filter {
                            (sources.and(it)) == it
                        }.toTypedArray()
                    },
                    InputDeviceUtils.sourceClassToStringResId,
                ),
                R.string.input_device_source_classes,
            ),
            Information(
                "sources",
                InformationValue.IntArrayValue(
                    inputDevice.sources.let { sources ->
                        InputDeviceUtils.sourceToStringResId.keys.filter {
                            (sources.and(it)) == it
                        }.toTypedArray()
                    },
                    InputDeviceUtils.sourceToStringResId,
                ),
                R.string.input_device_sources,
            ),
            Information(
                "keyboard_type",
                InformationValue.IntValue(
                    inputDevice.keyboardType,
                    InputDeviceUtils.keyboardTypeToStringResId,
                ),
                R.string.input_device_keyboard_type,
            ),
            Information(
                "key_character_map_keyboard_type",
                InformationValue.IntValue(
                    inputDevice.keyCharacterMap.keyboardType,
                    InputDeviceUtils.keyCharacterMapKeyboardTypeToStringResId,
                ),
                R.string.input_device_key_character_map_keyboard_type,
            ),
            Information(
                "motion_ranges_count",
                InformationValue.IntValue(inputDevice.motionRanges.size),
                R.string.input_device_motion_ranges_count,
            ),
            Information(
                "has_vibrator",
                InformationValue.BooleanValue(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        inputDevice.vibratorManager.vibratorIds.isNotEmpty()
                    } else {
                        @Suppress("DEPRECATION")
                        inputDevice.vibrator.hasVibrator()
                    }
                ),
                R.string.input_device_has_vibrator,
            ),
            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val batteryState = inputDevice.batteryState
                listOfNotNull(
                    Information(
                        "battery_is_present",
                        InformationValue.BooleanValue(batteryState.isPresent),
                        R.string.input_device_battery_is_present,
                    ),
                    *if (batteryState.isPresent) {
                        arrayOf(
                            Information(
                                "battery_status",
                                InformationValue.IntValue(
                                    batteryState.status,
                                    batteryStatusToStringResId,
                                ),
                                R.string.input_device_battery_status,
                            ),
                            Information(
                                "battery_capacity",
                                batteryState.capacity.takeUnless { it.isNaN() }?.let {
                                    InformationValue.IntValue((it * 100).toInt())
                                },
                                R.string.input_device_battery_capacity,
                            ),
                        )
                    } else {
                        arrayOf()
                    }
                ).toTypedArray()
            } else {
                arrayOf()
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Information(
                    "available_lights",
                    InformationValue.IntArrayValue(
                        inputDevice.lightsManager.lights.map {
                            it.type
                        }.toTypedArray(),
                        LightsUtils.lightTypeToStringResId,
                    ),
                    R.string.input_device_available_lights,
                )
            } else {
                null
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Information(
                    "sensor_manager",
                    InformationValue.StringArrayValue(
                        inputDevice.sensorManager.getSensorList(Sensor.TYPE_ALL).map {
                            "${it.name} (${it.stringType})"
                        }.toTypedArray()
                    ),
                    R.string.input_device_available_sensors,
                )
            } else {
                null
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                Information(
                    "is_enabled",
                    InformationValue.BooleanValue(inputDevice.isEnabled),
                    R.string.input_device_is_enabled,
                )
            } else {
                null
            },
            Information(
                "has_microphone",
                InformationValue.BooleanValue(inputDevice.hasMicrophone()),
                R.string.input_device_has_microphone,
            ),
        ),
        R.string.input_device_title,
        arrayOf(inputDevice.id),
    )
}
