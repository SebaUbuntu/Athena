package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.athena.ext.context
import dev.sebaubuntu.athena.models.data.Section
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class SectionViewModel(application: Application) : AndroidViewModel(application) {
    val section = MutableLiveData<Section>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val sectionData = section.asFlow()
        .flatMapLatest {
            it.dataFlow(context)
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf(),
        )
}
