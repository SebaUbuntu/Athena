/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category

object AudioCategory : Category {
    override val name = R.string.section_audio_name
    override val description = R.string.section_audio_description
    override val icon = R.drawable.ic_audio
    override val requiredPermissions = arrayOf<String>()

    @SuppressLint("HardwareIds")
    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
        val audioManager = context.getSystemService(AudioManager::class.java)

        this["General"] = mutableMapOf(
            "Current audio mode" to when (audioManager.mode) {
                AudioManager.MODE_NORMAL -> "Normal"
                AudioManager.MODE_RINGTONE -> "Ringtone"
                AudioManager.MODE_IN_CALL -> "In call"
                AudioManager.MODE_IN_COMMUNICATION -> "In communication"
                AudioManager.MODE_CALL_SCREENING -> "Call screening"
                AudioManager.MODE_CALL_REDIRECT -> "Call redirect"
                AudioManager.MODE_COMMUNICATION_REDIRECT -> "Communication redirect"
                else -> "Unknown"
            },
            "Is volume fixed" to "${audioManager.isVolumeFixed}",
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this["Is call screening mode supported"] =
                    "${audioManager.isCallScreeningModeSupported}"
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            this["Spatializer"] = mapOf(
                "Available" to "${audioManager.spatializer.isAvailable}",
                "Enabled" to "${audioManager.spatializer.isEnabled}",
            )
        }

        val outputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        if (outputDevices.isNotEmpty()) {
            this["Output devices"] = mapOf()
            for (outputDevice in outputDevices) {
                this["${outputDevice.id}"] = getAudioDeviceInfo(outputDevice)
            }
        }

        val inputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)
        if (inputDevices.isNotEmpty()) {
            this["Input devices"] = mapOf()
            for (inputDevice in inputDevices) {
                this["${inputDevice.id}"] = getAudioDeviceInfo(inputDevice)
            }
        }
    }

    private fun getAudioDeviceInfo(audioDeviceInfo: AudioDeviceInfo) =
        mutableMapOf<String, String>().apply {
            this["Device ID"] = audioDeviceInfo.id.toString()
            this["Product name"] = audioDeviceInfo.productName.toString()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val address = audioDeviceInfo.address
                if (address.isNotEmpty()) {
                    this["Address"] = address
                }
            }

            this["Audio device type"] = (deviceTypeToString[audioDeviceInfo.type] ?: "Unknown type")

            this["Device type"] = when {
                audioDeviceInfo.isSource && audioDeviceInfo.isSink -> "Source/sink"
                audioDeviceInfo.isSource -> "Source"
                audioDeviceInfo.isSink -> "Sink"
                else -> "None"
            }

            val channelCounts = audioDeviceInfo.channelCounts
            if (channelCounts.isNotEmpty()) {
                this["Channel counts"] = channelCounts.joinToString()
            }

            val channelMasks = audioDeviceInfo.channelMasks
            if (channelMasks.isNotEmpty()) {
                this["Channel masks"] = channelMasks.joinToString()
            }

            val channelIndexMasks = audioDeviceInfo.channelIndexMasks
            if (channelIndexMasks.isNotEmpty()) {
                this["Channel index masks"] = channelIndexMasks.joinToString()
            }

            val sampleRates = audioDeviceInfo.sampleRates
            if (sampleRates.isNotEmpty()) {
                this["Sample rates"] = sampleRates.joinToString()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val audioDescriptors = audioDeviceInfo.audioDescriptors
                if (audioDescriptors.isNotEmpty()) {
                    this["Audio descriptors"] = audioDescriptors.joinToString {
                        it.standard.toString()
                    }
                }
            }
        }

    private val deviceTypeToString = mutableMapOf(
        AudioDeviceInfo.TYPE_UNKNOWN to "Unknown",
        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE to "Built in earpiece",
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER to "Built in speaker",
        AudioDeviceInfo.TYPE_WIRED_HEADSET to "Wired headset",
        AudioDeviceInfo.TYPE_WIRED_HEADPHONES to "Wired headphones",
        AudioDeviceInfo.TYPE_LINE_ANALOG to "Line analog",
        AudioDeviceInfo.TYPE_LINE_DIGITAL to "Line digital",
        AudioDeviceInfo.TYPE_BLUETOOTH_SCO to "Bluetooth SCO",
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP to "Bluetooth A2DP",
        AudioDeviceInfo.TYPE_HDMI to "HDMI",
        AudioDeviceInfo.TYPE_HDMI_ARC to "HDMI ARC",
        AudioDeviceInfo.TYPE_USB_DEVICE to "USB device",
        AudioDeviceInfo.TYPE_USB_ACCESSORY to "USB accessory",
        AudioDeviceInfo.TYPE_DOCK to "Dock",
        AudioDeviceInfo.TYPE_FM to "FM",
        AudioDeviceInfo.TYPE_BUILTIN_MIC to "Built in mic",
        AudioDeviceInfo.TYPE_FM_TUNER to "FM tuner",
        AudioDeviceInfo.TYPE_TV_TUNER to "TV tuner",
        AudioDeviceInfo.TYPE_TELEPHONY to "Telephony",
        AudioDeviceInfo.TYPE_AUX_LINE to "AUX line",
        AudioDeviceInfo.TYPE_IP to "IP",
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this[AudioDeviceInfo.TYPE_BUS] = "Bus"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this[AudioDeviceInfo.TYPE_USB_HEADSET] = "USB headset"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this[AudioDeviceInfo.TYPE_HEARING_AID] = "Hearing aid"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this[AudioDeviceInfo.TYPE_BUILTIN_SPEAKER_SAFE] = "Built in speaker safe"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this[AudioDeviceInfo.TYPE_REMOTE_SUBMIX] = "Remote submix"
            this[AudioDeviceInfo.TYPE_BLE_HEADSET] = "BLE headset"
            this[AudioDeviceInfo.TYPE_BLE_SPEAKER] = "BLE speaker"
            this[AudioDeviceInfo.TYPE_HDMI_EARC] = "HDMI EARC"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this[AudioDeviceInfo.TYPE_BLE_BROADCAST] = "BLE broadcast"
        }
    }.toMap()
}
