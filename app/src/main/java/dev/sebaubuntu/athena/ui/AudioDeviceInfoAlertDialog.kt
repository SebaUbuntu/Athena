/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import android.content.Context
import android.media.AudioDeviceInfo
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.AudioDeviceInfoUtils

class AudioDeviceInfoAlertDialog(
    context: Context,
    private val audioDeviceInfo: AudioDeviceInfo,
) : AlertDialog(context, R.style.Theme_Athena_CustomDialog) {
    private val addressListItem by lazy { findViewById<ListItem>(R.id.addressListItem)!! }
    private val audioDescriptorsListItem by lazy { findViewById<ListItem>(R.id.audioDescriptorsListItem)!! }
    private val channelCountsListItem by lazy { findViewById<ListItem>(R.id.channelCountsListItem)!! }
    private val channelMasksListItem by lazy { findViewById<ListItem>(R.id.channelMasksListItem)!! }
    private val channelIndexMasksListItem by lazy { findViewById<ListItem>(R.id.channelIndexMasksListItem)!! }
    private val deviceTypeListItem by lazy { findViewById<ListItem>(R.id.deviceTypeListItem)!! }
    private val doneButton by lazy { findViewById<MaterialButton>(R.id.doneButton)!! }
    private val idListItem by lazy { findViewById<ListItem>(R.id.idListItem)!! }
    private val productNameListItem by lazy { findViewById<ListItem>(R.id.productNameListItem)!! }
    private val roleListItem by lazy { findViewById<ListItem>(R.id.roleListItem)!! }
    private val sampleRatesListItem by lazy { findViewById<ListItem>(R.id.sampleRatesListItem)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_audio_device_info)

        idListItem.supportingText = "${audioDeviceInfo.id}"

        productNameListItem.supportingText = audioDeviceInfo.productName

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            addressListItem.supportingText = audioDeviceInfo.address
        }

        val type = audioDeviceInfo.type

        AudioDeviceInfoUtils.deviceTypeToStringRes[type]?.also {
            deviceTypeListItem.setSupportingText(it)
        } ?: run {
            deviceTypeListItem.setSupportingText(R.string.audio_device_type_seriously_unknown, type)
        }

        val isSink = audioDeviceInfo.isSink
        val isSource = audioDeviceInfo.isSource
        roleListItem.setSupportingText(
            when {
                isSink && isSource -> R.string.audio_role_sink_and_source
                isSink -> R.string.audio_role_sink
                isSource -> R.string.audio_role_source
                else -> R.string.unknown
            }
        )

        channelCountsListItem.supportingText = audioDeviceInfo.channelCounts.joinToString()

        channelMasksListItem.supportingText = audioDeviceInfo.channelMasks.joinToString()

        channelIndexMasksListItem.supportingText = audioDeviceInfo.channelIndexMasks.joinToString()

        sampleRatesListItem.supportingText = audioDeviceInfo.sampleRates.joinToString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioDescriptorsListItem.supportingText = audioDeviceInfo.audioDescriptors.joinToString {
                it.standard.toString()
            }
        }

        doneButton.setOnClickListener {
            dismiss()
        }
    }
}
