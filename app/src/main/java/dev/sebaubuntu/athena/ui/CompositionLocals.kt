/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import dev.sebaubuntu.athena.models.NavDestination
import dev.sebaubuntu.athena.utils.PermissionsManager

val LocalNavigationBackStack: CompositionLocal<SnapshotStateList<NavDestination>> =
    staticCompositionLocalOf {
        mutableStateListOf(NavDestination.DEFAULT)
    }

val LocalPermissionsManager = staticCompositionLocalOf<PermissionsManager> {
    error("No permissions manager")
}

val LocalSnackbarHostState: CompositionLocal<SnackbarHostState> = compositionLocalOf(
    defaultFactory = ::SnackbarHostState
)
