/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.sections.Section

class SectionButtonsAdapter(
    private val fragment: Fragment
) : RecyclerView.Adapter<SectionButtonsAdapter.SectionButtonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SectionButtonViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.section_button_item, parent, false),
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
        private val button = itemView as MaterialButton

        fun setSection(sectionId: Int) {
            val section = Section.sections[sectionId]!!

            button.setText(section.name)
            button.setCompoundDrawablesWithIntrinsicBounds(
                section.icon, 0, 0, 0,
            )
            button.setOnClickListener {
                fragment.findNavController().navigate(
                    R.id.action_mainFragment_to_sectionFragment,
                    SectionFragment.createBundle(sectionId)
                )
            }
        }
    }
}
