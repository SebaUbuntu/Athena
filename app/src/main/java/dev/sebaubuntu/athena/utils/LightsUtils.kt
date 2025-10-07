/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.hardware.lights.Light
import android.os.Build
import dev.sebaubuntu.athena.R

object LightsUtils {
    val lightTypeToStringResId = mutableMapOf(
        // System reserved, light HIDL and AIDL
        0 to R.string.light_type_backlight,
        1 to R.string.light_type_keyboard,
        2 to R.string.light_type_buttons,
        3 to R.string.light_type_battery,
        4 to R.string.light_type_notifications,
        5 to R.string.light_type_attention,
        6 to R.string.light_type_bluetooth,
        7 to R.string.light_type_wifi,

        // Hidden in Light class
        8 to R.string.light_type_microphone, // Light.LIGHT_TYPE_MICROPHONE
        9 to R.string.light_type_camera, // Light.LIGHT_TYPE_CAMERA
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this[Light.LIGHT_TYPE_INPUT] = R.string.light_type_input
            this[Light.LIGHT_TYPE_PLAYER_ID] = R.string.light_type_player_id
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this[Light.LIGHT_TYPE_KEYBOARD_BACKLIGHT] = R.string.light_type_keyboard_backlight
        }
    }.toMap()
}
