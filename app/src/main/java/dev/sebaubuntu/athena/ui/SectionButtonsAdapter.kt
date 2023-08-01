/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.sections.Section

class SectionButtonsAdapter(
    private val fragment: Fragment
) : RecyclerView.Adapter<SectionButtonsAdapter.SectionButtonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SectionButtonViewHolder(
        ListItem(parent.context),
        fragment
    )

    override fun getItemCount() = Section.sections.count()

    override fun onBindViewHolder(holder: SectionButtonViewHolder, position: Int) {
        holder.setSection(position)
    }

    class SectionButtonViewHolder(
        itemView: View,
        private val fragment: Fragment
    ) : RecyclerView.ViewHolder(itemView) {
        private val button = itemView as ListItem

        fun setSection(sectionId: Int) {
            val section = Section.sections[sectionId]!!

            button.leadingIconImage = ResourcesCompat.getDrawable(fragment.resources, section.icon, null)
            button.headlineText = fragment.resources.getString(section.name)
            button.supportingText = fragment.resources.getString(section.description)
            button.showDivider = false
            button.setOnClickListener {
                fragment.findNavController().navigate(
                    R.id.action_mainFragment_to_sectionFragment,
                    SectionFragment.createBundle(sectionId)
                )
            }
        }
    }
}
