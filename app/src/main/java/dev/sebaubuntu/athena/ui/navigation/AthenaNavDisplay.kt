/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.sebaubuntu.athena.models.NavDestination
import dev.sebaubuntu.athena.ui.screens.ResourceScreen
import dev.sebaubuntu.athena.ui.screens.SerializationScreen

@Composable
fun AthenaNavDisplay(
    backStack: SnapshotStateList<NavDestination>,
    paddingValues: PaddingValues,
) {
    val entryProvider = entryProvider {
        entry<NavDestination.Resource> {
            ResourceScreen(
                resourceIdentifier = it.identifier,
                paddingValues = paddingValues,
                onNavigateTo = { identifier ->
                    backStack.add(NavDestination.Resource(identifier))
                },
                onBack = backStack::removeLastOrNull,
            )
        }

        entry<NavDestination.Serialization> {
            SerializationScreen(
                paddingValues = paddingValues,
            )
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider,
    )
}
