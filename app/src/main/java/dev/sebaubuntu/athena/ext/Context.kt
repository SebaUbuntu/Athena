/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

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
