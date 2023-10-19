/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.recyclerview.PairAdapter
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.viewmodels.PropsViewModel
import kotlinx.coroutines.launch

class PropsFragment : Fragment(R.layout.fragment_props) {
    // View models
    private val model by viewModels<PropsViewModel>()

    // Views
    private val propsRecyclerView by getViewProperty<RecyclerView>(R.id.propsRecyclerView)

    // Recyclerview
    private val pairAdapter by lazy { PairAdapter() }
    private val gridLayoutManager by lazy { PairLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        propsRecyclerView.adapter = pairAdapter
        propsRecyclerView.layoutManager = gridLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            val props = model.props.toList()
            pairAdapter.submitList(props)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        propsRecyclerView.adapter = null
        propsRecyclerView.layoutManager = null
    }
}
