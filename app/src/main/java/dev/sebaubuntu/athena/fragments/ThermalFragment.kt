/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import androidx.fragment.app.Fragment
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.ui.ListItem

class ThermalFragment : Fragment(R.layout.fragment_thermal) {
    // Views
    private val thermalStatusListItem by getViewProperty<ListItem>(R.id.thermalMitigationListItem)

    // System services
    private val powerManager by lazy {
        requireContext().getSystemService(PowerManager::class.java)
    }

    // Thermal status listener
    private val thermalStatusListener by lazy {
        PowerManager.OnThermalStatusChangedListener {
            activity?.runOnUiThread {
                thermalStatusListItem.supportingText = resources.getString(
                    when (it) {
                        PowerManager.THERMAL_STATUS_NONE -> R.string.thermal_status_none
                        PowerManager.THERMAL_STATUS_LIGHT -> R.string.thermal_status_light
                        PowerManager.THERMAL_STATUS_MODERATE -> R.string.thermal_status_moderate
                        PowerManager.THERMAL_STATUS_SEVERE -> R.string.thermal_status_severe
                        PowerManager.THERMAL_STATUS_CRITICAL -> R.string.thermal_status_critical
                        PowerManager.THERMAL_STATUS_EMERGENCY -> R.string.thermal_status_emergency
                        PowerManager.THERMAL_STATUS_SHUTDOWN -> R.string.thermal_status_shutdown
                        else -> R.string.unknown
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager.addThermalStatusListener(thermalStatusListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager.removeThermalStatusListener(thermalStatusListener)
        }
    }
}
