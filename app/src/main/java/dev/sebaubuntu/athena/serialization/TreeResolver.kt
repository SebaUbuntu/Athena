/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.serialization

import android.util.Log
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.ext.mapAsync
import dev.sebaubuntu.athena.utils.ModulesManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonNull

/**
 * Step 1: Recursively resolve all the [Resource.Identifier]s.
 */
class TreeResolver(
    private val modulesManager: ModulesManager,
) {
    /**
     * The final data.
     */
    lateinit var result: Any

    /**
     * The set of [Resource.Identifier]s that have already been resolved.
     */
    private val resolvedResourceIdentifiers = mutableSetOf<Resource.Identifier>()

    /**
     * The set of [Resource.Identifier]s that need to be resolved.
     */
    private val resourceIdentifiersToResolve = mutableSetOf<Resource.Identifier>()

    /**
     * All the [Resource.Identifier] that encountered an [Error].
     */
    private val errors = mutableMapOf<Resource.Identifier, Error>()

    /**
     * [Mutex] used to protect access to all mutable collections.
     */
    private val dataMutex = Mutex()

    /**
     * Resolve all the [Resource.Identifier]s.
     *
     * Note that all required permissions must be granted before calling this function.
     */
    suspend fun resolveTree(): Result<Any, Error> {
        resourceIdentifiersToResolve.add(Resource.Identifier.ROOT)

        while (resourceIdentifiersToResolve.isNotEmpty()) {
            val snapshot = resourceIdentifiersToResolve.toSet()

            resourceIdentifiersToResolve.clear()

            resolvedResourceIdentifiers.addAll(snapshot)

            snapshot.mapAsync { resourceIdentifier ->
                resourceIdentifier.serialize()?.let {
                    registerData(
                        identifier = resourceIdentifier,
                        data = it,
                    )
                }
            }
        }

        // Log the errors
        errors.forEach { (identifier, error) ->
            Log.e(
                LOG_TAG,
                "Error while serializing identifier $identifier: $error"
            )
        }

        return Result.Success(result)
    }

    /**
     * Serializes a [Resource.Identifier] into a [Serializable] object.
     *
     * When null is returned, the resource is not included in the result.
     */
    private suspend fun Resource.Identifier.serialize(): Any? = when (
        val result = modulesManager.resolve(this).firstOrNull()
    ) {
        is Result.Success -> result.data.serialize()

        is Result.Error -> dataMutex.withLock {
            errors.put(this, result.error)
            null
        }

        null -> error("Resource $this emitted nothing")
    }

    /**
     * Serializes a [Resource] into a [Serializable] object.
     *
     * When null is returned, the resource is not included in the result.
     */
    private suspend fun Resource.serialize(): Any? = when (this) {
        is Screen -> when (this) {
            is Screen.CardListScreen -> elements.serialize()

            is Screen.DialogScreen -> elements.serialize()

            is Screen.ItemListScreen -> elements.serialize()
        }
    }

    private suspend fun Element.serialize(): Any? = when (val navigateTo = navigateTo) {
        null -> when (this) {
            is Element.Card -> elements.serialize()

            is Element.Item -> value ?: JsonNull
        }

        else -> {
            addResourceIdentifierToResolve(navigateTo)
            null
        }
    }

    private suspend fun Iterable<Element>.serialize() = this.mapAsync { element ->
        element.serialize()?.let {
            element.name to it
        }
    }.filterNotNull().toMap().toMutableMap()

    /**
     * Queue a [Resource.Identifier] to be resolved.
     */
    private suspend fun addResourceIdentifierToResolve(
        identifier: Resource.Identifier,
    ) = dataMutex.withLock {
        if (identifier in resolvedResourceIdentifiers) {
            Log.i(
                LOG_TAG,
                "Circular dependency detected for identifier $identifier, ignoring",
            )
            return@withLock
        }

        resourceIdentifiersToResolve.add(identifier)
    }

    /**
     * Add a data to the pointed [identifier].
     */
    private suspend fun registerData(
        identifier: Resource.Identifier,
        data: Any,
    ) = dataMutex.withLock {
        identifier.module?.also { module ->
            val path = buildList {
                add(module)

                addAll(identifier.path)
            }

            var currentMap: Any = result
            val currentPath = mutableListOf<String>()
            path.dropLast(1).forEach { pathSegment ->
                when (currentMap) {
                    is MutableMap<*, *> -> currentMap.requireKeysType<String>().let {
                        if (!it.containsKey(pathSegment)) {
                            it[pathSegment] = mutableMapOf<String, Any>()
                        }

                        currentMap = it.getValue(pathSegment)
                        currentPath.add(pathSegment)
                    }

                    else -> error("Data at path $currentPath is not a map")
                }
            }

            when (currentMap) {
                is MutableMap<*, *> -> currentMap.requireKeysType<String>().let {
                    require(!it.containsKey(path.last())) {
                        "Data at path $path already exists"
                    }

                    it[path.last()] = data
                }

                else -> error("Data at path $currentPath is not a map")
            }
        } ?: run {
            // No module, set as root data
            result = data
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> MutableMap<*, *>.requireKeysType() = when (
        keys.all(T::class::isInstance)
    ) {
        true -> this as MutableMap<T, Any>
        false -> error("MutableMap doesn't contain keys of type ${T::class.simpleName}")
    }

    companion object {
        private val LOG_TAG = TreeResolver::class.simpleName!!
    }
}
