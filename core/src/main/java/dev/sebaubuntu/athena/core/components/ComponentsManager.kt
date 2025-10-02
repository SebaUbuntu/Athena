/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.components

import android.content.Context
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Permission
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.ServiceLoader

class ComponentsManager(context: Context) {
    private val components = ServiceLoader.load(Component.Factory::class.java).map {
        it.create(context)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val nameToComponent = buildMap<String, Component> {
        components.forEach { component ->
            val name = component.name

            require(!contains(name)) { "Duplicate component name: $name" }

            put(name, component)
        }
    }

    private val rootScreen by lazy {
        Screen.ItemListScreen(
            identifier = Resource.Identifier.ROOT,
            title = LocalizedString("Athena"),
            elements = components.map { component ->
                Element.Item(
                    identifier = Resource.Identifier(
                        component = component.name,
                    ),
                    title = component.title,
                    isNavigable = true,
                    drawableResId = component.drawableResId,
                )
            },
        )
    }

    /**
     * Get the [Permission]s required to [resolve] the given resource.
     *
     * @param identifier The resource identifier
     */
    fun requiredPermissions(
        identifier: Resource.Identifier,
    ) = identifier.component?.let {
        nameToComponent[identifier.component]?.let {
            Result.Success<_, Error>(it.permissions)
        } ?: Result.Error(Error.NOT_FOUND)
    } ?: Result.Success(setOf())

    /**
     * @see Component.resolve
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun resolve(
        identifier: Resource.Identifier,
    ): Flow<Result<Resource, Error>> = identifier.component?.let { component ->
        nameToComponent[component]?.resolve(identifier) ?: flowOf(Result.Error(Error.NOT_FOUND))
    } ?: flowOf(Result.Success(rootScreen))
}
