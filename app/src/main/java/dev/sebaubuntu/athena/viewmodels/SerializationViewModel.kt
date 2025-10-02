/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Result.Companion.flatMap
import dev.sebaubuntu.athena.ext.applicationContext
import dev.sebaubuntu.athena.models.PermissionState
import dev.sebaubuntu.athena.serialization.ResourcesSerializer
import dev.sebaubuntu.athena.utils.PermissionsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileWriter

class SerializationViewModel(
    application: Application,
    private val permissionsManager: PermissionsManager,
) : AthenaViewModel(application) {
    private val _serializationStatus = MutableStateFlow<Result<Uri, Error>?>(null)
    val serializationStatus = _serializationStatus.asStateFlow()

    fun serialize(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        // Acknowledge the previous serialization status
        acknowledgeSerializationStatus()

        // Make sure we have all permissions granted
        val allRequiredPermissionsState = permissionsManager.requestPermissions(
            modulesManager.allRequiredPermissions
        )
        if (allRequiredPermissionsState.any { it.value != PermissionState.GRANTED }) {
            return@launch
        }

        // Write the file
        withContext(Dispatchers.IO) {
            val result = ResourcesSerializer.serializeToJson(modulesManager).flatMap { jsonData ->
                applicationContext.contentResolver.openFileDescriptor(
                    uri, "wt"
                )?.use { parcelFileDescriptor ->
                    FileWriter(parcelFileDescriptor.fileDescriptor).use { writer ->
                        writer.write(jsonData)
                    }

                    Result.Success<Uri, Error>(uri)
                } ?: Result.Error(Error.IO)
            }

            _serializationStatus.value = result
        }
    }

    fun acknowledgeSerializationStatus() {
        _serializationStatus.value = null
    }
}
