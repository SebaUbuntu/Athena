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
import dev.sebaubuntu.athena.repositories.PreferencesRepository
import dev.sebaubuntu.athena.serialization.ResourcesSerializer
import dev.sebaubuntu.athena.utils.PermissionsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.FileWriter

class SettingsViewModel(
    application: Application,
    private val permissionsManager: PermissionsManager,
) : AthenaViewModel(application) {
    sealed interface ExportDataStatus {
        data object Processing : ExportDataStatus
        data object PermissionsNotGranted : ExportDataStatus
        data class Done(val result: Result<Uri, Error>) : ExportDataStatus
    }

    // General
    val theme = preferencesRepository.theme
    val dynamicColors = preferencesRepository.dynamicColors

    // Export data
    private val _exportDataStatus = MutableSharedFlow<ExportDataStatus?>()
    val exportDataStatus = _exportDataStatus.asSharedFlow()

    fun <T> setPreferenceValue(
        preference: PreferencesRepository.PreferenceHolder<T>,
        value: T,
    ) = viewModelScope.launch(Dispatchers.IO) {
        preference.setValue(value)
    }

    fun exportData(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        _exportDataStatus.emit(ExportDataStatus.Processing)

        // Check permissions
        val allRequiredPermissionsStatus = permissionsManager.requestPermissions(
            modulesManager.allRequiredPermissions
        )
        if (allRequiredPermissionsStatus.any { it.value != PermissionState.GRANTED }) {
            _exportDataStatus.emit(ExportDataStatus.PermissionsNotGranted)
            return@launch
        }

        // Write the file
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

        _exportDataStatus.emit(ExportDataStatus.Done(result))
    }
}
