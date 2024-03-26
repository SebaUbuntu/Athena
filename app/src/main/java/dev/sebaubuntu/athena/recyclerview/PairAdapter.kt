/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import androidx.recyclerview.widget.DiffUtil
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ui.views.ListItem

class PairAdapter : SimpleListAdapter<Pair<String, String?>, ListItem>(
    diffCallback, ListItem::class.java
) {
    override fun ViewHolder.onBindView(item: Pair<String, String?>) {
        view.headlineText = item.first
        item.second?.let {
            view.supportingText = item.second
        } ?: view.setSupportingText(R.string.unknown)
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Pair<String, String?>>() {
            override fun areItemsTheSame(
                oldItem: Pair<String, String?>,
                newItem: Pair<String, String?>
            ) = oldItem.first == newItem.first

            override fun areContentsTheSame(
                oldItem: Pair<String, String?>,
                newItem: Pair<String, String?>
            ) = oldItem.second == newItem.second
        }
    }
}
