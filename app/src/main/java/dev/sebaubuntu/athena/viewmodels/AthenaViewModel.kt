/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dev.sebaubuntu.athena.AthenaApplication

abstract class AthenaViewModel(application: Application) : AndroidViewModel(application) {
    protected val modulesManager = getApplication<AthenaApplication>().modulesManager
    protected val preferencesRepository = getApplication<AthenaApplication>().preferencesRepository
}
