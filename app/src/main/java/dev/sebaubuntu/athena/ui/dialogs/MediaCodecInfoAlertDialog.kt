/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.dialogs

import android.content.Context
import android.media.MediaCodecInfo
import android.os.Build
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.stringRes
import dev.sebaubuntu.athena.ui.views.ListItem

class MediaCodecInfoAlertDialog(
    context: Context,
    private val mediaCodecInfo: MediaCodecInfo,
) : CustomAlertDialog(context, R.layout.dialog_media_codec_info) {
    private val canonicalNameListItem by lazy { findViewById<ListItem>(R.id.canonicalNameListItem)!! }
    private val doneButton by lazy { findViewById<MaterialButton>(R.id.doneButton)!! }
    private val isEncoderListItem by lazy { findViewById<ListItem>(R.id.isEncoderListItem)!! }
    private val isHardwareAcceleratedListItem by lazy { findViewById<ListItem>(R.id.isHardwareAcceleratedListItem)!! }
    private val isSoftwareOnlyListItem by lazy { findViewById<ListItem>(R.id.isSoftwareOnlyListItem)!! }
    private val isVendorListItem by lazy { findViewById<ListItem>(R.id.isVendorListItem)!! }
    private val nameListItem by lazy { findViewById<ListItem>(R.id.nameListItem)!! }
    private val supportedTypesListItem by lazy { findViewById<ListItem>(R.id.supportedTypesListItem)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nameListItem.supportingText = mediaCodecInfo.name
        canonicalNameListItem.setSupportingTextOrHide(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mediaCodecInfo.canonicalName
            } else {
                null
            }
        )

        isEncoderListItem.setSupportingText(mediaCodecInfo.isEncoder.stringRes)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isHardwareAcceleratedListItem.setSupportingText(
                mediaCodecInfo.isHardwareAccelerated.stringRes
            )
            isSoftwareOnlyListItem.setSupportingText(
                mediaCodecInfo.isSoftwareOnly.stringRes
            )
            isVendorListItem.setSupportingText(
                mediaCodecInfo.isVendor.stringRes
            )
        }

        supportedTypesListItem.supportingText = mediaCodecInfo.supportedTypes.joinToString()

        doneButton.setOnClickListener {
            dismiss()
        }
    }
}
