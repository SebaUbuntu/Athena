/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.models

import androidx.annotation.DrawableRes

/**
 * An element.
 */
sealed interface Element {
    /**
     * An item in a list.
     *
     * @param drawableResId The drawable resource ID
     * @param value The [Value] of the item
     */
    data class Item(
        override val name: String,
        override val title: LocalizedString,
        override val navigateTo: Resource.Identifier? = null,
        @DrawableRes val drawableResId: Int? = null,
        val value: Value<*>? = null,
    ) : Element

    /**
     * A card holding multiple items.
     */
    data class Card(
        override val name: String,
        override val title: LocalizedString,
        override val navigateTo: Resource.Identifier? = null,
        val elements: List<Item>,
    ) : Element

    /**
     * The name of the element.
     */
    val name: String

    /**
     * The main title of the element.
     */
    val title: LocalizedString

    /**
     * Whether clicking on this element should navigate to the specified [Resource.Identifier].
     */
    val navigateTo: Resource.Identifier?
}
