/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena

import android.app.Application
import com.google.android.material.color.DynamicColors
import dev.sebaubuntu.athena.core.components.ComponentsManager

class AthenaApplication : Application() {
    val componentsManager by lazy {
        ComponentsManager(this)
    }

    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)

        // Load native library
        System.loadLibrary("athena")

        // Start components manager
        componentsManager
    }
}
