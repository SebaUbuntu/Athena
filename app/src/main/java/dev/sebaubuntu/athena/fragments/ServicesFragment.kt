/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.sebaubuntu.athena.recyclerview.PairAdapter
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.viewmodels.ServicesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ServicesFragment : RecyclerViewFragment() {
    // View models
    private val model by viewModels<ServicesViewModel>()

    // Recyclerview
    private val pairAdapter by lazy { PairAdapter() }
    private val gridLayoutManager by lazy { PairLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = pairAdapter
        recyclerView.layoutManager = gridLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.services.collectLatest {
                    pairAdapter.submitList(it)
                }
            }
        }
    }
}
