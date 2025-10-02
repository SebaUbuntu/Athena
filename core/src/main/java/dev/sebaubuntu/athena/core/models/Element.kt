/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.models

import androidx.annotation.DrawableRes

/**
 * An element.
 */
sealed interface Element : Resource {
    /**
     * An item in a list.
     *
     * @param drawableResId The drawable resource ID
     * @param value The [Value] of the item
     */
    data class Item(
        override val identifier: Resource.Identifier,
        override val title: LocalizedString,
        override val isNavigable: Boolean = false,
        @DrawableRes val drawableResId: Int? = null,
        val value: Value<*>? = null,
    ) : Element

    /**
     * A card holding multiple items.
     */
    data class Card(
        override val identifier: Resource.Identifier,
        override val title: LocalizedString,
        override val isNavigable: Boolean = false,
        val elements: List<Item>,
    ) : Element

    /**
     * The main title of the element.
     */
    val title: LocalizedString

    /**
     * Whether the [identifier] is navigable.
     */
    val isNavigable: Boolean
}
