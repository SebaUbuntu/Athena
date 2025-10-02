/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.data

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

abstract class Section(
    val name: String,
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val icon: Int,
    val requiredPermissions: Array<String> = arrayOf(),
    @IdRes val navigationActionId: Int? = null
) {
    open fun dataFlow(context: Context): Flow<List<Subsection>?> = flowOf(null)
}
