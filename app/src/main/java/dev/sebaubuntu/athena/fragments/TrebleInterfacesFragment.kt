/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.recyclerview.TrebleInterfacesAdapter
import dev.sebaubuntu.athena.vintf.VINTFUtils
import kotlinx.coroutines.launch

class TrebleInterfacesFragment : Fragment(R.layout.fragment_treble_interfaces) {
    // Views
    private val interfacesRecyclerView by getViewProperty<RecyclerView>(R.id.interfacesRecyclerView)

    // Recyclerview
    private val trebleInterfacesAdapter by lazy { TrebleInterfacesAdapter() }
    private val gridLayoutManager by lazy { PairLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        interfacesRecyclerView.adapter = trebleInterfacesAdapter
        interfacesRecyclerView.layoutManager = gridLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            val interfaces = VINTFUtils.halInterfaces.sortedBy { it.name }
            trebleInterfacesAdapter.submitList(interfaces)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        interfacesRecyclerView.adapter = null
        interfacesRecyclerView.layoutManager = null
    }
}
