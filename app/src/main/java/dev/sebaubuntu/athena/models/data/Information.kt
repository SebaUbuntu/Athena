/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.data

import androidx.annotation.StringRes
import dev.sebaubuntu.athena.core.models.Value
import kotlinx.serialization.Transient
import kotlin.reflect.safeCast

data class Information<T>(
    /**
     * Name of the information in snake_case.
     */
    val name: String,

    /**
     * The value of the information.
     */
    val value: Value<T>?,

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
    override fun equals(other: Any?) = Information::class.safeCast(other)?.let {
        name == it.name && value == it.value
    } ?: false

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}
