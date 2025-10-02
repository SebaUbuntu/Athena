/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.systemproperties

import android.content.Context
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.modules.systemproperties.utils.SystemProperties
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class SystemPropertiesModule : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = SystemPropertiesModule()
    }

    override val id = "system_properties"

    override val name = LocalizedString(R.string.section_props_name)

    override val description = LocalizedString(R.string.section_props_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_build

    override val requiredPermissions = arrayOf<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> SystemProperties::getProps.asFlow()
            .mapLatest { props ->
                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = name,
                    elements = props.map { prop ->
                        Element.Item(
                            name = prop.key,
                            title = LocalizedString(prop.key),
                            value = Value(prop.value),
                        )
                    }
                )

                Result.Success<Resource, Error>(screen)
            }

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }
}
