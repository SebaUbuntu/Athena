/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.models

import android.content.Context
import androidx.annotation.StringRes

/**
 * Localized string.
 */
sealed interface LocalizedString {
    data class SimpleString(val value: String) : LocalizedString {
        override fun getString(context: Context) = value
    }

    class StringResource(
        @StringRes val stringResId: Int,
        val args: Array<out Any?>? = null,
    ) : LocalizedString {
        override fun getString(context: Context) = args?.let {
            context.getString(stringResId, *it)
        } ?: context.getString(stringResId)
    }

    /**
     * Get the string representation of the localized string.
     */
    fun getString(context: Context): String

    companion object {
        operator fun invoke(value: String) = SimpleString(value)

        operator fun invoke(@StringRes stringResId: Int) = StringResource(stringResId)

        operator fun invoke(
            @StringRes stringResId: Int,
            vararg args: Any?,
        ) = StringResource(stringResId, args)
    }
}
