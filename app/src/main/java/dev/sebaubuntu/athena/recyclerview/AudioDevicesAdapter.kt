/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import android.media.AudioDeviceInfo
import androidx.recyclerview.widget.DiffUtil
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ui.dialogs.AudioDeviceInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.utils.AudioDeviceInfoUtils

class AudioDevicesAdapter : SimpleListAdapter<AudioDeviceInfo, ListItem>(
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
            AudioDeviceInfoUtils.deviceTypeToDrawableRes[type] ?: R.drawable.ic_question_mark
        )
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<AudioDeviceInfo>() {
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
