/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.Manifest
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.os.Build
import android.os.Handler
import android.os.Looper
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

object CameraSection : Section(
    "camera",
    R.string.section_camera_name,
    R.string.section_camera_description,
    R.drawable.ic_camera,
    arrayOf(
        Manifest.permission.CAMERA,
    ),
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun dataFlow(context: Context) = context.getSystemService(
        CameraManager::class.java
    )?.let { cameraManager ->
        callbackFlow {
            val cameras = cameraManager.cameraIdList.toMutableSet()

            val availabilityCallback = object : CameraManager.AvailabilityCallback() {
                override fun onCameraAvailable(cameraId: String) {
                    cameras.add(cameraId)
                    trySend(cameras)
                }

                override fun onCameraUnavailable(cameraId: String) {
                    cameras.remove(cameraId)
                    trySend(cameras)
                }
            }

            cameraManager.registerAvailabilityCallback(
                availabilityCallback, Handler(Looper.getMainLooper())
            )

            awaitClose {
                cameraManager.unregisterAvailabilityCallback(availabilityCallback)
            }
        }.mapLatest { cameras ->
            cameras.map { cameraId ->
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)

                Subsection(
                    "camera_$cameraId",
                    listOf(
                        Information(
                            "facing",
                            cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)?.let {
                                InformationValue.IntValue(it, cameraFacingToStringResId)
                            },
                            R.string.camera_facing,
                        ),
                        Information(
                            "pixel_array_size",
                            cameraCharacteristics.get(
                                CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE
                            )?.let {
                                InformationValue.StringValue("$it")
                            },
                            R.string.camera_pixel_array_size,
                        ),
                        Information(
                            "sensor_physical_size",
                            cameraCharacteristics.get(
                                CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE
                            )?.let {
                                InformationValue.StringValue("$it")
                            },
                            R.string.camera_sensor_physical_size,
                        ),
                        Information(
                            "has_flash_unit",
                            cameraCharacteristics.get(
                                CameraCharacteristics.FLASH_INFO_AVAILABLE
                            )?.let {
                                InformationValue.BooleanValue(it)
                            },
                            R.string.camera_has_flash_unit,
                        ),
                        Information(
                            "available_apertures",
                            cameraCharacteristics.get(
                                CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES
                            )?.let {
                                InformationValue.FloatArrayValue(it.toTypedArray())
                            },
                            R.string.camera_available_apertures,
                        ),
                        Information(
                            "available_capabilities",
                            cameraCharacteristics.get(
                                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
                            )?.let {
                                InformationValue.IntArrayValue(
                                    it.toTypedArray(),
                                    cameraCapabilityToStringResId,
                                )
                            },
                            R.string.camera_available_capablities,
                        ),
                    ),
                    R.string.camera_title,
                    arrayOf(cameraId),
                )
            }
        }
    } ?: flowOf(
        listOf(
            Subsection(
                "not_available",
                listOf(),
                R.string.camera_not_available,
            )
        )
    )

    private val cameraFacingToStringResId = mapOf(
        CameraMetadata.LENS_FACING_BACK to R.string.camera_facing_back,
        CameraMetadata.LENS_FACING_FRONT to R.string.camera_facing_front,
        CameraMetadata.LENS_FACING_EXTERNAL to R.string.camera_facing_external,
    )

    private val cameraCapabilityToStringResId = mutableMapOf(
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE to
                R.string.camera_capabilities_backward_compatible,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR to
                R.string.camera_capabilities_manual_sensor,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING to
                R.string.camera_capabilities_manual_post_processing,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW to
                R.string.camera_capabilities_raw,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING to
                R.string.camera_capabilities_private_reprocessing,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS to
                R.string.camera_capabilities_read_sensor_settings,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE to
                R.string.camera_capabilities_burst_capture,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING to
                R.string.camera_capabilities_yuv_reprocessing,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT to
                R.string.camera_capabilities_depth_output,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO to
                R.string.camera_capabilities_constrained_high_speed_video,
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING] =
                R.string.camera_capabilities_motion_tracking
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA] =
                R.string.camera_capabilities_logical_multi_camera
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME] =
                R.string.camera_capabilities_monochrome
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA] =
                R.string.camera_capabilities_secure_image_data
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA] =
                R.string.camera_capabilities_system_camera
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING] =
                R.string.camera_capabilities_offline_processing
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR] =
                R.string.camera_capabilities_ultra_high_resolution_sensor
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING] =
                R.string.camera_capabilities_remosaic_processing
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT] =
                R.string.camera_capabilities_dynamic_range_ten_bit
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_STREAM_USE_CASE] =
                R.string.camera_capabilities_stream_use_case
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_COLOR_SPACE_PROFILES] =
                R.string.camera_capabilities_color_space_profiles
        }
    }
}
