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
import dev.sebaubuntu.athena.viewmodels.ServicesViewModel
import kotlinx.coroutines.launch

class ServicesFragment : Fragment(R.layout.fragment_services) {
    // View models
    private val model by viewModels<ServicesViewModel>()

    // Views
    private val servicesRecyclerView by getViewProperty<RecyclerView>(R.id.servicesRecyclerView)

    // Recyclerview
    private val pairAdapter by lazy { PairAdapter() }
    private val gridLayoutManager by lazy { PairLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        servicesRecyclerView.adapter = pairAdapter
        servicesRecyclerView.layoutManager = gridLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            val services = model.services.toList()
            pairAdapter.submitList(services)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        servicesRecyclerView.adapter = null
        servicesRecyclerView.layoutManager = null
    }
}
