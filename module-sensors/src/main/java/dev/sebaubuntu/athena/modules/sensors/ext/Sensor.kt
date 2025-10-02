/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.sensors.ext

import android.hardware.Sensor
import android.os.Build
import dev.sebaubuntu.athena.core.ext.hashCodeOf
import dev.sebaubuntu.athena.modules.sensors.models.SensorType

val Sensor.uniqueId: Int
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        when (id) {
            -1 -> hashCodeOf(Sensor::getType, Sensor::getName)

            0 -> {
                // "Return value of 0 means this sensor does not support this function"
                // The fuck are we supposed to do?
                hashCodeOf(Sensor::getType, Sensor::getName)
            }

            else -> id
        }
    } else {
        hashCodeOf(Sensor::getType, Sensor::getName)
    }

val Sensor.sensorType
    get() = SensorType.fromType(type)
