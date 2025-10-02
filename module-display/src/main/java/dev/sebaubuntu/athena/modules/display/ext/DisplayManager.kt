/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.display.ext

import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import android.view.Display
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun DisplayManager.displaysFlow() = callbackFlow {
    val onDisplayUpdated = {
        trySend(displays.toList())
    }

    val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) {
            onDisplayUpdated()
        }

        override fun onDisplayRemoved(displayId: Int) {
            onDisplayUpdated()
        }

        override fun onDisplayChanged(displayId: Int) {
            onDisplayUpdated()
        }
    }

    registerDisplayListener(displayListener, Handler(Looper.getMainLooper()))

    onDisplayUpdated()

    awaitClose {
        unregisterDisplayListener(displayListener)
    }
}

fun DisplayManager.displayFlow(displayId: Int) = callbackFlow<Display?> {
    val onDisplayUpdated = { updatedDisplayId: Int ->
        if (updatedDisplayId == displayId) {
            trySend(getDisplay(displayId))
        }
    }

    val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) {
            onDisplayUpdated(displayId)
        }

        override fun onDisplayRemoved(displayId: Int) {
            onDisplayUpdated(displayId)
        }

        override fun onDisplayChanged(displayId: Int) {
            onDisplayUpdated(displayId)
        }
    }

    registerDisplayListener(displayListener, Handler(Looper.getMainLooper()))

    onDisplayUpdated(displayId)

    awaitClose {
        unregisterDisplayListener(displayListener)
    }
}
