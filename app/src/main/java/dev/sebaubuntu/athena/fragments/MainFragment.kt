/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.recyclerview.SectionsAdapter
import dev.sebaubuntu.athena.sections.SectionEnum

class MainFragment : RecyclerViewFragment() {
    private val sectionsAdapter by lazy { SectionsAdapter() }
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

        sectionsAdapter.submitList(SectionEnum.values().toList())
    }

    companion object {
        private const val GRID_SPAN_COUNT = 1
    }
}
