/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import androidx.activity.ComponentActivity

fun ComponentActivity.permissionsStateFlow(
    permissions: Array<String>,
) = permissionsStateFlow(lifecycle, permissions)
