/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.content.Context
import android.hardware.display.DeviceProductInfo
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Display
import android.view.Display.HdrCapabilities
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category

object DisplayCategory : Category {
    override val name = R.string.section_display_name
    override val description = R.string.section_display_description
    override val icon = R.drawable.ic_display
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
        val displayManager = context.getSystemService(DisplayManager::class.java)

        for (display in displayManager.displays) {
            this["Display ${display.displayId}"] = getDisplayProperties(display)
        }
    }

    private fun getDisplayProperties(display: Display): Map<String, String> {
        return mutableMapOf<String, String>().apply {
            this["Name"] = display.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                display.deviceProductInfo?.let { deviceProductInfo ->
                    deviceProductInfo.name?.let { name ->
                        if (name.isNotEmpty()) {
                            this["Product name"] = name
                        }
                    }
                    this["Manufacturer PnP ID"] = deviceProductInfo.manufacturerPnpId
                    this["Manufacturer product ID"] = deviceProductInfo.productId
                    deviceProductInfo.modelYear.let { modelYear ->
                        if (modelYear != -1) {
                            this["Model year"] = "$modelYear"
                        }
                    }
                    this["Manufacturer date"] =
                        "${deviceProductInfo.manufactureWeek}/${deviceProductInfo.manufactureYear}"
                    this["Connection to sink type"] =
                        when (deviceProductInfo.connectionToSinkType) {
                            DeviceProductInfo.CONNECTION_TO_SINK_BUILT_IN -> "Built-in"
                            DeviceProductInfo.CONNECTION_TO_SINK_DIRECT -> "Direct"
                            DeviceProductInfo.CONNECTION_TO_SINK_TRANSITIVE -> "Transitive"
                            DeviceProductInfo.CONNECTION_TO_SINK_UNKNOWN -> "Unknown"
                            else -> throw Exception("Unknown sink type")
                        }
                }
            }
            this["Is connected"] = "${display.isValid}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                display.isHdr.let {
                    this["Supports HDR"] = "$it"
                    if (it) {
                        this["Supported HDR types"] =
                            display.hdrCapabilities.supportedHdrTypes.joinToString { supportedHdrType ->
                                when (supportedHdrType) {
                                    HdrCapabilities.HDR_TYPE_DOLBY_VISION -> "Dolby Vision"
                                    HdrCapabilities.HDR_TYPE_HDR10 -> "HDR10"
                                    HdrCapabilities.HDR_TYPE_HDR10_PLUS -> "HDR10+"
                                    HdrCapabilities.HDR_TYPE_HLG -> "HLG"
                                    else -> throw Exception("Unknown HDR type")
                                }
                            }
                    }
                }
                display.isWideColorGamut.let {
                    this["Supports Wide Color Gamut"] = "$it"
                    if (it && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        this["Preferred WCG color space"] =
                            display.preferredWideGamutColorSpace?.name
                                ?: "Unknown"
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this["Supports minimal post processing mode"] =
                    "${display.isMinimalPostProcessingSupported}"
            }
            val currentMode = display.mode
            for (mode in display.supportedModes) {
                var modeString = "Mode ${mode.modeId}"
                if (mode == currentMode) {
                    modeString += " (active)"
                }
                this[modeString] =
                    "${mode.physicalWidth}x${mode.physicalHeight}@${mode.refreshRate}"
            }
        }
    }
}
