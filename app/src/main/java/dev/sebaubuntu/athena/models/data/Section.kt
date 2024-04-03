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
import kotlinx.coroutines.flow.asFlow

abstract class Section {
    @get:StringRes
    abstract val title: Int

    @get:StringRes
    abstract val description: Int

    @get:DrawableRes
    abstract val icon: Int

    open val requiredPermissions: Array<String> = arrayOf()

    open fun getInfoOld(context: Context): Map<String, Map<String, String?>> = throw Exception()

    open fun dataFlow(context: Context): Flow<List<Subsection>> = {
        getInfoOld(context).map { subsection ->
            Subsection(
                subsection.key,
                subsection.value.map { information ->
                    Information(
                        information.key,
                        information.value?.let { InformationValue.StringValue(it) }
                    )
                }
            )
        }
    }.asFlow()

    @IdRes
    open val navigationActionId: Int? = null
}
