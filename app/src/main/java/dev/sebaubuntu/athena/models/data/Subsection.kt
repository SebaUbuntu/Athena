/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.data

import android.content.Context
import androidx.annotation.StringRes
import kotlinx.serialization.Transient
import kotlin.reflect.safeCast

data class Subsection(
    /**
     * Name of the subsection in snake_case.
     */
    val name: String,

    /**
     * The information regarding this subsection.
     */
    val information: List<Information>,

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
    override fun equals(other: Any?) = Subsection::class.safeCast(other)?.let { o ->
        name == o.name
                && information == o.information
                && title == o.title
                && titleFormatArgs?.let {
                    o.titleFormatArgs?.let { oTitleFormatArgs ->
                        titleFormatArgs.contentEquals(oTitleFormatArgs)
                    } ?: false
                } ?: (o.titleFormatArgs == null)
    } ?: false

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + information.hashCode()
        result = 31 * result + (title ?: 0)
        result = 31 * result + (titleFormatArgs?.contentHashCode() ?: 0)
        return result
    }

    fun getDisplayTitle(context: Context) = title?.let {
        titleFormatArgs?.let { formatArgs ->
            context.getString(it, *formatArgs)
        } ?: context.getString(it)
    } ?: name
}
