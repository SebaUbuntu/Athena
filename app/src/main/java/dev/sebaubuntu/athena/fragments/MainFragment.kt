/*
 * SPDX-FileCopyrightText: 2023-2025 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.recyclerview.SimpleListAdapter
import dev.sebaubuntu.athena.sections.SectionEnum
import dev.sebaubuntu.athena.ui.views.ListItem

class MainFragment : RecyclerViewFragment() {
    private val sectionsAdapter by lazy {
        object : SimpleListAdapter<SectionEnum, ListItem>(
            diffCallback, ::ListItem
        ) {
            var onSectionClicked: (sectionEnum: SectionEnum) -> Unit = {}

            override fun ViewHolder.onPrepareView() {
                view.setOnClickListener {
                    item?.let { sectionEnum ->
                        onSectionClicked(sectionEnum)
                    }
                }
            }

            override fun ViewHolder.onBindView(item: SectionEnum) {
                val section = item.clazz

                view.setLeadingIconImage(section.icon)
                view.setHeadlineText(section.title)
                view.setSupportingText(section.description)
            }
        }
    }
    private val gridLayoutManager by lazy { GridLayoutManager(requireContext(), GRID_SPAN_COUNT) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = sectionsAdapter
        recyclerView.layoutManager = gridLayoutManager

        sectionsAdapter.onSectionClicked = { sectionEnum ->
            sectionEnum.clazz.navigationActionId?.also {
                findNavController().navigate(it)
            } ?: findNavController().navigate(
                R.id.action_mainFragment_to_sectionFragment,
                SectionFragment.createBundle(sectionEnum)
            )
        }

        sectionsAdapter.submitList(SectionEnum.entries)
    }

    companion object {
        private const val GRID_SPAN_COUNT = 1

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
