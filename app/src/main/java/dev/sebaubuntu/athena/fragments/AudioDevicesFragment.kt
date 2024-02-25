/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.media.AudioManager
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import dev.sebaubuntu.athena.recyclerview.AudioDevicesAdapter
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AudioDevicesFragment : RecyclerViewFragment() {
    // Recyclerview
    private val audioDevicesAdapter by lazy { AudioDevicesAdapter() }
    private val pairLayoutManager by lazy { PairLayoutManager(requireContext()) }

    // System services
    private val audioManager by lazy {
        requireContext().getSystemService(AudioManager::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = audioDevicesAdapter
        recyclerView.layoutManager = pairLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            val devices = withContext(Dispatchers.IO) {
                listOf(
                    audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS),
                    audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS),
                ).flatMap { it.toList() }.distinctBy { it.id }.sortedBy { it.id }
            }

            audioDevicesAdapter.submitList(devices)
        }
    }
}
