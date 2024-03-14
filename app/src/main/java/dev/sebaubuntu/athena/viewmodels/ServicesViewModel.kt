/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.athena.utils.SystemProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ServicesViewModel(application: Application) : AndroidViewModel(application) {
    val services = flow {
        emit(SystemProperties.props)
    }
        .map { props ->
            props.filterKeys {
                it.startsWith(INIT_SERVICE_PREFIX)
            }.map {
                it.key.removePrefix(INIT_SERVICE_PREFIX) to it.value
            }.sortedBy { it.first }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf()
        )

    companion object {
        private const val INIT_SERVICE_PREFIX = "init.svc."
    }
}
