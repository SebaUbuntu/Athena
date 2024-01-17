/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.fragments.SectionFragment
import dev.sebaubuntu.athena.sections.SectionEnum
import dev.sebaubuntu.athena.ui.views.ListItem

class SectionButtonsAdapter(
    private val fragment: Fragment
) : ListAdapter<SectionEnum, SectionButtonsAdapter.SectionButtonViewHolder>(ITEM_DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SectionButtonViewHolder(
        ListItem(parent.context),
        fragment
    )

    override fun onBindViewHolder(holder: SectionButtonViewHolder, position: Int) {
        holder.setSection(getItem(position))
    }

    class SectionButtonViewHolder(
        itemView: View,
        private val fragment: Fragment
    ) : RecyclerView.ViewHolder(itemView) {
        private val button = itemView as ListItem

        fun setSection(sectionEnum: SectionEnum) {
            val section = sectionEnum.clazz

            button.leadingIconImage =
                ResourcesCompat.getDrawable(fragment.resources, section.icon, null)
            button.headlineText = fragment.resources.getString(section.name)
            button.supportingText = fragment.resources.getString(section.description)
            button.setOnClickListener {
                section.navigationActionId?.also {
                    fragment.findNavController().navigate(it)
                } ?: fragment.findNavController().navigate(
                    R.id.action_mainFragment_to_sectionFragment,
                    SectionFragment.createBundle(sectionEnum)
                )
            }
        }
    }

    companion object {
        private val ITEM_DIFF = object : DiffUtil.ItemCallback<SectionEnum>() {
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
