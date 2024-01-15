/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import android.media.MediaCodecInfo
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ui.dialogs.MediaCodecInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem

class MediaCodecsAdapter :
    ListAdapter<MediaCodecInfo, MediaCodecsAdapter.AudioDeviceViewHolder>(PAIR_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AudioDeviceViewHolder(ListItem(parent.context))

    override fun onBindViewHolder(holder: AudioDeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AudioDeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItem = itemView as ListItem

        private var mediaCodecInfo: MediaCodecInfo? = null

        init {
            listItem.trailingIconImage = ResourcesCompat.getDrawable(
                listItem.resources, R.drawable.ic_arrow_right, null
            )
            listItem.setOnClickListener {
                mediaCodecInfo?.let {
                    MediaCodecInfoAlertDialog(listItem.context, it).show()
                }
            }
        }

        fun bind(mediaCodecInfo: MediaCodecInfo) {
            this.mediaCodecInfo = mediaCodecInfo

            listItem.headlineText = mediaCodecInfo.name
            listItem.supportingText = mediaCodecInfo.supportedTypes.joinToString()
            listItem.setLeadingIconImage(R.drawable.ic_video_settings)
        }
    }

    companion object {
        val PAIR_COMPARATOR = object : DiffUtil.ItemCallback<MediaCodecInfo>() {
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
