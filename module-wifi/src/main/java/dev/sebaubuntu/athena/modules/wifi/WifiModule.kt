/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.wifi

import android.Manifest
import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class WifiModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = WifiModule(context)
    }

    private val wifiManager: WifiManager? = context.getSystemService(WifiManager::class.java)

    override val id = "wifi"

    override val name = LocalizedString(R.string.section_wifi_name)

    override val description = LocalizedString(R.string.section_wifi_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_wifi

    override val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.INTERNET,
    )

    override fun resolve(identifier: Resource.Identifier) = wifiManager?.let { wifiManager ->
        when (identifier.path.firstOrNull()) {
            null -> suspend {
                val screen = Screen.CardListScreen(
                    identifier = identifier,
                    title = name,
                    elements = listOfNotNull(
                        Element.Card(
                            name = "general",
                            title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                            elements = listOf(
                                Element.Item(
                                    name = "enabled",
                                    title = LocalizedString(R.string.wifi_enabled),
                                    value = Value(wifiManager.isWifiEnabled),
                                ),
                            ),
                        ),
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Element.Card(
                                name = "supported_standards",
                                title = LocalizedString(R.string.wifi_supported_standards),
                                elements = WifiStandard.all.map { wifiStandard ->
                                    Element.Item(
                                        name = wifiStandard.name,
                                        title = LocalizedString(wifiStandard.resId),
                                        value = Value(
                                            wifiManager.isWifiStandardSupported(wifiStandard.value)
                                        ),
                                    )
                                },
                            )
                        } else {
                            null
                        },
                        Element.Card(
                            name = "supported_bands",
                            title = LocalizedString(R.string.wifi_supported_bands),
                            elements = WifiBand.all.map { wifiBand ->
                                Element.Item(
                                    name = wifiBand.name,
                                    title = LocalizedString(wifiBand.name),
                                    value = Value(wifiBand.isSupportedGetter(wifiManager)),
                                )
                            },
                        ),
                    ),
                )

                Result.Success<Resource, Error>(screen)
            }.asFlow()

            else -> flowOf(Result.Error(Error.NOT_FOUND))
        }
    } ?: flowOf(Result.Error(Error.NOT_IMPLEMENTED))

    private data class WifiStandard(
        val value: Int,
        val name: String,
        @StringRes val resId: Int,
    ) {
        companion object {
            @RequiresApi(Build.VERSION_CODES.R)
            val all = mutableListOf(
                WifiStandard(
                    ScanResult.WIFI_STANDARD_LEGACY,
                    "legacy",
                    R.string.wifi_standard_legacy,
                ),
                WifiStandard(
                    ScanResult.WIFI_STANDARD_11N,
                    "11n",
                    R.string.wifi_standard_11n,
                ),
                WifiStandard(
                    ScanResult.WIFI_STANDARD_11AC,
                    "11ac",
                    R.string.wifi_standard_11ac,
                ),
                WifiStandard(
                    ScanResult.WIFI_STANDARD_11AX,
                    "11ax",
                    R.string.wifi_standard_11ax,
                ),
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(
                        WifiStandard(
                            ScanResult.WIFI_STANDARD_11AD,
                            "11ad",
                            R.string.wifi_standard_11ad,
                        )
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(
                        WifiStandard(
                            ScanResult.WIFI_STANDARD_11BE,
                            "11be",
                            R.string.wifi_standard_11be,
                        )
                    )
                }
            }
        }
    }

    private data class WifiBand(
        val frequencyGhz: Double,
        val isSupportedGetter: WifiManager.() -> Boolean,
    ) {
        val name = "$frequencyGhz GHz"

        companion object {
            val all = buildList {
                add(
                    WifiBand(
                        2.4,
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            is24GHzBandSupported
                        } else {
                            true
                        }
                    }
                )

                add(
                    WifiBand(
                        5.0,
                        WifiManager::is5GHzBandSupported
                    )
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    add(
                        WifiBand(
                            6.0,
                            WifiManager::is6GHzBandSupported,
                        )
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(
                        WifiBand(
                            60.0,
                            WifiManager::is60GHzBandSupported,
                        )
                    )
                }
            }
        }
    }
}
