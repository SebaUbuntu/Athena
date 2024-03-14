/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.athena.ext.context
import dev.sebaubuntu.athena.ext.thermalStatusFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class ThermalViewModel(application: Application) : AndroidViewModel(application) {
    private val powerManager by lazy { context.getSystemService(PowerManager::class.java) }

    @RequiresApi(Build.VERSION_CODES.Q)
    val thermalStatus = powerManager.thermalStatusFlow()
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PowerManager.THERMAL_STATUS_NONE
        )
}
