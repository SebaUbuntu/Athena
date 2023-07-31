/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.*

class MainFragment : Fragment(R.layout.fragment_main) {
    // Views
    private val sectionsRecyclerView by getViewProperty<RecyclerView>(R.id.sectionsRecyclerView)

    private val sectionButtonsAdapter by lazy { SectionButtonsAdapter(this) }
    private val gridLayoutManager by lazy { GridLayoutManager(requireContext(), 2) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sectionsRecyclerView.adapter = sectionButtonsAdapter
        sectionsRecyclerView.layoutManager = gridLayoutManager
        sectionsRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(2, 16.dp, true)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        sectionsRecyclerView.layoutManager = null
        sectionsRecyclerView.adapter = null
    }
}
