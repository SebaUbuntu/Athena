/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import dev.sebaubuntu.athena.ui.AthenaApp
import dev.sebaubuntu.athena.ui.LocalPermissionsManager
import dev.sebaubuntu.athena.utils.PermissionsManager

class MainActivity : ComponentActivity() {
    private val permissionsManager = PermissionsManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge
        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
                AthenaApp()
            }
        }
    }
}
