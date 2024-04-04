/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.flow.asFlow

object BluetoothSection : Section(
    "bluetooth",
    R.string.section_bluetooth_name,
    R.string.section_bluetooth_description,
    R.drawable.ic_bluetooth,
    mutableListOf<String>().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            this.add(Manifest.permission.BLUETOOTH)
            this.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
    }.toTypedArray()
) {
    @SuppressLint("HardwareIds", "MissingPermission")
    override fun dataFlow(context: Context) = {
        context.getSystemService(
            BluetoothManager::class.java
        )?.let { bluetoothManager ->
            listOfNotNull(
                *bluetoothManager.adapter?.let { adapter ->
                    listOfNotNull(
                        Subsection(
                            "adapter",
                            listOfNotNull(
                                Information(
                                    "name",
                                    InformationValue.StringValue(adapter.name),
                                    R.string.bluetooth_adapter_name,
                                ),
                                Information(
                                    "mac_address",
                                    InformationValue.StringValue(adapter.address),
                                    R.string.bluetooth_adapter_mac_address,
                                ),
                            ),
                            R.string.bluetooth_adapter,
                        ),
                        adapter.bondedDevices.takeIf { it.isNotEmpty() }?.let {
                            Subsection(
                                "bonded_devices",
                                adapter.bondedDevices.map {
                                    Information(
                                        it.address,
                                        InformationValue.StringValue(it.name)
                                    )
                                },
                                R.string.bluetooth_bonded_devices,
                            )
                        },
                        Subsection(
                            "le",
                            listOf(
                                Information(
                                    "supported",
                                    InformationValue.BooleanValue(
                                        context.packageManager.hasSystemFeature(
                                            PackageManager.FEATURE_BLUETOOTH_LE
                                        )
                                    ),
                                    R.string.bluetooth_le_supported,
                                ),
                            ),
                            R.string.bluetooth_le,
                        )
                    )
                }?.toTypedArray() ?: arrayOf(
                    Subsection(
                        "no_adapter",
                        listOf(),
                        R.string.bluetooth_no_adapter,
                    )
                ),
            )
        } ?: listOf(
            Subsection(
                "not_supported",
                listOf(),
                R.string.bluetooth_not_supported,
            )
        )
    }.asFlow()
}
