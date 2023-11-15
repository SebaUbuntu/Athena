/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.ui.ListItem

class PairAdapter : ListAdapter<Pair<String, String>, PairAdapter.PairViewHolder>(
    PAIR_COMPARATOR
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PairViewHolder(ListItem(parent.context))

    override fun onBindViewHolder(holder: PairViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PairViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItem = itemView as ListItem

        fun bind(prop: Pair<String, String>) {
            listItem.headlineText = prop.first
            listItem.supportingText = prop.second
        }
    }

    companion object {
        val PAIR_COMPARATOR = object : DiffUtil.ItemCallback<Pair<String, String>>() {
            override fun areItemsTheSame(
                oldItem: Pair<String, String>,
                newItem: Pair<String, String>
            ) = oldItem.first == newItem.first

            override fun areContentsTheSame(
                oldItem: Pair<String, String>,
                newItem: Pair<String, String>
            ) = oldItem.second == newItem.second
        }
    }
}
