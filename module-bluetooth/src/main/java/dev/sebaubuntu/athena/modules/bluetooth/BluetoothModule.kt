/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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

class BluetoothModule(private val context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = BluetoothModule(context)
    }

    private val bluetoothManager: BluetoothManager? = context.getSystemService(
        BluetoothManager::class.java
    )

    private val bluetoothAdapter by lazy { bluetoothManager?.adapter }

    override val id = "bluetooth"

    override val name = LocalizedString(R.string.section_bluetooth_name)

    override val description = LocalizedString(R.string.section_bluetooth_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_bluetooth

    override val requiredPermissions = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            add(Manifest.permission.BLUETOOTH)
            add(Manifest.permission.BLUETOOTH_ADMIN)
        }
    }.toTypedArray()

    @SuppressLint("HardwareIds", "MissingPermission")
    override fun resolve(identifier: Resource.Identifier) = bluetoothAdapter?.let { adapter ->
        when (identifier.path.firstOrNull()) {
            null -> suspend {
                val screen = Screen.CardListScreen(
                    identifier = identifier,
                    title = name,
                    elements = listOfNotNull(
                        Element.Card(
                            name = "adapter",
                            title = LocalizedString(R.string.bluetooth_adapter),
                            elements = listOfNotNull(
                                Element.Item(
                                    name = "name",
                                    title = LocalizedString(R.string.bluetooth_adapter_name),
                                    value = Value(adapter.name),
                                ),
                                Element.Item(
                                    name = "mac_address",
                                    title = LocalizedString(R.string.bluetooth_adapter_mac_address),
                                    value = Value(adapter.address),
                                ),
                            ),
                        ),
                        adapter.bondedDevices.takeIf { it.isNotEmpty() }?.let {
                            Element.Card(
                                name = "bonded_devices",
                                title = LocalizedString(R.string.bluetooth_bonded_devices),
                                elements = adapter.bondedDevices.map {
                                    Element.Item(
                                        name = it.address,
                                        title = LocalizedString(it.name),
                                        drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_bluetooth,
                                        value = Value(it.address),
                                    )
                                },
                            )
                        },
                        Element.Card(
                            name = "le",
                            title = LocalizedString(R.string.bluetooth_le),
                            elements = listOf(
                                Element.Item(
                                    name = "supported",
                                    title = LocalizedString(R.string.bluetooth_le_supported),
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
