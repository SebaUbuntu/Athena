/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import dev.sebaubuntu.athena.ext.applicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class SensorsViewModel(application: Application) : AndroidViewModel(application) {
    private val sensorManager by lazy { applicationContext.getSystemService(SensorManager::class.java) }

    val sensors = callbackFlow {
        val getSensors = {
            listOf(
                sensorManager.getSensorList(Sensor.TYPE_ALL),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sensorManager.getDynamicSensorList(Sensor.TYPE_ALL)
                } else {
                    listOf()
                }
            ).flatten().sortedBy { it.type }
        }

        val callback =
            @RequiresApi(Build.VERSION_CODES.N) object : SensorManager.DynamicSensorCallback() {
                override fun onDynamicSensorConnected(sensor: Sensor?) {
                    super.onDynamicSensorConnected(sensor)

                    trySend(getSensors())
                }

                override fun onDynamicSensorDisconnected(sensor: Sensor?) {
                    super.onDynamicSensorDisconnected(sensor)

                    trySend(getSensors())
                }
            }

        trySend(getSensors())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sensorManager.registerDynamicSensorCallback(callback)
        }

        awaitClose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sensorManager.unregisterDynamicSensorCallback(callback)
            }
        }
    }
}
