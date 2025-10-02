/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * App theme.
 */
@Composable
fun AthenaTheme(content: @Composable () -> Unit) {
    val colorScheme = when (isSystemInDarkTheme()) {
        true -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicDarkColorScheme(LocalContext.current)
        } else {
            darkColorScheme()
        }

        else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicLightColorScheme(LocalContext.current)
        } else {
            lightColorScheme()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
