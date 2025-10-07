/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.media.AudioDeviceInfo
import android.os.Build
import dev.sebaubuntu.athena.R

object AudioDeviceInfoUtils {
    val deviceTypeToStringRes = mutableMapOf(
        AudioDeviceInfo.TYPE_UNKNOWN to R.string.audio_device_type_unknown,
        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE to R.string.audio_device_type_builtin_earpiece,
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER to R.string.audio_device_type_builtin_speaker,
        AudioDeviceInfo.TYPE_WIRED_HEADSET to R.string.audio_device_type_wired_headset,
        AudioDeviceInfo.TYPE_WIRED_HEADPHONES to R.string.audio_device_type_wired_headphones,
        AudioDeviceInfo.TYPE_LINE_ANALOG to R.string.audio_device_type_line_analog,
        AudioDeviceInfo.TYPE_LINE_DIGITAL to R.string.audio_device_type_line_digital,
        AudioDeviceInfo.TYPE_BLUETOOTH_SCO to R.string.audio_device_type_bluetooth_sco,
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP to R.string.audio_device_type_bluetooth_a2dp,
        AudioDeviceInfo.TYPE_HDMI to R.string.audio_device_type_hdmi,
        AudioDeviceInfo.TYPE_HDMI_ARC to R.string.audio_device_type_hdmi_arc,
        AudioDeviceInfo.TYPE_USB_DEVICE to R.string.audio_device_type_usb_device,
        AudioDeviceInfo.TYPE_USB_ACCESSORY to R.string.audio_device_type_usb_accessory,
        AudioDeviceInfo.TYPE_DOCK to R.string.audio_device_type_dock,
        AudioDeviceInfo.TYPE_FM to R.string.audio_device_type_fm,
        AudioDeviceInfo.TYPE_BUILTIN_MIC to R.string.audio_device_type_builtin_mic,
        AudioDeviceInfo.TYPE_FM_TUNER to R.string.audio_device_type_fm_tuner,
        AudioDeviceInfo.TYPE_TV_TUNER to R.string.audio_device_type_tv_tuner,
        AudioDeviceInfo.TYPE_TELEPHONY to R.string.audio_device_type_telephony,
        AudioDeviceInfo.TYPE_AUX_LINE to R.string.audio_device_type_aux_line,
        AudioDeviceInfo.TYPE_IP to R.string.audio_device_type_ip,
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this[AudioDeviceInfo.TYPE_BUS] = R.string.audio_device_type_bus
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this[AudioDeviceInfo.TYPE_USB_HEADSET] = R.string.audio_device_type_usb_headset
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this[AudioDeviceInfo.TYPE_HEARING_AID] = R.string.audio_device_type_hearing_aid
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this[AudioDeviceInfo.TYPE_BUILTIN_SPEAKER_SAFE] =
                R.string.audio_device_type_builtin_speaker_safe
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this[AudioDeviceInfo.TYPE_REMOTE_SUBMIX] = R.string.audio_device_type_remote_submix
            this[AudioDeviceInfo.TYPE_BLE_HEADSET] = R.string.audio_device_type_ble_headset
            this[AudioDeviceInfo.TYPE_BLE_SPEAKER] = R.string.audio_device_type_ble_speaker
            this[AudioDeviceInfo.TYPE_HDMI_EARC] = R.string.audio_device_type_hdmi_earc
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this[AudioDeviceInfo.TYPE_BLE_BROADCAST] = R.string.audio_device_type_ble_broadcast
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this[AudioDeviceInfo.TYPE_DOCK_ANALOG] = R.string.audio_device_type_dock_analog
        }
    }.toMap()

    val deviceTypeToDrawableRes = mutableMapOf(
        AudioDeviceInfo.TYPE_UNKNOWN to R.drawable.ic_question_mark,
        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE to R.drawable.ic_speaker_phone,
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER to R.drawable.ic_speaker,
        AudioDeviceInfo.TYPE_WIRED_HEADSET to R.drawable.ic_headset_mic,
        AudioDeviceInfo.TYPE_WIRED_HEADPHONES to R.drawable.ic_headphones,
        AudioDeviceInfo.TYPE_LINE_ANALOG to R.drawable.ic_cable,
        AudioDeviceInfo.TYPE_LINE_DIGITAL to R.drawable.ic_cable,
        AudioDeviceInfo.TYPE_BLUETOOTH_SCO to R.drawable.ic_phone_bluetooth_speaker,
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP to R.drawable.ic_bluetooth_audio,
        AudioDeviceInfo.TYPE_HDMI to R.drawable.ic_settings_input_hdmi,
        AudioDeviceInfo.TYPE_HDMI_ARC to R.drawable.ic_settings_input_hdmi,
        AudioDeviceInfo.TYPE_USB_DEVICE to R.drawable.ic_usb,
        AudioDeviceInfo.TYPE_USB_ACCESSORY to R.drawable.ic_usb,
        AudioDeviceInfo.TYPE_DOCK to R.drawable.ic_dock,
        AudioDeviceInfo.TYPE_FM to R.drawable.ic_radio,
        AudioDeviceInfo.TYPE_BUILTIN_MIC to R.drawable.ic_mic_none,
        AudioDeviceInfo.TYPE_FM_TUNER to R.drawable.ic_radio,
        AudioDeviceInfo.TYPE_TV_TUNER to R.drawable.ic_tv,
        AudioDeviceInfo.TYPE_TELEPHONY to R.drawable.ic_phone_in_talk,
        AudioDeviceInfo.TYPE_AUX_LINE to R.drawable.ic_cable,
        AudioDeviceInfo.TYPE_IP to R.drawable.ic_globe,
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this[AudioDeviceInfo.TYPE_BUS] = R.drawable.ic_cable
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this[AudioDeviceInfo.TYPE_USB_HEADSET] = R.drawable.ic_usb
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this[AudioDeviceInfo.TYPE_HEARING_AID] = R.drawable.ic_hearing
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this[AudioDeviceInfo.TYPE_BUILTIN_SPEAKER_SAFE] = R.drawable.ic_speaker
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this[AudioDeviceInfo.TYPE_REMOTE_SUBMIX] = R.drawable.ic_cast
            this[AudioDeviceInfo.TYPE_BLE_HEADSET] = R.drawable.ic_phone_bluetooth_speaker
            this[AudioDeviceInfo.TYPE_BLE_SPEAKER] = R.drawable.ic_bluetooth_audio
            this[AudioDeviceInfo.TYPE_HDMI_EARC] = R.drawable.ic_settings_input_hdmi
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this[AudioDeviceInfo.TYPE_BLE_BROADCAST] = R.drawable.ic_bluetooth_audio
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this[AudioDeviceInfo.TYPE_DOCK_ANALOG] = R.drawable.ic_dock
        }
    }.toMap()
}
