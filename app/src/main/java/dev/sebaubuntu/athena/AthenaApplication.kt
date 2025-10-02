/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena

import android.app.Application
import com.google.android.material.color.DynamicColors
import dev.sebaubuntu.athena.repositories.PreferencesRepository
import dev.sebaubuntu.athena.utils.ModulesManager
import dev.sebaubuntu.athena.utils.PreferencesManager
import kotlinx.coroutines.MainScope

class AthenaApplication : Application() {
    val coroutineScope = MainScope()

    val modulesManager by lazy { ModulesManager(this) }
    val preferencesManager by lazy { PreferencesManager.get(this) }

    // Repositories
    val preferencesRepository by lazy { PreferencesRepository(preferencesManager, coroutineScope) }

    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
