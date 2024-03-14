/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.recyclerview.TrebleInterfacesAdapter
import dev.sebaubuntu.athena.viewmodels.TrebleInterfacesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TrebleInterfacesFragment : RecyclerViewFragment() {
    // Views
    private val model: TrebleInterfacesViewModel by viewModels()

    // Recyclerview
    private val trebleInterfacesAdapter by lazy { TrebleInterfacesAdapter() }
    private val gridLayoutManager by lazy { PairLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = trebleInterfacesAdapter
        recyclerView.layoutManager = gridLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.trebleInterfaces.collectLatest {
                    trebleInterfacesAdapter.submitList(it)
                }
            }
        }
    }
}
