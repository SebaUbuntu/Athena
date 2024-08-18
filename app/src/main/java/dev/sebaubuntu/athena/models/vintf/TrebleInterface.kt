/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.vintf

import androidx.annotation.StringRes

interface TrebleInterface {
    val name: String

    @get:StringRes
    val interfaceTypeStringResId: Int
}
