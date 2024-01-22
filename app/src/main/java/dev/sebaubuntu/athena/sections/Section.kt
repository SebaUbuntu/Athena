/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

abstract class Section {
    @get:StringRes abstract val name: Int
    @get:StringRes abstract val description: Int
    @get:DrawableRes abstract val icon: Int

    @Deprecated(
        message = "Permissions should be checked in the section specific fragment",
    )
    open val requiredPermissions: Array<String> = arrayOf()

    @Deprecated(
        message = "Getting raw data is deprecated",
        replaceWith = ReplaceWith("navigationActionId"),
    )
    open fun getInfo(context: Context): Map<String, Map<String, String?>> = throw Exception()

    @IdRes
    open val navigationActionId: Int? = null
}
