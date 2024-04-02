/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.data

import android.content.Context
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.R
import kotlinx.serialization.Transient
import kotlin.reflect.safeCast

data class Information(
    /**
     * Name of the information in snake_case.
     */
    val name: String,

    /**
     * The value of the information.
     */
    val value: InformationValue?,

    /**
     * A title string resource ID, can be null.
     */
    @Transient
    @StringRes
    val title: Int? = null,

    /**
     * The format arguments for the title.
     */
    @Transient
    val titleFormatArgs: Array<Any>? = null,
) {
    fun getDisplayTitle(context: Context) = title?.let {
        titleFormatArgs?.let { formatArgs ->
            context.getString(it, *formatArgs)
        } ?: context.getString(it)
    } ?: name

    fun getDisplayValue(
        context: Context
    ) = value?.getDisplayValue(context) ?: context.getString(R.string.unknown)

    override fun equals(other: Any?) = Information::class.safeCast(other)?.let { o ->
        name == o.name
                && value == o.value
                && title == o.title
                && titleFormatArgs?.let {
            o.titleFormatArgs?.let { oTitleFormatArgs ->
                titleFormatArgs.contentEquals(oTitleFormatArgs)
            } ?: false
        } ?: (o.titleFormatArgs == null)
    } ?: false

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + (title ?: 0)
        result = 31 * result + (titleFormatArgs?.contentHashCode() ?: 0)
        return result
    }
}
