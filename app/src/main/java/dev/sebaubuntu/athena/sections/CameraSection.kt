/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.os.Build
import dev.sebaubuntu.athena.R

object CameraSection : Section() {
    override val name = R.string.section_camera_name
    override val description = R.string.section_camera_description
    override val icon = R.drawable.ic_camera
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context): Map<String, Map<String, String?>> {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        return mutableMapOf<String, Map<String, String?>>().apply {
            for (cameraId in cameraManager.cameraIdList) {
                this["Camera $cameraId"] = getCameraProperties(cameraId, cameraManager)
            }
        }
    }

    private fun getCameraProperties(
        cameraId: String, cameraManager: CameraManager
    ): Map<String, String?> {
        val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)

        return mutableMapOf<String, String?>().apply {
            this["Facing"] = when (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)) {
                CameraMetadata.LENS_FACING_BACK -> "Back"
                CameraMetadata.LENS_FACING_FRONT -> "Front"
                CameraMetadata.LENS_FACING_EXTERNAL -> "External"
                else -> null
            }
            this["Resolution"] = cameraCharacteristics.get(
                CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE
            )?.let {
                "$it"
            }
            this["Sensor size"] = cameraCharacteristics.get(
                CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE
            )?.let {
                "$it"
            }
            this["Has flash"] = cameraCharacteristics.get(
                CameraCharacteristics.FLASH_INFO_AVAILABLE
            )?.let {
                "$it"
            }
            this["Available apertures"] = cameraCharacteristics.get(
                CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES
            )?.joinToString()
            cameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)?.let {
                for (capability in capabilityToString) {
                    this[capability.value] = "${it.contains(capability.key)}"
                }
            }
        }
    }

    private val capabilityToString = mutableMapOf(
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE to "Is backward compatible",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR to "Supports 3A manual controls",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING to "Supports manual post-processing",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW to "Supports RAW",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING to "Supports ZSL",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS to "Supports precise sensor settings reporting",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE to "Supports burst capture",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING to "Supports YUV reprocessing",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT to "Supports depth measurement",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO to "Supports HFR recording",
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING] =
                "Supports motion tracking"
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA] =
                "Is a logical camera"
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME] = "Is monochrome"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA] =
                "Supports secure image captures"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA] =
                "Only accessible to system camera apps"
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING] =
                "Supports offline reprocessing"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR] =
                "Supports disabling pixel binning mode"
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING] =
                "Supports remosaic reprocessing"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT] =
                "Supports 10-bit HLG output"
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_STREAM_USE_CASE] =
                "Supports stream use cases"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this[CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_COLOR_SPACE_PROFILES] =
                "Supports color space profiles"
        }
    }
}
