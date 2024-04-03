/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.thermalStatusFlow
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

object ThermalSection : Section() {
    override val title = R.string.section_thermal_name
    override val description = R.string.section_thermal_description
    override val icon = R.drawable.ic_thermostat

    override fun dataFlow(context: Context) = channelFlow {
        var thermalStatus: Int? = null

        val updateData = {
            trySend(
                listOf(
                    Subsection(
                        "general",
                        listOf(
                            Information(
                                "thermal_status",
                                thermalStatus?.let {
                                    InformationValue.IntValue(
                                        it,
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            thermalStatusToStringResId
                                        } else {
                                            null
                                        }
                                    )
                                } ?: InformationValue.StringResValue(
                                    R.string.thermal_status_unsupported,
                                    null,
                                    "unsupported",
                                ),
                                R.string.thermal_status,
                            )
                        ),
                        R.string.thermal_general,
                    )
                )
            )
        }

        updateData()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getSystemService(PowerManager::class.java).thermalStatusFlow().collectLatest {
                thermalStatus = it
                updateData()
            }
        }
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
