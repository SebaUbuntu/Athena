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
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebaubuntu.athena.models.Theme
import dev.sebaubuntu.athena.viewmodels.ThemeViewModel

/**
 * App theme.
 */
@Composable
fun AthenaTheme(content: @Composable () -> Unit) {
    val viewModel = viewModel<ThemeViewModel>()

    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val dynamicColors by viewModel.dynamicColors.collectAsStateWithLifecycle()

    val colorScheme = when (theme) {
        Theme.SYSTEM -> when (isSystemInDarkTheme()) {
            true -> darkColorScheme(dynamicColors)
            false -> lightColorScheme(dynamicColors)
        }

        Theme.LIGHT -> lightColorScheme(dynamicColors)

        Theme.DARK -> darkColorScheme(dynamicColors)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

@Composable
private fun lightColorScheme(
    dynamicColors: Boolean,
) = if (dynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    dynamicLightColorScheme(LocalContext.current)
} else {
    lightColorScheme()
}

@Composable
private fun darkColorScheme(
    dynamicColors: Boolean,
) = if (dynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    dynamicDarkColorScheme(LocalContext.current)
} else {
    darkColorScheme()
}
