/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import android.hardware.Sensor
import dev.sebaubuntu.athena.models.sensors.SensorType

val Sensor.sensorType
    get() = SensorType.fromType(type)
