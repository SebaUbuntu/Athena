/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category

object CameraCategory : Category() {
    override val name = R.string.section_camera_name
    override val description = R.string.section_camera_description
    override val icon = R.drawable.ic_camera
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context): Map<String, Map<String, String>> {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        return mutableMapOf<String, Map<String, String>>().apply {
            for (cameraId in cameraManager.cameraIdList) {
                this["Camera $cameraId"] = getCameraProperties(cameraId, cameraManager)
            }
        }
    }

    private fun getCameraProperties(
        cameraId: String, cameraManager: CameraManager
    ): Map<String, String> {
        val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)

        return mutableMapOf<String, String>().apply {
            this["Facing"] = when (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)) {
                CameraMetadata.LENS_FACING_BACK -> "Back"
                CameraMetadata.LENS_FACING_FRONT -> "Front"
                CameraMetadata.LENS_FACING_EXTERNAL -> "External"
                else -> "Unknown"
            }
            this["Resolution"] =
                "${cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)}"
            this["Sensor size"] =
                "${cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)}"
            this["Has flash"] =
                "${cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)}"
            cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)?.let {
                this["Available apertures"] = it.joinToString()
            }
            cameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)?.let {
                for (capability in capabilityToString) {
                    this[capability.value] = "${it.contains(capability.key)}"
                }
            }
        }
    }

    private val capabilityToString = mapOf(
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
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING to "Supports motion tracking",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA to "Is a logical camera",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME to "Is monochrome",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA to "Supports secure image captures",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA to "Only accessible to system camera apps",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING to "Supports offline reprocessing",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR to "Supports disabling pixel binning mode",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING to "Supports remosaic reprocessing",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT to "Supports 10-bit HLG output",
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_STREAM_USE_CASE to "Supports stream use cases",
    )
}
