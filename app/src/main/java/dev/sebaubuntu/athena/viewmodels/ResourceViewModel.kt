/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.athena.core.models.FlowResult
import dev.sebaubuntu.athena.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.athena.core.models.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class ResourceViewModel(
    application: Application,
    resourceIdentifier: Resource.Identifier,
) : AthenaViewModel(application) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val requiredPermissions = suspend {
        modulesManager.requiredPermissions(resourceIdentifier)
    }
        .asFlow()
        .asFlowResult()
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val resource = modulesManager.resolve(resourceIdentifier)
        .asFlowResult()
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )
}
