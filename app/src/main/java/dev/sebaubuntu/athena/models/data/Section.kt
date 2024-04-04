/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.data

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.models.data.Subsection.Companion.listSerializer
import dev.sebaubuntu.athena.models.data.Subsection.Companion.toSerializable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.PairSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

abstract class Section(
    val name: String,
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val icon: Int,
    val requiredPermissions: Array<String> = arrayOf(),
    @IdRes val navigationActionId: Int? = null
) {
    open fun getInfoOld(context: Context): Map<String, Map<String, String?>>? = null

    open fun dataFlow(context: Context): Flow<List<Subsection>?> = {
        getInfoOld(context)?.map { subsection ->
            Subsection(
                subsection.key,
                subsection.value.map { information ->
                    Information(
                        information.key,
                        information.value?.let { InformationValue.StringValue(it) }
                    )
                }
            )
        }
    }.asFlow()

    companion object {
        fun Map<Section, List<Subsection>>.toSerializable() = map {
            it.key.name to it.value.toSerializable()
        }.toMap()

        fun <T : Map<Section, List<Subsection>>> listSerializer() = MapSerializer(
            String.serializer(), listSerializer<List<Subsection>>()
        )

        class Serializer : KSerializer<Pair<Section, List<Subsection>>> {
            override val descriptor = PairSerializer(
                String.serializer(),
                listSerializer<List<Subsection>>()
            ).descriptor

            override fun deserialize(decoder: Decoder): Pair<Section, List<Subsection>> {
                throw Exception("Deserialization is not supported")
            }

            override fun serialize(
                encoder: Encoder, value: Pair<Section, List<Subsection>>
            ) = encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.first.name)
                encodeSerializableElement(
                    descriptor,
                    1,
                    listSerializer<List<Subsection>>(),
                    value.second.toSerializable()
                )
            }
        }
    }
}
