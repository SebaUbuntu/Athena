/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import android.media.MediaCodecInfo
import androidx.recyclerview.widget.DiffUtil
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ui.dialogs.MediaCodecInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem

class MediaCodecsAdapter : SimpleListAdapter<MediaCodecInfo, ListItem>(
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

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<MediaCodecInfo>() {
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
