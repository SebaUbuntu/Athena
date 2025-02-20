/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.media.MediaCodecInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.recyclerview.SimpleListAdapter
import dev.sebaubuntu.athena.ui.dialogs.MediaCodecInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.viewmodels.MediaCodecsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MediaFragment : RecyclerViewFragment() {
    // View models
    private val model: MediaCodecsViewModel by viewModels()

    // Recyclerview
    private val mediaCodecsAdapter by lazy {
        object : SimpleListAdapter<MediaCodecInfo, ListItem>(
            diffCallback, ::ListItem
        ) {
            override fun ViewHolder.onPrepareView() {
                view.setLeadingIconImage(R.drawable.ic_video_settings)
                view.setTrailingIconImage(R.drawable.ic_arrow_right)
                view.setOnClickListener {
                    item?.let {
                        MediaCodecInfoAlertDialog(view.context, it).show()
                    }
                }
            }

            override fun ViewHolder.onBindView(item: MediaCodecInfo) {
                view.headlineText = item.name
                view.supportingText = item.supportedTypes.joinToString()
            }


        }
    }
    private val pairLayoutManager by lazy { PairLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = mediaCodecsAdapter
        recyclerView.layoutManager = pairLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.mediaCodecs.collectLatest {
                    mediaCodecsAdapter.submitList(it)
                }
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MediaCodecInfo>() {
            override fun areItemsTheSame(
                oldItem: MediaCodecInfo,
                newItem: MediaCodecInfo
            ) = oldItem.name == newItem.name

            override fun areContentsTheSame(
                oldItem: MediaCodecInfo,
                newItem: MediaCodecInfo
            ) = oldItem.name == newItem.name
        }
    }
}
