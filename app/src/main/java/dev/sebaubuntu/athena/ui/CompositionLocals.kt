/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import androidx.compose.runtime.staticCompositionLocalOf
import dev.sebaubuntu.athena.utils.PermissionsManager

val LocalPermissionsManager = staticCompositionLocalOf<PermissionsManager> { error("No permissions manager") }
