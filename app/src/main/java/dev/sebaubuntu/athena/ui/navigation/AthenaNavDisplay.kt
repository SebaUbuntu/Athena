/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.sebaubuntu.athena.models.NavDestination
import dev.sebaubuntu.athena.ui.LocalNavigationBackStack
import dev.sebaubuntu.athena.ui.screens.ResourceScreen
import dev.sebaubuntu.athena.ui.screens.SettingsScreen

@Composable
fun AthenaNavDisplay(
    paddingValues: PaddingValues,
) {
    val navigationBackStack = LocalNavigationBackStack.current

    val entryProvider = entryProvider {
        entry<NavDestination.Resource> {
            ResourceScreen(
                resourceIdentifier = it.identifier,
                paddingValues = paddingValues,
                onNavigateTo = { identifier ->
                    navigationBackStack.add(NavDestination.Resource(identifier))
                },
                onBack = navigationBackStack::removeLastOrNull,
            )
        }

        entry<NavDestination.Settings> {
            SettingsScreen(
                paddingValues = paddingValues,
            )
        }
    }

    NavDisplay(
        backStack = navigationBackStack,
        entryProvider = entryProvider,
    )
}
