/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
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

object BluetoothSection : Section() {
    override val name = R.string.section_bluetooth_name
    override val description = R.string.section_bluetooth_description
    override val icon = R.drawable.ic_bluetooth
    override val requiredPermissions = mutableListOf<String>().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            this.add(Manifest.permission.BLUETOOTH)
            this.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
    }.toTypedArray()

    @SuppressLint("HardwareIds", "MissingPermission")
    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)

        bluetoothManager.adapter?.also { bluetoothAdapter ->
            this["Adapter"] = mapOf(
                "Name" to bluetoothAdapter.name,
                "MAC address" to bluetoothAdapter.address,
            )

            val bondedDevices = bluetoothAdapter.bondedDevices
            if (bondedDevices.isNotEmpty()) {
                this["Bonded devices"] = bondedDevices.associate {
                    it.name to it.address
                }
            }

            this["LE"] = mapOf(
                "Supported" to "${
                    context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
                }",
            )
        } ?: run {
            this["Bluetooth not supported"] = mapOf()
        }
    }.toMap()
}
