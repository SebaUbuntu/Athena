/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun Context.permissionGranted(
    permission: String,
) = when (val permissionState = ContextCompat.checkSelfPermission(this, permission)) {
    PackageManager.PERMISSION_GRANTED -> true
    PackageManager.PERMISSION_DENIED -> false
    else -> error("Unknown permission state $permissionState")
}

fun Context.permissionsGranted(
    permissions: Array<String>,
) = permissions.associateWith(::permissionGranted)

fun Context.permissionsGrantedFlow(
    lifecycle: Lifecycle,
    permissions: Array<String>,
) = lifecycle.eventFlow(Lifecycle.Event.ON_RESUME)
    .onStart { emit(Unit) }
    .map { permissionsGranted(permissions) }
