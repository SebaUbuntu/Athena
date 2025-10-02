/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import dev.sebaubuntu.athena.core.models.Permission
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import dev.sebaubuntu.athena.ext.permissionsGranted
import dev.sebaubuntu.athena.ext.permissionsGrantedFlow
import dev.sebaubuntu.athena.models.PermissionState

class PermissionsManager(
    private val activity: ComponentActivity,
) {
    // Each permission needs a PermissionsChecker
    private val permissionsCheckers = Permission.entries.associateWith {
        PermissionsChecker(activity, it.toAndroidPermissions())
    }

    fun permissionState(
        permission: Permission
    ) = when (activity.permissionsGranted(permission.toAndroidPermissions())) {
        true -> PermissionState.GRANTED
        false -> PermissionState.NOT_GRANTED
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun permissionStateFlow(
        permission: Permission
    ) = activity.permissionsGrantedFlow(activity.lifecycle, permission.toAndroidPermissions())
        .mapLatest {
            when (it) {
                true -> PermissionState.GRANTED
                false -> PermissionState.NOT_GRANTED
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun withPermissionGranted(
        permission: Permission, block: suspend () -> Unit,
    ) = permissionStateFlow(permission)
        .distinctUntilChanged()
        .collectLatest { permissionState ->
            when (permissionState) {
                PermissionState.GRANTED -> block()
                else -> Unit
            }
        }

    suspend fun requestPermission(
        permission: Permission
    ) = when (permission.permissionsChecker.requestPermissions()) {
        true -> PermissionState.GRANTED
        false -> PermissionState.NOT_GRANTED
    }

    private val Permission.permissionsChecker: PermissionsChecker
        get() = permissionsCheckers[this] ?: error(
            "No PermissionsChecker for permission ${this.name}"
        )

    companion object {
        private val biometricsPermissions = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(Manifest.permission.USE_BIOMETRIC)
            } else {
                @Suppress("DEPRECATION")
                add(Manifest.permission.USE_FINGERPRINT)
            }
        }.toTypedArray()

        private val bluetoothPermissions = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                add(Manifest.permission.BLUETOOTH)
                add(Manifest.permission.BLUETOOTH_ADMIN)
            }
        }.toTypedArray()

        private val cameraPermissions = arrayOf(
            Manifest.permission.CAMERA,
        )

        private val internetPermissions = arrayOf(
            Manifest.permission.INTERNET,
        )

        private val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        private val nfcPermissions = arrayOf(
            Manifest.permission.NFC,
        )

        private val rilPermissions = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.READ_PHONE_STATE)
            }
        }.toTypedArray()

        private val sensorsPermissions = buildList {
            add(Manifest.permission.BODY_SENSORS)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Manifest.permission.HIGH_SAMPLING_RATE_SENSORS
            }
        }.toTypedArray()

        private val uwbPermissions = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.UWB_RANGING)
            }
        }.toTypedArray()

        private val wifiPermissions = arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
        )

        private fun Permission.toAndroidPermissions() = when (this) {
            Permission.BIOMETRICS -> biometricsPermissions
            Permission.BLUETOOTH -> bluetoothPermissions
            Permission.CAMERA -> cameraPermissions
            Permission.INTERNET -> internetPermissions
            Permission.LOCATION -> locationPermissions
            Permission.NFC -> nfcPermissions
            Permission.RIL -> rilPermissions
            Permission.SENSORS -> sensorsPermissions
            Permission.UWB -> uwbPermissions
            Permission.WIFI -> wifiPermissions
        }
    }
}
