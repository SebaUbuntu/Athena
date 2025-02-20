/*
 * SPDX-FileCopyrightText: 2023-2025 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.media.AudioDeviceInfo
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
import dev.sebaubuntu.athena.ui.dialogs.AudioDeviceInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.utils.AudioDeviceInfoUtils
import dev.sebaubuntu.athena.viewmodels.AudioDevicesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AudioDevicesFragment : RecyclerViewFragment() {
    // View models
    private val model: AudioDevicesViewModel by viewModels()

    // Recyclerview
    private val audioDevicesAdapter by lazy {
        object : SimpleListAdapter<AudioDeviceInfo, ListItem>(
            diffCallback, ::ListItem
        ) {
            override fun ViewHolder.onPrepareView() {
                view.setTrailingIconImage(R.drawable.ic_arrow_right)
                view.setOnClickListener {
                    item?.let {
                        AudioDeviceInfoAlertDialog(view.context, it).show()
                    }
                }
            }

            override fun ViewHolder.onBindView(item: AudioDeviceInfo) {
                val type = item.type

                AudioDeviceInfoUtils.deviceTypeToStringRes[type]?.also {
                    view.setHeadlineText(it)
                } ?: run {
                    view.setHeadlineText(R.string.audio_device_type_seriously_unknown, type)
                }

                val isSink = item.isSink
                val isSource = item.isSource

                view.setSupportingText(
                    when {
                        isSink && isSource -> R.string.audio_role_sink_and_source
                        isSink -> R.string.audio_role_sink
                        isSource -> R.string.audio_role_source
                        else -> R.string.unknown
                    }
                )

                view.setLeadingIconImage(
                    AudioDeviceInfoUtils.deviceTypeToDrawableRes[
                        type
                    ] ?: R.drawable.ic_question_mark
                )
            }
        }
    }
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

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<AudioDeviceInfo>() {
            override fun areItemsTheSame(
                oldItem: AudioDeviceInfo,
                newItem: AudioDeviceInfo
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: AudioDeviceInfo,
                newItem: AudioDeviceInfo
            ) = oldItem == newItem
        }
    }
}
