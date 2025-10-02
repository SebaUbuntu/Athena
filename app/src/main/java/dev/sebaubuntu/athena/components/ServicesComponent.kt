/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class ServicesComponent : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = ServicesComponent()
    }

    override val name = "services"

    override val title = LocalizedString(R.string.section_services_name)

    override val description = LocalizedString(R.string.section_services_description)

    override val drawableResId = R.drawable.ic_settings_account_box

    override val permissions = setOf<Permission>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val services = SystemProperties.getProps().mapNotNull { (key, value) ->
                when (key.startsWith(INIT_SERVICE_PREFIX)) {
                    true -> key.removePrefix(INIT_SERVICE_PREFIX) to value
                    false -> null
                }
            }.sortedBy { it.first }

            val screen = Screen.ItemListScreen(
                identifier = identifier,
                title = title,
                elements = services.map {
                    Element.Item(
                        identifier = identifier / it.first,
                        title = LocalizedString(it.first),
                        value = Value(it.second),
                    )
                },
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }

    companion object {
        private const val INIT_SERVICE_PREFIX = "init.svc."
    }
}
