/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

/**
 * @see SensorManager.getDynamicSensorList
 */
@RequiresApi(Build.VERSION_CODES.N)
fun SensorManager.dynamicSensorsFlow() = callbackFlow {
    val onDynamicSensorUpdated = {
        trySend(getDynamicSensorList(Sensor.TYPE_ALL))
    }

    val callback = object : SensorManager.DynamicSensorCallback() {
        override fun onDynamicSensorConnected(sensor: Sensor?) {
            super.onDynamicSensorConnected(sensor)

            onDynamicSensorUpdated()
        }

        override fun onDynamicSensorDisconnected(sensor: Sensor?) {
            super.onDynamicSensorDisconnected(sensor)

            onDynamicSensorUpdated()
        }
    }

    registerDynamicSensorCallback(callback)

    onDynamicSensorUpdated()

    awaitClose {
        unregisterDynamicSensorCallback(callback)
    }
}

fun SensorManager.sensorsFlow() = channelFlow {
    val onSensorUpdated = {
        val sensors = buildList<Sensor> {
            addAll(getSensorList(Sensor.TYPE_ALL))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                addAll(getDynamicSensorList(Sensor.TYPE_ALL))
            }
        }

        trySend(sensors)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        launch {
            dynamicSensorsFlow().collectLatest {
                onSensorUpdated()
            }
        }
    }

    onSensorUpdated()
}

fun SensorManager.sensorFlow(
    id: Int,
    name: String,
    type: Int,
) = sensorsFlow()
    .map { sensors ->
        sensors.firstOrNull {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                when (it.id) {
                    -1 -> it.name == name && it.type == type
                    0 -> {
                        // "Return value of 0 means this sensor does not support this function"
                        // The fuck are we supposed to do?
                        it.name == name && it.type == type
                    }
                    else -> it.id == id
                }
            } else {
                it.name == name && it.type == type
            }
        }
    }
