/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import androidx.recyclerview.widget.DiffUtil
import dev.sebaubuntu.athena.models.data.Subsection
import dev.sebaubuntu.athena.ui.views.SubsectionLayout

class SubsectionAdapter : SimpleListAdapter<Subsection, SubsectionLayout>(
    diffCallback, SubsectionLayout::class.java
) {
    override fun ViewHolder.onBindView(item: Subsection) {
        view.subsection = item
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Subsection>() {
            override fun areItemsTheSame(
                oldItem: Subsection,
                newItem: Subsection
            ) = oldItem.name == newItem.name

            override fun areContentsTheSame(
                oldItem: Subsection,
                newItem: Subsection
            ) = oldItem.title == newItem.title && oldItem.information.toTypedArray().contentEquals(
                newItem.information.toTypedArray()
            )
        }
    }
}
