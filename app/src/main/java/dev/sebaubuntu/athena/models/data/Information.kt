/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.data

import android.content.Context
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.R
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.MapEntrySerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlin.reflect.safeCast

@Serializable(with = Information.Companion.Serializer::class)
data class Information(
    /**
     * Name of the information in snake_case.
     */
    val name: String,

    /**
     * The value of the information.
     */
    val value: InformationValue?,

    /**
     * A title string resource ID, can be null.
     */
    @Transient
    @StringRes
    val title: Int? = null,

    /**
     * The format arguments for the title.
     */
    @Transient
    val titleFormatArgs: Array<Any>? = null,
) {
    fun getDisplayTitle(context: Context) = title?.let {
        titleFormatArgs?.let { formatArgs ->
            context.getString(it, *formatArgs)
        } ?: context.getString(it)
    } ?: name

    fun getDisplayValue(
        context: Context
    ) = value?.getDisplayValue(context) ?: context.getString(R.string.unknown)

    fun toPair() = name to value

    override fun equals(other: Any?) = Information::class.safeCast(other)?.let { o ->
        name == o.name
                && value == o.value
                && title == o.title
                && titleFormatArgs?.let {
            o.titleFormatArgs?.let { oTitleFormatArgs ->
                titleFormatArgs.contentEquals(oTitleFormatArgs)
            } ?: false
        } ?: (o.titleFormatArgs == null)
    } ?: false

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + (title ?: 0)
        result = 31 * result + (titleFormatArgs?.contentHashCode() ?: 0)
        return result
    }

    companion object {
        fun List<Information>.toSerializable() = associate {
            it.toPair()
        }

        fun <T : List<Information>> listSerializer() = MapSerializer(
            String.serializer(), InformationValue.serializer().nullable
        )

        object Serializer : KSerializer<Information> {
            override val descriptor = MapEntrySerializer(
                String.serializer(), InformationValue.serializer().nullable
            ).descriptor

            override fun deserialize(decoder: Decoder): Information {
                throw Exception("Deserialization is not supported")
            }

            override fun serialize(
                encoder: Encoder, value: Information
            ) = encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.name)
                encodeSerializableElement(
                    descriptor, 1, InformationValue.serializer().nullable, value.value
                )
            }
        }
    }
}
