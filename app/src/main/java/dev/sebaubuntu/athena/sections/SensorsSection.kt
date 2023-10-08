/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import dev.sebaubuntu.athena.R

object SensorsSection : Section() {
    override val name = R.string.section_sensors_name
    override val description = R.string.section_sensors_description
    override val icon = R.drawable.ic_sensors
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context): Map<String, Map<String, String>> {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

        return sensors.associate { it.name to getSensorInfo(it) }
    }

    private fun getSensorInfo(sensor: Sensor): Map<String, String> = mutableMapOf(
        "Name" to sensor.name,
        "Is wake-up sensor" to sensor.isWakeUpSensor.toString(),
        "FIFO max event count" to sensor.fifoMaxEventCount.toString(),
        "FIFO reserved event count" to sensor.fifoReservedEventCount.toString(),
        "Max delay" to sensor.maxDelay.toString(),
        "Maximum range" to sensor.maximumRange.toString(),
        "Min delay" to sensor.minDelay.toString(),
        "Power" to sensor.power.toString(),
        "Reporting mode" to sensor.reportingMode.toString(),
        "Resolution" to sensor.resolution.toString(),
        "String type" to sensor.stringType,
        "Type" to sensor.type.toString(),
        "Vendor" to sensor.vendor,
        "Version" to sensor.version.toString(),
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this["ID"] to sensor.id.toString()
            this["Is dynamic sensor"] = sensor.isDynamicSensor.toString()
            this["Is additional info supported"] = sensor.isAdditionalInfoSupported.toString()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this["Highest direct report rate level"] =
                sensor.highestDirectReportRateLevel.toString()
        }
    }
}
