/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.sebaubuntu.athena.core.models.Value

/**
 * @see Value.getDisplayValue
 */
@Composable
fun <T> Value<T>.getDisplayValue() = getDisplayValue(LocalContext.current)
