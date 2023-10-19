/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class GpuViewModel(application: Application) : AndroidViewModel(application) {
    val gpuRenderer = MutableLiveData<String>()
    val gpuVendor = MutableLiveData<String>()
    val gpuVersion = MutableLiveData<String>()
    val gpuExtensions = MutableLiveData<String>()
}
