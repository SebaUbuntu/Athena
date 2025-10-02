/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun Context.batteryStatusFlow() = callbackFlow {
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                trySend(it)
            }
        }
    }

    registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

    awaitClose {
        unregisterReceiver(broadcastReceiver)
    }
}

fun Context.permissionGranted(
    permission: String
) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.permissionsGranted(permissions: Array<String>) = permissions.all {
    permissionGranted(it)
}

fun Context.permissionsGrantedFlow(
    lifecycle: Lifecycle,
    permissions: Array<String>,
) = lifecycle.eventFlow(Lifecycle.Event.ON_RESUME)
    .onStart { emit(Unit) }
    .map { permissionsGranted(permissions) }
