/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import android.media.AudioDeviceInfo
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ui.dialogs.AudioDeviceInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.utils.AudioDeviceInfoUtils

class AudioDevicesAdapter :
    ListAdapter<AudioDeviceInfo, AudioDevicesAdapter.AudioDeviceViewHolder>(PAIR_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AudioDeviceViewHolder(ListItem(parent.context))

    override fun onBindViewHolder(holder: AudioDeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AudioDeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItem = itemView as ListItem

        private var audioDeviceInfo: AudioDeviceInfo? = null

        init {
            listItem.trailingIconImage = ResourcesCompat.getDrawable(
                listItem.resources, R.drawable.ic_arrow_right, null
            )
            listItem.setOnClickListener {
                audioDeviceInfo?.let {
                    AudioDeviceInfoAlertDialog(listItem.context, it).show()
                }
            }
        }

        fun bind(audioDeviceInfo: AudioDeviceInfo) {
            this.audioDeviceInfo = audioDeviceInfo

            val type = audioDeviceInfo.type

            AudioDeviceInfoUtils.deviceTypeToStringRes[type]?.also {
                listItem.setHeadlineText(it)
            } ?: run {
                listItem.setHeadlineText(R.string.audio_device_type_seriously_unknown, type)
            }

            val isSink = audioDeviceInfo.isSink
            val isSource = audioDeviceInfo.isSource

            listItem.setSupportingText(
                when {
                    isSink && isSource -> R.string.audio_role_sink_and_source
                    isSink -> R.string.audio_role_sink
                    isSource -> R.string.audio_role_source
                    else -> R.string.unknown
                }
            )

            listItem.setLeadingIconImage(
                AudioDeviceInfoUtils.deviceTypeToDrawableRes[type] ?: R.drawable.ic_question_mark
            )
        }
    }

    companion object {
        val PAIR_COMPARATOR = object : DiffUtil.ItemCallback<AudioDeviceInfo>() {
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
