/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.components.Component
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Permission
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlin.collections.set

class AudioComponent(context: Context) : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = AudioComponent(context)
    }

    private val audioManager = context.getSystemService(AudioManager::class.java)

    override val name = "audio"

    override val title = LocalizedString(R.string.section_audio_name)

    override val description = LocalizedString(R.string.section_audio_description)

    override val drawableResId = R.drawable.ic_audio

    override val permissions = setOf<Permission>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = title,
                elements = listOfNotNull(
                    Element.Card(
                        identifier = identifier / "general",
                        title = LocalizedString(R.string.audio_general),
                        elements = listOfNotNull(
                            Element.Item(
                                identifier = identifier / "general" / "current_mode",
                                title = LocalizedString(R.string.audio_current_mode),
                                value = Value(audioManager.mode), // TODO
                            ),
                            Element.Item(
                                identifier = identifier / "general" / "fixed_volume",
                                title = LocalizedString(R.string.audio_fixed_volume),
                                value = Value(audioManager.isVolumeFixed),
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                Element.Item(
                                    identifier = identifier / "general" / "call_screening_mode_supported",
                                    title = LocalizedString(R.string.audio_call_screening_mode_supported),
                                    value = Value(audioManager.isCallScreeningModeSupported),
                                )
                            } else {
                                null
                            },
                            Element.Item(
                                identifier = identifier / "devices",
                                title = LocalizedString(R.string.audio_devices),
                                isNavigable = true,
                            ),
                        ),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                        Element.Card(
                            identifier = identifier / "spatializer",
                            title = LocalizedString(R.string.audio_spatializer),
                            elements = listOf(
                                Element.Item(
                                    identifier = identifier / "spatializer" / "available",
                                    title = LocalizedString(R.string.audio_spatializer_available),
                                    value = Value(audioManager.spatializer.isAvailable),
                                ),
                                Element.Item(
                                    identifier = identifier / "spatializer" / "enabled",
                                    title = LocalizedString(R.string.audio_spatializer_enabled),
                                    value = Value(audioManager.spatializer.isEnabled),
                                ),
                            ),
                        )
                    } else {
                        null
                    },
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        "devices" -> when (identifier.path.getOrNull(1)) {
            null -> suspend {
                val devices = arrayOf(
                    audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS),
                    audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS),
                ).flatten().distinctBy { it.id }.sortedBy { it.id }

                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.audio_devices),
                    elements = devices.map {
                        val isSink = it.isSink
                        val isSource = it.isSource

                        Element.Item(
                            identifier = identifier / it.id.toString(),
                            title = it.getDeviceTypeLocalizedString(),
                            isNavigable = true,
                            drawableResId = it.getDeviceTypeDrawableResId(),
                            value = Value(
                                "isSink: $isSink, isSource: $isSource",
                                when {
                                    isSink && isSource -> R.string.audio_role_sink_and_source
                                    isSink -> R.string.audio_role_sink
                                    isSource -> R.string.audio_role_source
                                    else -> R.string.unknown
                                },
                            ),
                        )
                    }
                )

                Result.Success<Resource, Error>(screen)
            }.asFlow()

            else -> when (identifier.path.getOrNull(2)) {
                null -> suspend {
                    val deviceId = identifier.path[1].toIntOrNull()

                    val devices = arrayOf(
                        audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS),
                        audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS),
                    ).flatten()

                    val device = deviceId?.let { deviceId ->
                        devices.find { it.id == deviceId }
                    }

                    val screen = device?.let {
                        Screen.ItemListScreen(
                            identifier = identifier,
                            title = it.getDeviceTypeLocalizedString(),
                            elements = listOfNotNull(
                                Element.Item(
                                    identifier = identifier / "id",
                                    title = LocalizedString(R.string.audio_device_id),
                                    value = Value(it.id),
                                ),
                                Element.Item(
                                    identifier = identifier / "product_name",
                                    title = LocalizedString(R.string.audio_device_product_name),
                                    value = Value(it.productName.toString()),
                                ),
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    Element.Item(
                                        identifier = identifier / "address",
                                        title = LocalizedString(R.string.audio_device_address),
                                        value = Value(it.address),
                                    )
                                } else {
                                    null
                                },
                                Element.Item(
                                    identifier = identifier / "type",
                                    title = LocalizedString(R.string.audio_device_type),
                                    value = Value(
                                        value = it.type,
                                        deviceTypeToStringRes,
                                    ),
                                ),
                                Element.Item(
                                    identifier = identifier / "role",
                                    title = LocalizedString(R.string.audio_device_role),
                                    value = run {
                                        Value(
                                            value = when {
                                                it.isSink && it.isSource -> 3
                                                it.isSink -> 1
                                                it.isSource -> 2
                                                else -> 0
                                            },
                                            valueToStringResId = mapOf(
                                                0 to R.string.unknown,
                                                1 to R.string.audio_role_sink,
                                                2 to R.string.audio_role_source,
                                                3 to R.string.audio_role_sink_and_source,
                                            ),
                                        )
                                    },
                                ),
                                Element.Item(
                                    identifier = identifier / "channel_counts",
                                    title = LocalizedString(R.string.audio_device_channel_counts),
                                    value = Value(it.channelCounts.toTypedArray()),
                                ),
                                Element.Item(
                                    identifier = identifier / "channel_masks",
                                    title = LocalizedString(R.string.audio_device_channel_masks),
                                    value = Value(it.channelMasks.toTypedArray()),
                                ),
                                Element.Item(
                                    identifier = identifier / "channel_index_masks",
                                    title = LocalizedString(R.string.audio_device_channel_index_masks),
                                    value = Value(it.channelIndexMasks.toTypedArray()),
                                ),
                                Element.Item(
                                    identifier = identifier / "sample_rates",
                                    title = LocalizedString(R.string.audio_device_sample_rates),
                                    value = Value(it.sampleRates.toTypedArray()),
                                ),
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    Element.Item(
                                        identifier = identifier / "audio_descriptors",
                                        title = LocalizedString(R.string.audio_device_audio_descriptors),
                                        value = Value(
                                            it.audioDescriptors.map { audioDescriptor ->
                                                audioDescriptor.standard
                                            }.toTypedArray()
                                        ),
                                    )
                                } else {
                                    null
                                },
                            ),
                        )
                    }

                    screen?.let {
                        Result.Success<Resource, Error>(it)
                    } ?: Result.Error(Error.NOT_FOUND)
                }.asFlow()

                else -> flowOf(Result.Error<Resource, Error>(Error.NOT_FOUND))
            }
        }

        else -> flowOf(Result.Error<Resource, Error>(Error.NOT_FOUND))
    }

    companion object {
        private val deviceTypeToStringRes = mutableMapOf(
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

        private val deviceTypeToDrawableRes = mutableMapOf(
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

        private fun AudioDeviceInfo.getDeviceTypeLocalizedString() = deviceTypeToStringRes[type]?.let {
            LocalizedString(it)
        } ?: LocalizedString(R.string.audio_device_type_seriously_unknown, type)

        private fun AudioDeviceInfo.getDeviceTypeDrawableResId() = deviceTypeToDrawableRes[
            type
        ] ?: R.drawable.ic_question_mark
    }
}
