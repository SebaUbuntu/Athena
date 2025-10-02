/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.ui.screens.ResourceScreen

@Composable
fun AthenaNavDisplay(
    backStack: SnapshotStateList<Resource.Identifier>,
    paddingValues: PaddingValues,
) {
    val entryProvider = { key: Resource.Identifier ->
        NavEntry(key) {
            ResourceScreen(
                resourceIdentifier = key,
                paddingValues = paddingValues,
                onNavigateTo = backStack::add,
                onBack = backStack::removeLastOrNull,
            )
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider,
    )
}
