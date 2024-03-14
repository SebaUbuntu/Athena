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
import dev.sebaubuntu.athena.recyclerview.AudioDevicesAdapter
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.viewmodels.AudioDevicesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AudioDevicesFragment : RecyclerViewFragment() {
    // View models
    private val model: AudioDevicesViewModel by viewModels()

    // Recyclerview
    private val audioDevicesAdapter by lazy { AudioDevicesAdapter() }
    private val pairLayoutManager by lazy { PairLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = audioDevicesAdapter
        recyclerView.layoutManager = pairLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.audioDevices.collectLatest {
                    audioDevicesAdapter.submitList(it)
                }
            }
        }
    }
}
