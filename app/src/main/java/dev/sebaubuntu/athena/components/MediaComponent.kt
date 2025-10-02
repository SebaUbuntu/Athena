/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
import android.media.MediaCodecInfo
import android.media.MediaCodecList
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

class MediaComponent : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = MediaComponent()
    }

    override val name = "media"

    override val title = LocalizedString(R.string.section_media_name)

    override val description = LocalizedString(R.string.section_media_description)

    override val drawableResId = R.drawable.ic_video_settings

    override val permissions = setOf<Permission>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.ItemListScreen(
                identifier = identifier,
                title = title,
                elements = getAllCodecs().map { mediaCodecInfo ->
                    Element.Item(
                        identifier = identifier / mediaCodecInfo.name,
                        title = LocalizedString(mediaCodecInfo.name),
                        isNavigable = true,
                        drawableResId = R.drawable.ic_video_settings,
                        value = Value(mediaCodecInfo.supportedTypes),
                    )
                },
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> when (identifier.path.getOrNull(1)) {
            null -> suspend {
                val codecName = identifier.path.first()

                val mediaCodecInfo = getAllCodecs().firstOrNull { mediaCodecInfo ->
                    mediaCodecInfo.name == codecName
                }

                val screen = mediaCodecInfo?.let { mediaCodecInfo ->
                    Screen.ItemListScreen(
                        identifier = identifier,
                        title = LocalizedString(mediaCodecInfo.name),
                        elements = listOfNotNull(
                            Element.Item(
                                identifier = identifier / "name",
                                title = LocalizedString(R.string.media_codec_name),
                                value = Value(mediaCodecInfo.name),
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                Element.Item(
                                    identifier = identifier / "canonical_name",
                                    title = LocalizedString(R.string.media_codec_canonical_name),
                                    value = Value(mediaCodecInfo.canonicalName),
                                )
                            } else {
                                null
                            },
                            Element.Item(
                                identifier = identifier / "is_encoder",
                                title = LocalizedString(R.string.media_codec_is_encoder),
                                value = Value(mediaCodecInfo.isEncoder),
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                Element.Item(
                                    identifier = identifier / "is_hardware_accelerated",
                                    title = LocalizedString(R.string.media_codec_is_hardware_accelerated),
                                    value = Value(mediaCodecInfo.isHardwareAccelerated),
                                )
                            } else {
                                null
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                Element.Item(
                                    identifier = identifier / "is_software_only",
                                    title = LocalizedString(R.string.media_codec_is_software_only),
                                    value = Value(mediaCodecInfo.isSoftwareOnly),
                                )
                            } else {
                                null
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                Element.Item(
                                    identifier = identifier / "is_vendor",
                                    title = LocalizedString(R.string.media_codec_is_vendor),
                                    value = Value(mediaCodecInfo.isVendor),
                                )
                            } else {
                                null
                            },
                            Element.Item(
                                identifier = identifier / "supported_types",
                                title = LocalizedString(R.string.media_codec_supported_types),
                                value = Value(mediaCodecInfo.supportedTypes),
                            ),
                        ),
                    )
                }

                screen?.let {
                    Result.Success<Resource, Error>(it)
                } ?: Result.Error(Error.NOT_FOUND)
            }.asFlow()

            else -> flowOf(Result.Error(Error.NOT_FOUND))
        }
    }

    private fun getAllCodecs(): Array<MediaCodecInfo> = MediaCodecList(
        MediaCodecList.ALL_CODECS
    ).codecInfos
}
