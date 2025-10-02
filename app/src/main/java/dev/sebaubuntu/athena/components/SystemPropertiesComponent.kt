/*
 * SPDX-FileCopyrightText: 2025 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
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
import dev.sebaubuntu.athena.utils.SystemProperties
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class SystemPropertiesComponent : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = SystemPropertiesComponent()
    }

    override val name = "system_properties"

    override val title = LocalizedString(R.string.section_props_name)

    override val description = LocalizedString(R.string.section_props_description)

    override val drawableResId = R.drawable.ic_build

    override val permissions = setOf<Permission>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> SystemProperties::getProps.asFlow()
            .mapLatest { props ->
                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = title,
                    elements = props.map { prop ->
                        Element.Item(
                            identifier = identifier / prop.key,
                            title = LocalizedString(prop.key),
                            value = Value(prop.value),
                        )
                    }
                )

                Result.Success<Resource, Error>(screen)
            }

        else -> when (identifier.path.getOrNull(1)) {
            null -> suspend {
                val key = identifier.path.first()

                SystemProperties.getString(key)?.let {
                    val element = Element.Item(
                        identifier = identifier,
                        title = LocalizedString(key),
                        value = Value(it),
                    )

                    Result.Success<Resource, Error>(element)
                } ?: Result.Error<Resource, Error>(Error.NOT_FOUND)
            }.asFlow()

            else -> flowOf(Result.Error(Error.NOT_FOUND))
        }
    }
}
