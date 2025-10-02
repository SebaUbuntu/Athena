/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena

import android.app.Application
import com.google.android.material.color.DynamicColors
import dev.sebaubuntu.athena.utils.ModulesManager

class AthenaApplication : Application() {
    val modulesManager by lazy { ModulesManager(this) }

    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
