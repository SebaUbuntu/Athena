/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.components.Component
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Permission
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.ext.cameraIdsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class CameraComponent(context: Context) : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = CameraComponent(context)
    }

    private val cameraManager: CameraManager? = context.getSystemService(
        CameraManager::class.java
    )

    override val name = "camera"

    override val title = LocalizedString(R.string.section_camera_name)

    override val description = LocalizedString(R.string.section_camera_description)

    override val drawableResId = R.drawable.ic_camera

    override val permissions = setOf(Permission.CAMERA)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(identifier: Resource.Identifier) = cameraManager?.let { cameraManager ->
        when (identifier.path.firstOrNull()) {
            null -> cameraManager.cameraIdsFlow().mapLatest { cameraIds ->
                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = title,
                    elements = cameraIds.map { cameraId ->
                        val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)

                        val cameraFacing = cameraCharacteristics.get(
                            CameraCharacteristics.LENS_FACING
                        )

                        Element.Item(
                            identifier = identifier / cameraId,
                            title = LocalizedString(
                                R.string.camera_title,
                                cameraId,
                            ),
                            isNavigable = true,
                            drawableResId = R.drawable.ic_camera,
                            value = cameraFacing?.let {
                                Value.IntValue(
                                    value = it,
                                    valueToStringResId = cameraFacingToStringResId,
                                )
                            },
                        )
                    },
                )

                Result.Success<Resource, Error>(screen)
            }

            else -> when (identifier.path.getOrNull(1)) {
                null -> identifier.path.first().let { cameraId ->
                    cameraManager.cameraIdsFlow()
                        .mapLatest { cameraIds -> cameraIds.contains(cameraId) }
                        .distinctUntilChanged()
                        .mapLatest { isPresent ->
                            when (isPresent) {
                                true -> {
                                    val screen = cameraManager.getCameraScreen(
                                        identifier = identifier,
                                        cameraId = cameraId,
                                    )

                                    Result.Success<Resource, Error>(screen)
                                }

                                false -> Result.Error(Error.NOT_FOUND)
                            }
                        }
                }

                else -> flowOf(Result.Error(Error.NOT_FOUND))
            }
        }
    } ?: flowOf(Result.Error(Error.NOT_IMPLEMENTED))

    private fun CameraManager.getCameraScreen(
        identifier: Resource.Identifier,
        cameraId: String,
    ): Screen.ItemListScreen {
        val cameraCharacteristics = getCameraCharacteristics(cameraId)

        return Screen.ItemListScreen(
            identifier = identifier,
            title = LocalizedString(R.string.camera_title, cameraId),
            elements = listOf(
                Element.Item(
                    identifier = identifier / "facing",
                    title = LocalizedString(R.string.camera_facing),
                    value = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)?.let {
                        Value(it, cameraFacingToStringResId)
                    },
                ),
                Element.Item(
                    identifier = identifier / "pixel_array_size",
                    title = LocalizedString(R.string.camera_pixel_array_size),
                    value = cameraCharacteristics.get(
                        CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE
                    )?.let {
                        Value("$it")
                    },
                ),
                Element.Item(
                    identifier = identifier / "sensor_physical_size",
                    title = LocalizedString(R.string.camera_sensor_physical_size),
                    value = cameraCharacteristics.get(
                        CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE
                    )?.let {
                        Value("$it")
                    },
                ),
                Element.Item(
                    identifier = identifier / "has_flash_unit",
                    title = LocalizedString(R.string.camera_has_flash_unit),
                    value = cameraCharacteristics.get(
                        CameraCharacteristics.FLASH_INFO_AVAILABLE
                    )?.let {
                        Value(it)
                    },
                ),
                Element.Item(
                    identifier = identifier / "available_apertures",
                    title = LocalizedString(R.string.camera_available_apertures),
                    value = cameraCharacteristics.get(
                        CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES
                    )?.let {
                        Value(it.toTypedArray())
                    },
                ),
                Element.Item(
                    identifier = identifier / "available_capabilities",
                    title = LocalizedString(R.string.camera_available_capablities),
                    value = cameraCharacteristics.get(
                        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
                    )?.let {
                        Value(
                            it.toTypedArray(),
                            cameraCapabilityToStringResId,
                        )
                    },
                ),
            ),
        )
    }

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
