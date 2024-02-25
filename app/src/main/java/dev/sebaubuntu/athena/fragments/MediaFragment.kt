/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.media.MediaCodecList
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import dev.sebaubuntu.athena.recyclerview.MediaCodecsAdapter
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaFragment : RecyclerViewFragment() {
    // Recyclerview
    private val mediaCodecsAdapter by lazy { MediaCodecsAdapter() }
    private val pairLayoutManager by lazy { PairLayoutManager(requireContext()) }

    // System services
    private val mediaCodecList by lazy { MediaCodecList(MediaCodecList.ALL_CODECS) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = mediaCodecsAdapter
        recyclerView.layoutManager = pairLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            val codecInfos = withContext(Dispatchers.IO) {
                mediaCodecList.codecInfos.toMutableList()
            }
            mediaCodecsAdapter.submitList(codecInfos)
        }
    }
}
