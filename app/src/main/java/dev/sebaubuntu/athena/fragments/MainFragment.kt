/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import dev.sebaubuntu.athena.recyclerview.SectionButtonsAdapter
import dev.sebaubuntu.athena.sections.SectionEnum

class MainFragment : RecyclerViewFragment() {
    private val sectionButtonsAdapter by lazy { SectionButtonsAdapter(this) }
    private val gridLayoutManager by lazy { GridLayoutManager(requireContext(), GRID_SPAN_COUNT) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = sectionButtonsAdapter
        recyclerView.layoutManager = gridLayoutManager

        sectionButtonsAdapter.submitList(SectionEnum.values().toList())
    }

    companion object {
        private const val GRID_SPAN_COUNT = 1
    }
}
