/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.camera.ext

import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun CameraManager.cameraIdsFlow() = callbackFlow {
    val onCameraIdUpdated = {
        trySend(cameraIdList.toList())
    }

    val availabilityCallback = object : CameraManager.AvailabilityCallback() {
        override fun onCameraAvailable(cameraId: String) {
            onCameraIdUpdated()
        }

        override fun onCameraUnavailable(cameraId: String) {
            onCameraIdUpdated()
        }
    }

    registerAvailabilityCallback(
        availabilityCallback, Handler(Looper.getMainLooper())
    )

    onCameraIdUpdated()

    awaitClose {
        unregisterAvailabilityCallback(availabilityCallback)
    }
}
