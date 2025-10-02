/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.athena.core.models.FlowResult
import dev.sebaubuntu.athena.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.athena.core.models.FlowResult.Companion.mapLatestData
import dev.sebaubuntu.athena.core.models.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

class ResourceViewModel @JvmOverloads constructor(
    application: Application,
    resourceIdentifier: Resource.Identifier? = null,
) : AthenaViewModel(application) {
    private val resourceIdentifier = MutableStateFlow<Resource.Identifier?>(resourceIdentifier)

    @OptIn(ExperimentalCoroutinesApi::class)
    val requiredPermissions = this.resourceIdentifier
        .filterNotNull()
        .mapLatest { resourceIdentifier ->
            componentsManager.requiredPermissions(resourceIdentifier)
        }
        .asFlowResult()
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val resource = this.resourceIdentifier
        .filterNotNull()
        .flatMapLatest { resourceIdentifier ->
            componentsManager.resolve(resourceIdentifier).asFlowResult()
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    fun setResourceIdentifier(resourceIdentifier: Resource.Identifier) {
        this.resourceIdentifier.value = resourceIdentifier
    }
}
