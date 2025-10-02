/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class BluetoothComponent(private val context: Context) : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = BluetoothComponent(context)
    }

    private val bluetoothManager: BluetoothManager? = context.getSystemService(
        BluetoothManager::class.java
    )

    private val bluetoothAdapter by lazy { bluetoothManager?.adapter }

    override val name = "bluetooth"

    override val title = LocalizedString(R.string.section_bluetooth_name)

    override val description = LocalizedString(R.string.section_bluetooth_description)

    override val drawableResId = R.drawable.ic_bluetooth

    override val permissions = setOf(Permission.BLUETOOTH)

    @SuppressLint("HardwareIds", "MissingPermission")
    override fun resolve(identifier: Resource.Identifier) = bluetoothAdapter?.let { adapter ->
        when (identifier.path.firstOrNull()) {
            null -> suspend {
                val screen = Screen.CardListScreen(
                    identifier = identifier,
                    title = title,
                    elements = listOfNotNull(
                        Element.Card(
                            identifier = identifier / "adapter",
                            title = LocalizedString(R.string.bluetooth_adapter),
                            isNavigable = false,
                            elements = listOfNotNull(
                                Element.Item(
                                    identifier = identifier / "adapter" / "name",
                                    title = LocalizedString(R.string.bluetooth_adapter_name),
                                    value = Value(adapter.name),
                                ),
                                Element.Item(
                                    identifier = identifier / "adapter" / "mac_address",
                                    title = LocalizedString(R.string.bluetooth_adapter_mac_address),
                                    value = Value(adapter.address),
                                ),
                            ),
                        ),
                        adapter.bondedDevices.takeIf { it.isNotEmpty() }?.let {
                            Element.Card(
                                identifier = identifier / "bonded_devices",
                                title = LocalizedString(R.string.bluetooth_bonded_devices),
                                elements = adapter.bondedDevices.map {
                                    Element.Item(
                                        identifier = identifier / "bonded_devices" / it.address,
                                        title = LocalizedString(it.name),
                                        drawableResId = R.drawable.ic_bluetooth,
                                        value = Value(it.address),
                                    )
                                },
                            )
                        },
                        Element.Card(
                            identifier / "le",
                            title = LocalizedString(R.string.bluetooth_le),
                            isNavigable = false,
                            elements = listOf(
                                Element.Item(
                                    identifier / "le" / "supported",
                                    LocalizedString(R.string.bluetooth_le_supported),
                                    value = Value(
                                        context.packageManager.hasSystemFeature(
                                            PackageManager.FEATURE_BLUETOOTH_LE
                                        )
                                    ),
                                ),
                            ),
                        )
                    ),
                )

                Result.Success<Resource, Error>(screen)
            }.asFlow()

            else -> flowOf(Result.Error<Resource, Error>(Error.NOT_FOUND))
        }
    } ?: flowOf(Result.Error<Resource, Error>(Error.NOT_IMPLEMENTED))
}
