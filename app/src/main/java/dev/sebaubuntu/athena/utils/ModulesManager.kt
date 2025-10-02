/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.content.Context
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.ServiceLoader

class ModulesManager(context: Context) {
    private val modules = ServiceLoader.load(Module.Factory::class.java).map {
        it.create(context)
    }

    private val nameToModule = buildMap<String, Module> {
        modules.forEach { module ->
            val id = module.id

            require(!contains(id)) { "Duplicate module ID: $id" }

            put(id, module)
        }
    }

    val allRequiredPermissions = buildSet {
        modules.forEach { module ->
            addAll(module.requiredPermissions)
        }
    }.toTypedArray()

    private val rootScreen by lazy {
        Screen.ItemListScreen(
            identifier = Resource.Identifier.ROOT,
            title = LocalizedString(R.string.app_name),
            elements = modules.map { module ->
                Element.Item(
                    name = module.id,
                    title = module.name,
                    navigateTo = Resource.Identifier(
                        module = module.id,
                        path = listOf(),
                    ),
                    drawableResId = module.drawableResId,
                    value = Value(module.description),
                )
            },
        )
    }

    /**
     * Get the permissions required to [resolve] the given resource.
     *
     * @param identifier The resource identifier
     */
    fun requiredPermissions(
        identifier: Resource.Identifier,
    ) = identifier.module?.let {
        nameToModule[identifier.module]?.let {
            Result.Success<_, Error>(it.requiredPermissions)
        } ?: Result.Error(Error.NOT_FOUND)
    } ?: Result.Success(arrayOf())

    /**
     * @see Module.resolve
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun resolve(
        identifier: Resource.Identifier,
    ): Flow<Result<Resource, Error>> = identifier.module?.let { module ->
        nameToModule[module]?.resolve(identifier) ?: flowOf(Result.Error(Error.NOT_FOUND))
    } ?: flowOf(Result.Success(rootScreen))
}
