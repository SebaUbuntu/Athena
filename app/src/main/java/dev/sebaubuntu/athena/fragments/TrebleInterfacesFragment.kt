/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.recyclerview.TrebleInterfacesAdapter
import dev.sebaubuntu.athena.vintf.VINTFUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrebleInterfacesFragment : RecyclerViewFragment() {
    // Recyclerview
    private val trebleInterfacesAdapter by lazy { TrebleInterfacesAdapter() }
    private val gridLayoutManager by lazy { PairLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = trebleInterfacesAdapter
        recyclerView.layoutManager = gridLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            val interfaces = withContext(Dispatchers.IO) {
                VINTFUtils.halInterfaces.sortedBy { it.name }
            }
            trebleInterfacesAdapter.submitList(interfaces)
        }
    }
}
