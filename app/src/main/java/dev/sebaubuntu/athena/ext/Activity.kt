/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import dev.sebaubuntu.athena.models.PermissionState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun Activity.permissionState(permission: String) = when (permissionGranted(permission)) {
    true -> PermissionState.GRANTED
    false -> when (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
        true -> PermissionState.NOT_GRANTED
        false -> PermissionState.NOT_GRANTED // TODO
    }
}

fun Activity.permissionsState(
    permissions: Array<String>,
) = permissions.associateWith(::permissionState)

fun Activity.permissionsStateFlow(
    lifecycle: Lifecycle,
    permissions: Array<String>
) = lifecycle.eventFlow(Lifecycle.Event.ON_RESUME)
    .onStart { emit(Unit) }
    .map { permissionsState(permissions) }
