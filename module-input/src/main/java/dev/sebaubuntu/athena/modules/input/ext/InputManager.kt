/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.input.ext

import android.hardware.input.InputManager
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun InputManager.inputDevicesFlow() = callbackFlow {
    val onInputDeviceUpdated = {
        trySend(
            inputDeviceIds.toList().mapNotNull(::getInputDevice)
        )
    }

    val listener = object : InputManager.InputDeviceListener {
        override fun onInputDeviceAdded(deviceId: Int) {
            onInputDeviceUpdated()
        }

        override fun onInputDeviceRemoved(deviceId: Int) {
            onInputDeviceUpdated()
        }

        override fun onInputDeviceChanged(deviceId: Int) {
            onInputDeviceUpdated()
        }
    }

    registerInputDeviceListener(listener, Handler(Looper.getMainLooper()))

    onInputDeviceUpdated()

    awaitClose {
        unregisterInputDeviceListener(listener)
    }
}

fun InputManager.inputDeviceFlow(deviceId: Int) = callbackFlow {
    val onInputDeviceUpdated = { updatedDeviceId: Int ->
        if (updatedDeviceId == deviceId) {
            trySend(getInputDevice(deviceId))
        }
    }

    val listener = object : InputManager.InputDeviceListener {
        override fun onInputDeviceAdded(deviceId: Int) {
            onInputDeviceUpdated(deviceId)
        }

        override fun onInputDeviceRemoved(deviceId: Int) {
            onInputDeviceUpdated(deviceId)
        }

        override fun onInputDeviceChanged(deviceId: Int) {
            onInputDeviceUpdated(deviceId)
        }
    }

    registerInputDeviceListener(listener, Handler(Looper.getMainLooper()))

    onInputDeviceUpdated(deviceId)

    awaitClose {
        unregisterInputDeviceListener(listener)
    }
}
