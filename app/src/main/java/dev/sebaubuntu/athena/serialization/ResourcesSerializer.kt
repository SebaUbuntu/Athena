/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.serialization

import dev.sebaubuntu.athena.core.models.Result.Companion.map
import dev.sebaubuntu.athena.utils.ModulesManager
import kotlinx.serialization.json.Json

/**
 * Convert the modules tree into a JSON string.
 */
object ResourcesSerializer {
    private val json = Json {
        prettyPrint = true
    }

    suspend fun serializeToJson(
        modulesManager: ModulesManager,
    ) = TreeResolver(modulesManager).resolveTree()
        .map(ToJsonElementSerializer::serializeTree)
        .map(json::encodeToString)
}
