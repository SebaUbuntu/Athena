/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.hardware.Sensor
import android.hardware.SensorDirectChannel
import android.os.Build
import androidx.annotation.RequiresApi
import dev.sebaubuntu.athena.R

object SensorUtils {
    val sensorTypeToString = mutableMapOf(
        Sensor.TYPE_ACCELEROMETER to R.string.sensor_type_accelerometer,
        Sensor.TYPE_MAGNETIC_FIELD to R.string.sensor_type_magnetic_field,
        Sensor.TYPE_ORIENTATION to R.string.sensor_type_orientation,
        Sensor.TYPE_GYROSCOPE to R.string.sensor_type_gyroscope,
        Sensor.TYPE_LIGHT to R.string.sensor_type_light,
        Sensor.TYPE_PRESSURE to R.string.sensor_type_pressure,
        Sensor.TYPE_TEMPERATURE to R.string.sensor_type_temperature,
        Sensor.TYPE_PROXIMITY to R.string.sensor_type_proximity,
        Sensor.TYPE_GRAVITY to R.string.sensor_type_gravity,
        Sensor.TYPE_LINEAR_ACCELERATION to R.string.sensor_type_linear_acceleration,
        Sensor.TYPE_ROTATION_VECTOR to R.string.sensor_type_rotation_vector,
        Sensor.TYPE_RELATIVE_HUMIDITY to R.string.sensor_type_relative_humidity,
        Sensor.TYPE_AMBIENT_TEMPERATURE to R.string.sensor_type_ambient_temperature,
        Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED to R.string.sensor_type_magnetic_field_uncalibrated,
        Sensor.TYPE_GAME_ROTATION_VECTOR to R.string.sensor_type_game_rotation_vector,
        Sensor.TYPE_GYROSCOPE_UNCALIBRATED to R.string.sensor_type_gyroscope_uncalibrated,
        Sensor.TYPE_SIGNIFICANT_MOTION to R.string.sensor_type_significant_motion,
        Sensor.TYPE_STEP_DETECTOR to R.string.sensor_type_step_detector,
        Sensor.TYPE_STEP_COUNTER to R.string.sensor_type_step_counter,
        Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR to R.string.sensor_type_geomagnetic_rotation_vector,
        Sensor.TYPE_HEART_RATE to R.string.sensor_type_heart_rate,
        //Sensor.TYPE_TILT_DETECTOR to R.string.sensor_type_tilt_detector,
        //Sensor.TYPE_WAKE_GESTURE to R.string.sensor_type_wake_gesture,
        //Sensor.TYPE_GLANCE_GESTURE to R.string.sensor_type_glance_gesture,
        //Sensor.TYPE_PICK_UP_GESTURE to R.string.sensor_type_pick_up_gesture,
        //Sensor.TYPE_WRIST_TILT_GESTURE to R.string.sensor_type_wrist_tilt_gesture,
        //Sensor.TYPE_DEVICE_ORIENTATION to R.string.sensor_type_device_orientation,
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this[Sensor.TYPE_POSE_6DOF] = R.string.sensor_type_pose_6dof
            this[Sensor.TYPE_STATIONARY_DETECT] = R.string.sensor_type_stationary_detect
            this[Sensor.TYPE_MOTION_DETECT] = R.string.sensor_type_motion_detect
            this[Sensor.TYPE_HEART_BEAT] = R.string.sensor_type_heart_beat
            //this[Sensor.TYPE_DYNAMIC_SENSOR_META] = R.string.sensor_type_dynamic_sensor_meta
            //this[Sensor.TYPE_ADDITIONAL_INFO] = R.string.sensor_type_additional_info
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this[Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT] =
                R.string.sensor_type_low_latency_offbody_detect
            this[Sensor.TYPE_ACCELEROMETER_UNCALIBRATED] =
                R.string.sensor_type_accelerometer_uncalibrated
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this[Sensor.TYPE_HINGE_ANGLE] = R.string.sensor_type_hinge_angle
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this[Sensor.TYPE_HEAD_TRACKER] = R.string.sensor_type_head_tracker
            this[Sensor.TYPE_ACCELEROMETER_LIMITED_AXES] =
                R.string.sensor_type_accelerometer_limited_axes
            this[Sensor.TYPE_GYROSCOPE_LIMITED_AXES] = R.string.sensor_type_gyroscope_limited_axes
            this[Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED] =
                R.string.sensor_type_accelerometer_limited_axes_uncalibrated
            this[Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED] =
                R.string.sensor_type_gyroscope_limited_axes_uncalibrated
            this[Sensor.TYPE_HEADING] = R.string.sensor_type_heading
        }
    }

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
