/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import androidx.annotation.StringRes
import dev.sebaubuntu.athena.R

@get:StringRes
val Boolean?.stringRes: Int
    get() = when (this) {
        true -> R.string.yes
        false -> R.string.no
        else -> R.string.unknown
    }
