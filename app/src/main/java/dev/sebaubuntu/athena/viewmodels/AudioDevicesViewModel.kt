/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import android.media.AudioManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.athena.ext.applicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AudioDevicesViewModel(application: Application) : AndroidViewModel(application) {
    private val audioManager by lazy { applicationContext.getSystemService(AudioManager::class.java) }

    val audioDevices = flow {
        emit(
            listOf(
                audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS),
                audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS),
            )
        )
    }
        .map { audioDevices ->
            audioDevices.flatMap { it.toList() }.distinctBy { it.id }.sortedBy { it.id }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf()
        )
}
