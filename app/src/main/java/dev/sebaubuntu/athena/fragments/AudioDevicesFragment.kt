/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.media.AudioManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.*
import dev.sebaubuntu.athena.recyclerview.AudioDevicesAdapter
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import kotlinx.coroutines.launch

class AudioDevicesFragment : Fragment(R.layout.fragment_audio_devices) {
    // Views
    private val audioDevicesRecyclerView by getViewProperty<RecyclerView>(R.id.audioDevicesRecyclerView)

    // Recyclerview
    private val audioDevicesAdapter by lazy { AudioDevicesAdapter() }
    private val pairLayoutManager by lazy { PairLayoutManager(requireContext()) }

    // System services
    private val audioManager by lazy {
        requireContext().getSystemService(AudioManager::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioDevicesRecyclerView.adapter = audioDevicesAdapter
        audioDevicesRecyclerView.layoutManager = pairLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            val devices = listOf(
                audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS),
                audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS),
            ).flatMap { it.toList() }.distinctBy { it.id }.sortedBy { it.id }

            audioDevicesAdapter.submitList(devices)
        }
    }
}
