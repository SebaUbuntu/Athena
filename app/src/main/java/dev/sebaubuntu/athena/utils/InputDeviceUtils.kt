/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.view.InputDevice
import android.view.KeyCharacterMap
import androidx.core.view.InputDeviceCompat
import dev.sebaubuntu.athena.R

object InputDeviceUtils {
    val keyboardTypeToStringResId = mapOf(
        InputDevice.KEYBOARD_TYPE_NONE to R.string.input_device_keyboard_type_none,
        InputDevice.KEYBOARD_TYPE_NON_ALPHABETIC to
                R.string.input_device_keyboard_type_non_alphabetic,
        InputDevice.KEYBOARD_TYPE_ALPHABETIC to R.string.input_device_keyboard_type_alphabetic,
    )

    val keyCharacterMapKeyboardTypeToStringResId = mapOf(
        KeyCharacterMap.NUMERIC to R.string.key_character_map_keyboard_type_numeric,
        KeyCharacterMap.PREDICTIVE to R.string.key_character_map_keyboard_type_predictive,
        KeyCharacterMap.ALPHA to R.string.key_character_map_keyboard_type_alpha,
        KeyCharacterMap.FULL to R.string.key_character_map_keyboard_type_full,
        KeyCharacterMap.SPECIAL_FUNCTION to
                R.string.key_character_map_keyboard_type_special_function,
    )

    val sourceClassToStringResId = mapOf(
        InputDeviceCompat.SOURCE_CLASS_BUTTON to R.string.input_device_source_class_button,
        InputDeviceCompat.SOURCE_CLASS_POINTER to R.string.input_device_source_class_pointer,
        InputDeviceCompat.SOURCE_CLASS_TRACKBALL to R.string.input_device_source_class_trackball,
        InputDeviceCompat.SOURCE_CLASS_POSITION to R.string.input_device_source_class_position,
        InputDeviceCompat.SOURCE_CLASS_JOYSTICK to R.string.input_device_source_class_joystick,
    )

    val sourceToStringResId = mapOf(
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
