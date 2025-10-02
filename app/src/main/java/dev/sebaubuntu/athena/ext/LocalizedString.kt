/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.sebaubuntu.athena.core.models.LocalizedString

/**
 * @see LocalizedString.getString
 */
@Composable
fun LocalizedString.getString() = getString(LocalContext.current)
