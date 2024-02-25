/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.ext.stringRes
import dev.sebaubuntu.athena.ui.views.ListItem

class AudioFragment : Fragment(R.layout.fragment_audio) {
    // Views
    private val callScreeningModeSupportedListItem by getViewProperty<ListItem>(R.id.callScreeningModeSupportedListItem)
    private val currentAudioModeListItem by getViewProperty<ListItem>(R.id.currentAudioModeListItem)
    private val devicesListItem by getViewProperty<ListItem>(R.id.devicesListItem)
    private val fixedVolumeListItem by getViewProperty<ListItem>(R.id.fixedVolumeListItem)
    private val spatializerAvailableListItem by getViewProperty<ListItem>(R.id.spatializerAvailableListItem)
    private val spatializerEnabledListItem by getViewProperty<ListItem>(R.id.spatializerEnabledListItem)

    // System services
    private val audioManager by lazy {
        requireContext().getSystemService(AudioManager::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(
                bottom = insets.bottom,
                left = insets.left,
                right = insets.right,
            )

            windowInsets
        }

        devicesListItem.setOnClickListener {
            findNavController().navigate(R.id.action_audioFragment_to_audioDevicesFragment)
        }

        val currentAudioMode = audioManager.mode
        currentAudioModeListItem.setSupportingText(
            when (currentAudioMode) {
                AudioManager.MODE_NORMAL -> R.string.audio_mode_normal
                AudioManager.MODE_RINGTONE -> R.string.audio_mode_ringtone
                AudioManager.MODE_IN_CALL -> R.string.audio_mode_in_call
                AudioManager.MODE_IN_COMMUNICATION -> R.string.audio_mode_in_communication
                AudioManager.MODE_CALL_SCREENING -> R.string.audio_mode_call_screening
                AudioManager.MODE_CALL_REDIRECT -> R.string.audio_mode_call_redirect
                AudioManager.MODE_COMMUNICATION_REDIRECT ->
                    R.string.audio_mode_communication_redirect

                else -> R.string.audio_mode_unknown
            }, currentAudioMode
        )

        fixedVolumeListItem.setSupportingText(audioManager.isVolumeFixed.stringRes)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            callScreeningModeSupportedListItem.setSupportingText(
                audioManager.isCallScreeningModeSupported.stringRes
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            spatializerAvailableListItem.setSupportingText(
                audioManager.spatializer.isAvailable.stringRes
            )
            spatializerEnabledListItem.setSupportingText(
                audioManager.spatializer.isEnabled.stringRes
            )
        }
    }
}
