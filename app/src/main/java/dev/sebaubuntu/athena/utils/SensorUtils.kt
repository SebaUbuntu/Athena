/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.hardware.Sensor
import android.hardware.SensorDirectChannel
import android.os.Build
import androidx.annotation.RequiresApi
import dev.sebaubuntu.athena.R

object SensorUtils {
    val sensorReportingModeToString = mapOf(
        Sensor.REPORTING_MODE_CONTINUOUS to R.string.sensor_reporting_mode_continuous,
        Sensor.REPORTING_MODE_ON_CHANGE to R.string.sensor_reporting_mode_on_change,
        Sensor.REPORTING_MODE_ONE_SHOT to R.string.sensor_reporting_mode_one_shot,
        Sensor.REPORTING_MODE_SPECIAL_TRIGGER to R.string.sensor_reporting_mode_special_trigger,
    )

    @RequiresApi(Build.VERSION_CODES.O)
    val sensorDirectReportModeRatesToString = mapOf(
        SensorDirectChannel.RATE_STOP to R.string.sensor_direct_report_mode_rate_stop,
        SensorDirectChannel.RATE_NORMAL to R.string.sensor_direct_report_mode_rate_normal,
        SensorDirectChannel.RATE_FAST to R.string.sensor_direct_report_mode_rate_fast,
        SensorDirectChannel.RATE_VERY_FAST to R.string.sensor_direct_report_mode_rate_very_fast,
    )
}
