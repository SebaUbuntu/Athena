/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.services

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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class ServicesModule : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = ServicesModule()
    }

    override val id = "services"

    override val name = LocalizedString(R.string.section_services_name)

    override val description = LocalizedString(R.string.section_services_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_settings_account_box

    override val requiredPermissions = arrayOf<String>()

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
                title = name,
                elements = services.map {
                    Element.Item(
                        name = it.first,
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
