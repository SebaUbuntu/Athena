/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.viewmodels.ThermalViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ThermalFragment : Fragment(R.layout.fragment_thermal) {
    // View models
    private val model: ThermalViewModel by viewModels()

    // Views
    private val thermalStatusListItem by getViewProperty<ListItem>(R.id.thermalStatusListItem)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(
                bottom = insets.bottom,
                left = insets.left,
                right = insets.right,
            )

            windowInsets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    model.thermalStatus.collectLatest {
                        thermalStatusListItem.setSupportingText(
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
        }
    }
}
