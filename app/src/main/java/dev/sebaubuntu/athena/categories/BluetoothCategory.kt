/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category
import dev.sebaubuntu.athena.utils.DeviceInfo

object BluetoothCategory : Category {
    override val name = R.string.section_bluetooth_name
    override val description = R.string.section_bluetooth_description
    override val icon = R.drawable.ic_bluetooth
    override val requiredPermissions = mutableListOf<String>().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }.toTypedArray()

    @SuppressLint("HardwareIds")
    override fun getInfo(context: Context): Map<String, Map<String, String>> {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return mapOf(
                "Bluetooth permission not granted" to mapOf()
            )
        }

        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter ?: return mapOf(
            "Bluetooth not supported" to mapOf()
        )

        return mapOf(
            "Adapter" to mapOf(
                "Name" to bluetoothAdapter.name,
                "MAC address" to bluetoothAdapter.address,
            ),
            "Bonded devices" to bluetoothAdapter.bondedDevices.associate {
                it.name to it.address
            },
            "A2DP" to mapOf(
                "Hardware offload supported" to "${DeviceInfo.a2dpOffloadSupported}",
                "Hardware offload disabled" to "${DeviceInfo.a2dpOffloadDisabled}"
            ),
            "LE" to mapOf(
                "Supported" to "${
                    context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
                }",
            ),
        )
    }
}
