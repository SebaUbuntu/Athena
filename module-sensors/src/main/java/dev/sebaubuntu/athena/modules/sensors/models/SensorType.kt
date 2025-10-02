/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.sensors.models

import android.hardware.Sensor
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.modules.sensors.R

enum class SensorType(
    @StringRes val stringResId: Int,
    @DrawableRes val drawableResId: Int = dev.sebaubuntu.athena.core.R.drawable.ic_sensors,
) {
    ACCELEROMETER(
        R.string.sensor_type_accelerometer,
    ),
    MAGNETIC_FIELD(
        R.string.sensor_type_magnetic_field,
    ),
    ORIENTATION(
        R.string.sensor_type_orientation,
    ),
    GYROSCOPE(
        R.string.sensor_type_gyroscope,
    ),
    LIGHT(
        R.string.sensor_type_light,
        dev.sebaubuntu.athena.core.R.drawable.ic_lightbulb_2,
    ),
    PRESSURE(
        R.string.sensor_type_pressure,
    ),
    TEMPERATURE(
        R.string.sensor_type_temperature,
        dev.sebaubuntu.athena.core.R.drawable.ic_thermostat,
    ),
    PROXIMITY(
        R.string.sensor_type_proximity,
    ),
    GRAVITY(
        R.string.sensor_type_gravity,
    ),
    LINEAR_ACCELERATION(
        R.string.sensor_type_linear_acceleration,
    ),
    ROTATION_VECTOR(
        R.string.sensor_type_rotation_vector,
        dev.sebaubuntu.athena.core.R.drawable.ic_screen_rotation,
    ),
    RELATIVE_HUMIDITY(
        R.string.sensor_type_relative_humidity,
        dev.sebaubuntu.athena.core.R.drawable.ic_humidity_indoor,
    ),
    AMBIENT_TEMPERATURE(
        R.string.sensor_type_ambient_temperature,
        dev.sebaubuntu.athena.core.R.drawable.ic_thermostat,
    ),
    MAGNETIC_FIELD_UNCALIBRATED(
        R.string.sensor_type_magnetic_field_uncalibrated,
    ),
    GAME_ROTATION_VECTOR(
        R.string.sensor_type_game_rotation_vector,
        dev.sebaubuntu.athena.core.R.drawable.ic_screen_rotation,
    ),
    GYROSCOPE_UNCALIBRATED(
        R.string.sensor_type_gyroscope_uncalibrated,
    ),
    SIGNIFICANT_MOTION(
        R.string.sensor_type_significant_motion,
    ),
    STEP_DETECTOR(
        R.string.sensor_type_step_detector,
        dev.sebaubuntu.athena.core.R.drawable.ic_steps,
    ),
    STEP_COUNTER(
        R.string.sensor_type_step_counter,
        dev.sebaubuntu.athena.core.R.drawable.ic_steps,
    ),
    GEOMAGNETIC_ROTATION_VECTOR(
        R.string.sensor_type_geomagnetic_rotation_vector,
        dev.sebaubuntu.athena.core.R.drawable.ic_screen_rotation,
    ),
    HEART_RATE(
        R.string.sensor_type_heart_rate,
        dev.sebaubuntu.athena.core.R.drawable.ic_ecg_heart,
    ),
    TILT_DETECTOR(
        R.string.sensor_type_tilt_detector,
    ),
    WAKE_GESTURE(
        R.string.sensor_type_wake_gesture,
    ),
    GLANCE_GESTURE(
        R.string.sensor_type_glance_gesture,
    ),
    PICK_UP_GESTURE(
        R.string.sensor_type_pick_up_gesture,
    ),
    WRIST_TILT_GESTURE(
        R.string.sensor_type_wrist_tilt_gesture,
        dev.sebaubuntu.athena.core.R.drawable.ic_wrist,
    ),
    DEVICE_ORIENTATION(
        R.string.sensor_type_device_orientation,
        dev.sebaubuntu.athena.core.R.drawable.ic_screen_rotation,
    ),
    POSE_6DOF(
        R.string.sensor_type_pose_6dof,
    ),
    STATIONARY_DETECT(
        R.string.sensor_type_stationary_detect,
    ),
    MOTION_DETECT(
        R.string.sensor_type_motion_detect,
        dev.sebaubuntu.athena.core.R.drawable.ic_motion_sensor_active,
    ),
    HEART_BEAT(
        R.string.sensor_type_heart_beat,
        dev.sebaubuntu.athena.core.R.drawable.ic_ecg_heart,
    ),
    DYNAMIC_SENSOR_META(
        R.string.sensor_type_dynamic_sensor_meta,
    ),
    ADDITIONAL_INFO(
        R.string.sensor_type_additional_info,
    ),
    LOW_LATENCY_OFFBODY_DETECT(
        R.string.sensor_type_low_latency_offbody_detect,
    ),
    ACCELEROMETER_UNCALIBRATED(
        R.string.sensor_type_accelerometer_uncalibrated,
    ),
    HINGE_ANGLE(
        R.string.sensor_type_hinge_angle,
        dev.sebaubuntu.athena.core.R.drawable.ic_devices_fold,
    ),
    HEAD_TRACKER(
        R.string.sensor_type_head_tracker,
        dev.sebaubuntu.athena.core.R.drawable.ic_spatial_tracking,
    ),
    ACCELEROMETER_LIMITED_AXES(
        R.string.sensor_type_accelerometer_limited_axes,
    ),
    GYROSCOPE_LIMITED_AXES(
        R.string.sensor_type_gyroscope_limited_axes,
    ),
    ACCELEROMETER_LIMITED_AXES_UNCALIBRATED(
        R.string.sensor_type_accelerometer_limited_axes_uncalibrated,
    ),
    GYROSCOPE_LIMITED_AXES_UNCALIBRATED(
        R.string.sensor_type_gyroscope_limited_axes_uncalibrated,
    ),
    HEADING(
        R.string.sensor_type_heading,
        dev.sebaubuntu.athena.core.R.drawable.ic_compass_calibration,
    );

    companion object {
        @Suppress("DEPRECATION")
        val all = mutableMapOf(
            Sensor.TYPE_ACCELEROMETER to ACCELEROMETER,
            Sensor.TYPE_MAGNETIC_FIELD to MAGNETIC_FIELD,
            Sensor.TYPE_ORIENTATION to ORIENTATION,
            Sensor.TYPE_GYROSCOPE to GYROSCOPE,
            Sensor.TYPE_LIGHT to LIGHT,
            Sensor.TYPE_PRESSURE to PRESSURE,
            Sensor.TYPE_TEMPERATURE to TEMPERATURE,
            Sensor.TYPE_PROXIMITY to PROXIMITY,
            Sensor.TYPE_GRAVITY to GRAVITY,
            Sensor.TYPE_LINEAR_ACCELERATION to LINEAR_ACCELERATION,
            Sensor.TYPE_ROTATION_VECTOR to ROTATION_VECTOR,
            Sensor.TYPE_RELATIVE_HUMIDITY to RELATIVE_HUMIDITY,
            Sensor.TYPE_AMBIENT_TEMPERATURE to AMBIENT_TEMPERATURE,
            Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED to MAGNETIC_FIELD_UNCALIBRATED,
            Sensor.TYPE_GAME_ROTATION_VECTOR to GAME_ROTATION_VECTOR,
            Sensor.TYPE_GYROSCOPE_UNCALIBRATED to GYROSCOPE_UNCALIBRATED,
            Sensor.TYPE_SIGNIFICANT_MOTION to SIGNIFICANT_MOTION,
            Sensor.TYPE_STEP_DETECTOR to STEP_DETECTOR,
            Sensor.TYPE_STEP_COUNTER to STEP_COUNTER,
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR to GEOMAGNETIC_ROTATION_VECTOR,
            Sensor.TYPE_HEART_RATE to HEART_RATE,
            22 to TILT_DETECTOR, // Sensor.TYPE_TILT_DETECTOR
            23 to WAKE_GESTURE, // Sensor.TYPE_WAKE_GESTURE
            24 to GLANCE_GESTURE, // Sensor.TYPE_GLANCE_GESTURE
            25 to PICK_UP_GESTURE, // Sensor.TYPE_PICK_UP_GESTURE
            26 to WRIST_TILT_GESTURE, // Sensor.TYPE_WRIST_TILT_GESTURE
            27 to DEVICE_ORIENTATION, // Sensor.TYPE_DEVICE_ORIENTATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                put(Sensor.TYPE_POSE_6DOF, POSE_6DOF)
                put(Sensor.TYPE_STATIONARY_DETECT, STATIONARY_DETECT)
                put(Sensor.TYPE_MOTION_DETECT, MOTION_DETECT)
                put(Sensor.TYPE_HEART_BEAT, HEART_BEAT)
                put(32, DYNAMIC_SENSOR_META) // Sensor.TYPE_DYNAMIC_SENSOR_META
                put(33, ADDITIONAL_INFO) // Sensor.TYPE_ADDITIONAL_INFO
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                put(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT, LOW_LATENCY_OFFBODY_DETECT)
                put(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED, ACCELEROMETER_UNCALIBRATED)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                put(Sensor.TYPE_HINGE_ANGLE, HINGE_ANGLE)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                put(Sensor.TYPE_HEAD_TRACKER, HEAD_TRACKER)
                put(Sensor.TYPE_ACCELEROMETER_LIMITED_AXES, ACCELEROMETER_LIMITED_AXES)
                put(Sensor.TYPE_GYROSCOPE_LIMITED_AXES, GYROSCOPE_LIMITED_AXES)
                put(
                    Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED,
                    ACCELEROMETER_LIMITED_AXES_UNCALIBRATED
                )
                put(
                    Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED,
                    GYROSCOPE_LIMITED_AXES_UNCALIBRATED
                )
                put(Sensor.TYPE_HEADING, HEADING)
            }
        }.toMap()

        val sensorTypeToStringResId = all.entries.associate { it.key to it.value.stringResId }

        fun fromType(type: Int) = all[type]
    }
}
