/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.models

sealed interface Screen : Resource {
    /**
     * The main title of the screen.
     */
    val title: LocalizedString

    /**
     * A screen of a list of items.
     */
    data class ItemListScreen(
        override val identifier: Resource.Identifier,
        override val title: LocalizedString,
        val elements: List<Element.Item>,
    ) : Screen

    /**
     * A screen of a list of cards.
     */
    data class CardListScreen(
        override val identifier: Resource.Identifier,
        override val title: LocalizedString,
        val elements: List<Element.Card>,
    ) : Screen

    /**
     * A screen of a dialog.
     */
    data class DialogScreen(
        override val identifier: Resource.Identifier,
        override val title: LocalizedString,
        val elements: List<Element.Item>,
    ) : Screen
}
