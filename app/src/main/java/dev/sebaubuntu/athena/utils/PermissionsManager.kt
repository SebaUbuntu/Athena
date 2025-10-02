/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import dev.sebaubuntu.athena.ext.permissionsState
import dev.sebaubuntu.athena.ext.permissionsStateFlow
import dev.sebaubuntu.athena.ext.registerForSuspendActivityResult
import dev.sebaubuntu.athena.models.PermissionState
import kotlinx.coroutines.flow.Flow

class PermissionsManager(private val activity: ComponentActivity) {
    /**
     * [ActivityResultLauncher] used to request permissions.
     */
    private val suspendActivityResultLauncher = activity.registerForSuspendActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.isNotEmpty()) {
            activity.permissionsState(it.keys.toTypedArray())
        } else {
            error("Got empty permissions result")
        }
    }

    /**
     * Get the state of the given permissions.
     *
     * @param permissions The permissions to check
     * @return The state of the given permissions
     */
    fun permissionsState(permissions: Array<String>) = activity.permissionsState(permissions)

    /**
     * Get a [Flow] of the state of the given permissions.
     *
     * @param permissions The permissions to check
     * @return A [Flow] of the state of the given permissions
     */
    fun permissionsStateFlow(
        permissions: Array<String>,
    ) = activity.permissionsStateFlow(permissions)

    /**
     * Request the given permissions.
     *
     * @param permissions The permissions to request
     * @return The state of the given permissions
     */
    suspend fun requestPermissions(
        permissions: Array<String>
    ) = permissionsState(permissions).let { permissionsState ->
        when (permissionsState.all { it.value == PermissionState.GRANTED }) {
            true -> permissionsState
            false -> suspendActivityResultLauncher.launch(permissions)
        }
    }
}
