/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.thermal

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.modules.thermal.ext.thermalStatusFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

class ThermalModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = ThermalModule(context)
    }

    private val powerManager = context.getSystemService(PowerManager::class.java)

    override val id = "thermal"

    override val name = LocalizedString(R.string.section_thermal_name)

    override val description = LocalizedString(R.string.section_thermal_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_thermostat

    override val requiredPermissions = arrayOf<String>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> channelFlow {
            var thermalStatus: Int? = null

            val updateData = {
                val screen = Screen.CardListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.section_thermal_name),
                    elements = listOf(
                        Element.Card(
                            name = "general",
                            title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                            elements = listOf(
                                Element.Item(
                                    name = "thermal_status",
                                    title = LocalizedString(R.string.thermal_status),
                                    value = thermalStatus?.let {
                                        Value(
                                            it,
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                                thermalStatusToStringResId
                                            } else {
                                                null
                                            },
                                        )
                                    } ?: Value(
                                        "unsupported",
                                        R.string.thermal_status_unsupported,
                                    )
                                )
                            )
                        )
                    )
                )

                trySend(
                    Result.Success<Resource, Error>(screen)
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                powerManager.thermalStatusFlow().collectLatest {
                    thermalStatus = it
                    updateData()
                }
            } else {
                updateData()
            }
        }

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val thermalStatusToStringResId = mapOf(
        PowerManager.THERMAL_STATUS_NONE to R.string.thermal_status_none,
        PowerManager.THERMAL_STATUS_LIGHT to R.string.thermal_status_light,
        PowerManager.THERMAL_STATUS_MODERATE to R.string.thermal_status_moderate,
        PowerManager.THERMAL_STATUS_SEVERE to R.string.thermal_status_severe,
        PowerManager.THERMAL_STATUS_CRITICAL to R.string.thermal_status_critical,
        PowerManager.THERMAL_STATUS_EMERGENCY to R.string.thermal_status_emergency,
        PowerManager.THERMAL_STATUS_SHUTDOWN to R.string.thermal_status_shutdown,
    )
}
