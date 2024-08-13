/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.data

import android.content.Context
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.models.data.Information.Companion.listSerializer
import dev.sebaubuntu.athena.models.data.Information.Companion.toSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.PairSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlin.reflect.safeCast

@Serializable(with = Subsection.Companion.Serializer::class)
data class Subsection(
    /**
     * Name of the subsection in snake_case.
     */
    val name: String,

    /**
     * The information regarding this subsection.
     */
    val information: List<Information>,

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
    override fun equals(other: Any?) = Subsection::class.safeCast(other)?.let { o ->
        name == o.name
                && information == o.information
                && title == o.title
                && titleFormatArgs?.let {
            o.titleFormatArgs?.let { oTitleFormatArgs ->
                titleFormatArgs.contentEquals(oTitleFormatArgs)
            } ?: false
        } ?: (o.titleFormatArgs == null)
    } ?: false

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + information.hashCode()
        result = 31 * result + (title ?: 0)
        result = 31 * result + (titleFormatArgs?.contentHashCode() ?: 0)
        return result
    }

    fun getDisplayTitle(context: Context) = title?.let {
        titleFormatArgs?.let { formatArgs ->
            context.getString(it, *formatArgs)
        } ?: context.getString(it)
    } ?: name

    fun toPair() = name to information.toSerializable()

    companion object {
        fun List<Subsection>.toSerializable() = associate {
            it.toPair()
        }

        fun <T : List<Subsection>> listSerializer() = MapSerializer(
            String.serializer(), listSerializer<List<Information>>()
        )

        object Serializer : KSerializer<Subsection> {
            override val descriptor = PairSerializer(
                String.serializer(),
                listSerializer<List<Information>>()
            ).descriptor

            override fun deserialize(decoder: Decoder): Subsection {
                throw Exception("Deserialization is not supported")
            }

            override fun serialize(
                encoder: Encoder, value: Subsection
            ) = encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.name)
                encodeSerializableElement(
                    descriptor,
                    1,
                    listSerializer<List<Information>>(),
                    value.information.toSerializable()
                )
            }
        }
    }
}
