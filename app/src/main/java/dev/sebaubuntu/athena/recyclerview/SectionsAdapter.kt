/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import androidx.recyclerview.widget.DiffUtil
import dev.sebaubuntu.athena.sections.SectionEnum
import dev.sebaubuntu.athena.ui.views.ListItem

class SectionsAdapter : SimpleListAdapter<SectionEnum, ListItem>(
    diffCallback, ListItem::class.java
) {
    var onSectionClicked: (sectionEnum: SectionEnum) -> Unit = {}

    override fun SimpleListAdapter<SectionEnum, ListItem>.ViewHolder.onPrepareView() {
        view.setOnClickListener {
            item?.let { sectionEnum ->
                onSectionClicked(sectionEnum)
            }
        }
    }

    override fun ViewHolder.onBindView(item: SectionEnum) {
        val section = item.clazz

        view.setLeadingIconImage(section.icon)
        view.setHeadlineText(section.name)
        view.setSupportingText(section.description)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SectionEnum>() {
            override fun areItemsTheSame(
                oldItem: SectionEnum,
                newItem: SectionEnum
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: SectionEnum,
                newItem: SectionEnum
            ) = oldItem == newItem
        }
    }
}
