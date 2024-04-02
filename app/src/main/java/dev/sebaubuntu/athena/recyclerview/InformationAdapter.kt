/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import androidx.recyclerview.widget.DiffUtil
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.ui.views.ListItem

class InformationAdapter : SimpleListAdapter<Information, ListItem>(
    diffCallback, ListItem::class.java
) {
    override fun ViewHolder.onBindView(item: Information) {
        view.headlineText = item.getDisplayTitle(view.context)
        view.supportingText = item.getDisplayValue(view.context)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Information>() {
            override fun areItemsTheSame(
                oldItem: Information,
                newItem: Information
            ) = oldItem.name == newItem.name

            override fun areContentsTheSame(
                oldItem: Information,
                newItem: Information
            ) = oldItem.title == newItem.title && oldItem.value == newItem.value
        }
    }
}
