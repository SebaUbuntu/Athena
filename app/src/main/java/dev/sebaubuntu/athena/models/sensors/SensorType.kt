/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.sensors

import android.hardware.Sensor
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.R

data class SensorType(
    val id: Int,
    @StringRes val stringResId: Int,
    @DrawableRes val drawableResId: Int = R.drawable.ic_sensors,
) {
    companion object {
        val all = mutableListOf(
            SensorType(
                Sensor.TYPE_ACCELEROMETER,
                R.string.sensor_type_accelerometer,
            ),
            SensorType(
                Sensor.TYPE_MAGNETIC_FIELD,
                R.string.sensor_type_magnetic_field,
            ),
            SensorType(
                Sensor.TYPE_ORIENTATION,
                R.string.sensor_type_orientation,
            ),
            SensorType(
                Sensor.TYPE_GYROSCOPE,
                R.string.sensor_type_gyroscope,
            ),
            SensorType(
                Sensor.TYPE_LIGHT,
                R.string.sensor_type_light,
                R.drawable.ic_lightbulb,
            ),
            SensorType(
                Sensor.TYPE_PRESSURE,
                R.string.sensor_type_pressure,
            ),
            SensorType(
                Sensor.TYPE_TEMPERATURE,
                R.string.sensor_type_temperature,
                R.drawable.ic_thermostat,
            ),
            SensorType(
                Sensor.TYPE_PROXIMITY,
                R.string.sensor_type_proximity,
            ),
            SensorType(
                Sensor.TYPE_GRAVITY,
                R.string.sensor_type_gravity,
            ),
            SensorType(
                Sensor.TYPE_LINEAR_ACCELERATION,
                R.string.sensor_type_linear_acceleration,
            ),
            SensorType(
                Sensor.TYPE_ROTATION_VECTOR,
                R.string.sensor_type_rotation_vector,
                R.drawable.ic_screen_rotation,
            ),
            SensorType(
                Sensor.TYPE_RELATIVE_HUMIDITY,
                R.string.sensor_type_relative_humidity,
                R.drawable.ic_humidity_indoor,
            ),
            SensorType(
                Sensor.TYPE_AMBIENT_TEMPERATURE,
                R.string.sensor_type_ambient_temperature,
                R.drawable.ic_thermostat,
            ),
            SensorType(
                Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED,
                R.string.sensor_type_magnetic_field_uncalibrated,
            ),
            SensorType(
                Sensor.TYPE_GAME_ROTATION_VECTOR,
                R.string.sensor_type_game_rotation_vector,
                R.drawable.ic_screen_rotation,
            ),
            SensorType(
                Sensor.TYPE_GYROSCOPE_UNCALIBRATED,
                R.string.sensor_type_gyroscope_uncalibrated,
            ),
            SensorType(
                Sensor.TYPE_SIGNIFICANT_MOTION,
                R.string.sensor_type_significant_motion,
            ),
            SensorType(
                Sensor.TYPE_STEP_DETECTOR,
                R.string.sensor_type_step_detector,
                R.drawable.ic_steps,
            ),
            SensorType(
                Sensor.TYPE_STEP_COUNTER,
                R.string.sensor_type_step_counter,
                R.drawable.ic_steps,
            ),
            SensorType(
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
                R.string.sensor_type_geomagnetic_rotation_vector,
                R.drawable.ic_screen_rotation,
            ),
            SensorType(
                Sensor.TYPE_HEART_RATE,
                R.string.sensor_type_heart_rate,
                R.drawable.ic_ecg_heart,
            ),
            SensorType(
                22, // Sensor.TYPE_TILT_DETECTOR
                R.string.sensor_type_tilt_detector,
            ),
            SensorType(
                23, // Sensor.TYPE_WAKE_GESTURE
                R.string.sensor_type_wake_gesture,
            ),
            SensorType(
                24, // Sensor.TYPE_GLANCE_GESTURE
                R.string.sensor_type_glance_gesture,
            ),
            SensorType(
                25, // Sensor.TYPE_PICK_UP_GESTURE
                R.string.sensor_type_pick_up_gesture,
            ),
            SensorType(
                26, // Sensor.TYPE_WRIST_TILT_GESTURE
                R.string.sensor_type_wrist_tilt_gesture,
                R.drawable.ic_wrist,
            ),
            SensorType(
                27, // Sensor.TYPE_DEVICE_ORIENTATION
                R.string.sensor_type_device_orientation,
                R.drawable.ic_screen_rotation,
            ),
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                addAll(
                    listOf(
                        SensorType(
                            Sensor.TYPE_POSE_6DOF,
                            R.string.sensor_type_pose_6dof,
                        ),
                        SensorType(
                            Sensor.TYPE_STATIONARY_DETECT,
                            R.string.sensor_type_stationary_detect,
                        ),
                        SensorType(
                            Sensor.TYPE_MOTION_DETECT,
                            R.string.sensor_type_motion_detect,
                            R.drawable.ic_motion_sensor_active,
                        ),
                        SensorType(
                            Sensor.TYPE_HEART_BEAT,
                            R.string.sensor_type_heart_beat,
                            R.drawable.ic_ecg_heart,
                        ),
                        SensorType(
                            32, // Sensor.TYPE_DYNAMIC_SENSOR_META
                            R.string.sensor_type_dynamic_sensor_meta,
                        ),
                        SensorType(
                            33, // Sensor.TYPE_ADDITIONAL_INFO
                            R.string.sensor_type_additional_info,
                        ),
                    )
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                addAll(
                    listOf(
                        SensorType(
                            Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT,
                            R.string.sensor_type_low_latency_offbody_detect,
                        ),
                        SensorType(
                            Sensor.TYPE_ACCELEROMETER_UNCALIBRATED,
                            R.string.sensor_type_accelerometer_uncalibrated,
                        ),
                    )
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                addAll(
                    listOf(
                        SensorType(
                            Sensor.TYPE_HINGE_ANGLE,
                            R.string.sensor_type_hinge_angle,
                            R.drawable.ic_devices_fold,
                        ),
                    )
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                addAll(
                    listOf(
                        SensorType(
                            Sensor.TYPE_HEAD_TRACKER,
                            R.string.sensor_type_head_tracker,
                            R.drawable.ic_spatial_tracking,
                        ),
                        SensorType(
                            Sensor.TYPE_ACCELEROMETER_LIMITED_AXES,
                            R.string.sensor_type_accelerometer_limited_axes,
                        ),
                        SensorType(
                            Sensor.TYPE_GYROSCOPE_LIMITED_AXES,
                            R.string.sensor_type_gyroscope_limited_axes,
                        ),
                        SensorType(
                            Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED,
                            R.string.sensor_type_accelerometer_limited_axes_uncalibrated,
                        ),
                        SensorType(
                            Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED,
                            R.string.sensor_type_gyroscope_limited_axes_uncalibrated,
                        ),
                        SensorType(
                            Sensor.TYPE_HEADING,
                            R.string.sensor_type_heading,
                            R.drawable.ic_compass_calibration,
                        ),
                    )
                )
            }
        }.associateBy { it.id }

        fun fromType(type: Int) = all[type]
    }
}
