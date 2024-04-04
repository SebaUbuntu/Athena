/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.hardware.display.DeviceProductInfo
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Display
import android.view.Display.HdrCapabilities
import androidx.annotation.RequiresApi
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.mapLatest

object DisplaySection : Section(
    "display",
    R.string.section_display_name,
    R.string.section_display_description,
    R.drawable.ic_display,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun dataFlow(context: Context) = callbackFlow {
        val displayManager = context.getSystemService(DisplayManager::class.java)!!

        val displays = displayManager.displays.associateBy {
            it.displayId
        }.toMutableMap()

        val displayListener = object : DisplayManager.DisplayListener {
            override fun onDisplayAdded(displayId: Int) {
                displays[displayId] = displayManager.getDisplay(displayId)
                trySend(displays)
            }

            override fun onDisplayRemoved(displayId: Int) {
                displays.remove(displayId)
                trySend(displays)
            }

            override fun onDisplayChanged(displayId: Int) {
                displays[displayId] = displayManager.getDisplay(displayId)
                trySend(displays)
            }
        }

        displayManager.registerDisplayListener(displayListener, null)

        trySend(displays)

        awaitClose {
            displayManager.unregisterDisplayListener(displayListener)
        }
    }.mapLatest {
        it.map { (_, display) ->
            getDisplayInfo(display)
        }
    }

    private fun getDisplayInfo(display: Display) = Subsection(
        "display_${display.displayId}",
        listOfNotNull(
            Information(
                "name",
                InformationValue.StringValue(display.name),
                R.string.display_name,
            ),
            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                display.deviceProductInfo?.let { deviceProductInfo ->
                    listOfNotNull(
                        deviceProductInfo.name?.let {
                            Information(
                                "product_name",
                                InformationValue.StringValue(it),
                                R.string.display_product_name,
                            )
                        },
                        Information(
                            "manufacturer_pnp_id",
                            InformationValue.StringValue(deviceProductInfo.manufacturerPnpId),
                            R.string.display_manufacturer_pnp_id,
                        ),
                        Information(
                            "manufacturer_product_id",
                            InformationValue.StringValue(deviceProductInfo.productId),
                            R.string.display_manufacturer_product_id,
                        ),
                        deviceProductInfo.modelYear.takeIf { it != -1 }?.let {
                            Information(
                                "model_year",
                                InformationValue.IntValue(it),
                                R.string.display_model_year,
                            )
                        },
                        deviceProductInfo.manufactureWeek.takeIf { it != -1 }?.let {
                            Information(
                                "manufacture_week",
                                InformationValue.IntValue(it),
                                R.string.display_manufacture_week,
                            )
                        },
                        deviceProductInfo.manufactureYear.takeIf { it != -1 }?.let {
                            Information(
                                "manufacture_year",
                                InformationValue.IntValue(it),
                                R.string.display_manufacture_year,
                            )
                        },
                        Information(
                            "connection_to_sink_type",
                            InformationValue.IntValue(
                                deviceProductInfo.connectionToSinkType,
                                connectionToSinkTypeToStringResId,
                            ),
                            R.string.display_connection_to_sink_type,
                        ),
                    ).toTypedArray()
                } ?: arrayOf()
            } else {
                arrayOf()
            },
            Information(
                "is_valid",
                InformationValue.BooleanValue(display.isValid),
                R.string.display_is_valid,
            ),
            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                display.isHdr.let { isHdr ->
                    listOfNotNull(
                        Information(
                            "is_hdr",
                            InformationValue.BooleanValue(isHdr),
                            R.string.display_is_hdr,
                        ),
                        *if (isHdr) {
                            listOfNotNull(
                                Information(
                                    "supported_hdr_types",
                                    InformationValue.IntArrayValue(
                                        display.hdrCapabilities.supportedHdrTypes.toTypedArray(),
                                        hdrTypeToStringResId,
                                    ),
                                    R.string.display_supported_hdr_types,
                                )
                            ).toTypedArray()
                        } else {
                            arrayOf()
                        }
                    ).toTypedArray()
                }
            } else {
                arrayOf()
            },
            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                display.isWideColorGamut.let { isWideColorGamut ->
                    listOfNotNull(
                        Information(
                            "is_wide_color_gamut",
                            InformationValue.BooleanValue(isWideColorGamut),
                            R.string.display_is_wide_color_gamut
                        ),
                    ).toTypedArray()
                }
            } else {
                arrayOf()
            },
            *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                display.preferredWideGamutColorSpace?.let {
                    listOfNotNull(
                        Information(
                            "preferred_wcg_color_space",
                            InformationValue.StringValue(it.name),
                            R.string.display_preferred_wcg_color_space,
                        )
                    ).toTypedArray()
                } ?: arrayOf()
            } else {
                arrayOf()
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Information(
                    "is_minimal_post_processing_supported",
                    InformationValue.BooleanValue(display.isMinimalPostProcessingSupported),
                    R.string.display_is_minimal_post_processing_supported
                )
            } else {
                null
            },
            *display.mode.let { currentMode ->
                display.supportedModes.map {
                    Information(
                        "mode_${it.modeId}",
                        InformationValue.StringValue(
                            "${it.physicalWidth}x${it.physicalHeight}@${it.refreshRate}",
                            R.string.display_mode_resolution,
                            arrayOf(it.physicalWidth, it.physicalHeight, it.refreshRate),
                        ),
                        when (it == currentMode) {
                            true -> R.string.display_mode_active
                            false -> R.string.display_mode_not_active
                        },
                        arrayOf(it.modeId)
                    )
                }.toTypedArray()
            }
        ),
        R.string.display_title,
        arrayOf(display.displayId),
    )

    @RequiresApi(Build.VERSION_CODES.S)
    private val connectionToSinkTypeToStringResId = mapOf(
        DeviceProductInfo.CONNECTION_TO_SINK_UNKNOWN to R.string.display_connection_to_sink_unknown,
        DeviceProductInfo.CONNECTION_TO_SINK_BUILT_IN to R.string.display_connection_to_sink_built_in,
        DeviceProductInfo.CONNECTION_TO_SINK_DIRECT to R.string.display_connection_to_sink_direct,
        DeviceProductInfo.CONNECTION_TO_SINK_TRANSITIVE to R.string.display_connection_to_sink_transitive,
    )

    @RequiresApi(Build.VERSION_CODES.N)
    private val hdrTypeToStringResId = mutableMapOf(
        HdrCapabilities.HDR_TYPE_DOLBY_VISION to R.string.display_hdr_type_dolby_vision,
        HdrCapabilities.HDR_TYPE_HDR10 to R.string.display_hdr_type_hdr10,
        HdrCapabilities.HDR_TYPE_HLG to R.string.display_hdr_type_hlg
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this[HdrCapabilities.HDR_TYPE_HDR10_PLUS] = R.string.display_hdr_type_hdr10_plus
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this[HdrCapabilities.HDR_TYPE_INVALID] = R.string.display_hdr_type_invalid
        }
    }.toMap()
}
