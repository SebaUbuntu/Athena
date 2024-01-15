/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.media.MediaCodecList
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.recyclerview.MediaCodecsAdapter
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import kotlinx.coroutines.launch

class MediaFragment : Fragment(R.layout.fragment_media) {
    // Views
    private val mediaCodecsRecyclerView by getViewProperty<RecyclerView>(R.id.mediaCodecsRecyclerView)

    // Recyclerview
    private val mediaCodecsAdapter by lazy { MediaCodecsAdapter() }
    private val pairLayoutManager by lazy { PairLayoutManager(requireContext()) }

    // System services
    private val mediaCodecList by lazy { MediaCodecList(MediaCodecList.ALL_CODECS) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaCodecsRecyclerView.adapter = mediaCodecsAdapter
        mediaCodecsRecyclerView.layoutManager = pairLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            mediaCodecsAdapter.submitList(mediaCodecList.codecInfos.toMutableList())
        }
    }
}
