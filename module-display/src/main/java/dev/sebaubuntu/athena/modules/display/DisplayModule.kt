/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.display

import android.content.Context
import android.hardware.display.DeviceProductInfo
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.os.Build
import android.view.Display
import androidx.annotation.RequiresApi
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.modules.display.ext.displayFlow
import dev.sebaubuntu.athena.modules.display.ext.displaysFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class DisplayModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = DisplayModule(context)
    }

    private val displayManager = context.getSystemService(DisplayManager::class.java)!!

    private val inputManager = context.getSystemService(InputManager::class.java)!!

    override val id = "display"

    override val name = LocalizedString(R.string.section_display_name)

    override val description = LocalizedString(R.string.section_display_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_display

    override val requiredPermissions = arrayOf<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> displayManager.displaysFlow().mapLatest { displays ->
            val screen = Screen.ItemListScreen(
                identifier = identifier,
                title = name,
                elements = displays.map { display ->
                    Element.Item(
                        name = "${display.displayId}",
                        title = LocalizedString(
                            R.string.display_title,
                            display.displayId,
                        ),
                        navigateTo = identifier / "${display.displayId}",
                        drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_display,
                        value = Value(display.name),
                    )
                },
            )

            Result.Success<Resource, Error>(screen)
        }

        else -> when (identifier.path.getOrNull(1)) {
            null -> identifier.path.first().toIntOrNull()?.let { displayId ->
                displayManager.displayFlow(displayId).mapLatest { display ->
                    val screen = display?.getScreen(
                        identifier = identifier,
                    )

                    screen?.let {
                        Result.Success<Resource, Error>(it)
                    } ?: Result.Error(Error.NOT_FOUND)
                }
            } ?: flowOf(Result.Error(Error.NOT_FOUND))

            else -> flowOf(Result.Error(Error.NOT_FOUND))
        }
    }

    private fun Display.getScreen(
        identifier: Resource.Identifier,
    ) = Screen.CardListScreen(
        identifier = identifier,
        title = LocalizedString(
            R.string.display_title,
            displayId,
        ),
        elements = listOfNotNull(
            Element.Card(
                name = "general",
                title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                elements = listOfNotNull(
                    Element.Item(
                        name = "id",
                        title = LocalizedString(R.string.display_id),
                        value = Value(displayId),
                    ),
                    Element.Item(
                        name = "name",
                        title = LocalizedString(R.string.display_name),
                        value = Value(name),
                    ),
                    *if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        deviceProductInfo?.let { deviceProductInfo ->
                            listOfNotNull(
                                deviceProductInfo.name?.let {
                                    Element.Item(
                                        name = "product_name",
                                        title = LocalizedString(R.string.display_product_name),
                                        value = Value(it),
                                    )
                                },
                                Element.Item(
                                    name = "manufacturer_pnp_id",
                                    title = LocalizedString(R.string.display_manufacturer_pnp_id),
                                    value = Value(deviceProductInfo.manufacturerPnpId),
                                ),
                                Element.Item(
                                    name = "manufacturer_product_id",
                                    title = LocalizedString(R.string.display_manufacturer_product_id),
                                    value = Value(deviceProductInfo.productId),
                                ),
                                deviceProductInfo.modelYear.takeIf { it != -1 }?.let {
                                    Element.Item(
                                        name = "model_year",
                                        title = LocalizedString(R.string.display_model_year),
                                        value = Value(it),
                                    )
                                },
                                deviceProductInfo.manufactureWeek.takeIf { it != -1 }?.let {
                                    Element.Item(
                                        name = "manufacture_week",
                                        title = LocalizedString(R.string.display_manufacture_week),
                                        value = Value(it),
                                    )
                                },
                                deviceProductInfo.manufactureYear.takeIf { it != -1 }?.let {
                                    Element.Item(
                                        name = "manufacture_year",
                                        title = LocalizedString(R.string.display_manufacture_year),
                                        value = Value(it),
                                    )
                                },
                                Element.Item(
                                    name = "connection_to_sink_type",
                                    title = LocalizedString(R.string.display_connection_to_sink_type),
                                    value = Value(
                                        deviceProductInfo.connectionToSinkType,
                                        connectionToSinkTypeToStringResId,
                                    ),
                                ),
                            ).toTypedArray()
                        }.orEmpty()
                    } else {
                        arrayOf()
                    },
                    Element.Item(
                        name = "is_valid",
                        title = LocalizedString(R.string.display_is_valid),
                        value = Value(isValid),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Element.Item(
                            name = "is_hdr",
                            title = LocalizedString(R.string.display_is_hdr),
                            value = Value(isHdr),
                        )
                    } else {
                        null
                    },
                    Element.Item(
                        name="is_flag_presentable",
                        title=LocalizedString(R.string.display_is_flag_presentable),
                        value=Value((flags and Display.FLAG_PRESENTATION != 0))
                    ),
                    Element.Item(
                        name="is_flag_secure",
                        title=LocalizedString(R.string.display_is_flag_secure),
                        value=Value((flags and Display.FLAG_SECURE != 0))
                    ),
                    Element.Item(
                        name="is_flag_secure",
                        title=LocalizedString(R.string.display_is_flag_private),
                        value=Value((flags and Display.FLAG_PRIVATE != 0))
                    ),
                    Element.Item(
                        name="is_flag_supports_protected_buffers",
                        title=LocalizedString(R.string.display_is_flag_supports_protected_buffers),
                        value=Value((flags and Display.FLAG_SUPPORTS_PROTECTED_BUFFERS != 0))
                    ),
                    Element.Item(
                        name="is_flag_round",
                        title=LocalizedString(R.string.display_is_flag_round),
                        value=Value((flags and Display.FLAG_ROUND != 0))
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Element.Item(
                            name = "is_wide_color_gamut",
                            title = LocalizedString(R.string.display_is_wide_color_gamut),
                            value = Value(isWideColorGamut),
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        preferredWideGamutColorSpace?.let {
                            Element.Item(
                                name = "preferred_wcg_color_space",
                                title = LocalizedString(R.string.display_preferred_wcg_color_space),
                                value = Value(it.name),
                            )
                        }
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Element.Item(
                            name = "is_minimal_post_processing_supported",
                            title = LocalizedString(R.string.display_is_minimal_post_processing_supported),
                            value = Value(isMinimalPostProcessingSupported),
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        inputManager.getHostUsiVersion(this)?.let {
                            Element.Item(
                                name = "host_usi_version",
                                title = LocalizedString(R.string.display_host_usi_version),
                                value = Value(
                                    "${it.majorVersion}.${it.minorVersion}",
                                ),
                            )
                        }
                    } else {
                        null
                    },
                ),
            ),
            *mode.let { currentMode ->
                supportedModes.map {
                    Element.Card(
                        name = "mode_${it.modeId}",
                        title = LocalizedString(
                            when (it == currentMode) {
                                true -> R.string.display_mode_active
                                false -> R.string.display_mode_not_active
                            },
                            it.modeId,
                        ),
                        elements = listOfNotNull(
                            Element.Item(
                                name = "id",
                                title = LocalizedString(R.string.display_mode_id),
                                value = Value(it.modeId),
                            ),
                            Element.Item(
                                name = "resolution",
                                title = LocalizedString(R.string.display_mode_resolution),
                                value = Value(
                                    "${it.physicalWidth}x${it.physicalHeight}@${it.refreshRate}",
                                    R.string.display_mode_resolution_format,
                                    it.physicalWidth, it.physicalHeight, it.refreshRate,
                                ),
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                Element.Item(
                                    name = "alternative_refresh_rates",
                                    title = LocalizedString(R.string.display_mode_alternative_refresh_rates),
                                    value = Value(
                                        it.alternativeRefreshRates.toTypedArray(),
                                    ),
                                )
                            } else {
                                null
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Element.Item(
                                    name = "supported_hdr_types",
                                    title = LocalizedString(R.string.display_mode_supported_hdr_types),
                                    value = Value(
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                            it.supportedHdrTypes.toTypedArray()
                                        } else {
                                            @Suppress("DEPRECATION")
                                            hdrCapabilities.supportedHdrTypes.toTypedArray()
                                        },
                                        hdrTypeToStringResId,
                                    ),
                                )
                            } else {
                                null
                            },
                        ),
                    )
                }.toTypedArray()
            },
        ),
    )

    companion object {
        @RequiresApi(Build.VERSION_CODES.S)
        private val connectionToSinkTypeToStringResId = mapOf(
            DeviceProductInfo.CONNECTION_TO_SINK_UNKNOWN to R.string.display_connection_to_sink_unknown,
            DeviceProductInfo.CONNECTION_TO_SINK_BUILT_IN to R.string.display_connection_to_sink_built_in,
            DeviceProductInfo.CONNECTION_TO_SINK_DIRECT to R.string.display_connection_to_sink_direct,
            DeviceProductInfo.CONNECTION_TO_SINK_TRANSITIVE to R.string.display_connection_to_sink_transitive,
        )

        @RequiresApi(Build.VERSION_CODES.N)
        private val hdrTypeToStringResId = mutableMapOf(
            Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION to R.string.display_hdr_type_dolby_vision,
            Display.HdrCapabilities.HDR_TYPE_HDR10 to R.string.display_hdr_type_hdr10,
            Display.HdrCapabilities.HDR_TYPE_HLG to R.string.display_hdr_type_hlg
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this[Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS] =
                    R.string.display_hdr_type_hdr10_plus
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                this[Display.HdrCapabilities.HDR_TYPE_INVALID] = R.string.display_hdr_type_invalid
            }
        }.toMap()
    }
}
