/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.serialization

import dev.sebaubuntu.athena.core.models.Value
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

/**
 * Step 2: Convert the tree into a [JsonElement].
 */
object ToJsonElementSerializer {
    fun serializeTree(tree: Any?) = tree.toJsonElement()

    private fun Any?.toJsonElement(): JsonElement = when (this) {
        is JsonElement -> this

        is List<*> -> JsonArray(
            map { it.toJsonElement() }
        )

        is Map<*, *> -> JsonObject(
            requireKeysType<String>().mapValues { (_, value) -> value.toJsonElement() }
        )

        is Value<*> -> toJsonElement()

        null -> JsonNull

        else -> error("Got unknown type $this")
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> Map<*, *>.requireKeysType() = when (
        keys.all(T::class::isInstance)
    ) {
        true -> this as Map<T, Any>
        false -> error("Map doesn't contain keys of type ${T::class.simpleName}")
    }
}
