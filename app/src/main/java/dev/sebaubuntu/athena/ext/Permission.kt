/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.models.Permission

fun Permission.getStringResId() = when (this) {
    Permission.BIOMETRICS -> R.string.permission_biometrics
    Permission.BLUETOOTH -> R.string.permission_bluetooth
    Permission.CAMERA -> R.string.permission_camera
    Permission.INTERNET -> R.string.permission_internet
    Permission.LOCATION -> R.string.permission_location
    Permission.NFC -> R.string.permission_nfc
    Permission.RIL -> R.string.permission_ril
    Permission.SENSORS -> R.string.permission_sensors
    Permission.UWB -> R.string.permission_uwb
    Permission.WIFI -> R.string.permission_wifi
}
