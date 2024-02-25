/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.Manifest
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.recyclerview.SensorsAdapter
import dev.sebaubuntu.athena.utils.PermissionsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SensorsFragment : RecyclerViewFragment() {
    // Recyclerview
    private val sensorsAdapter by lazy { SensorsAdapter() }
    private val pairLayoutManager by lazy { PairLayoutManager(requireContext()) }

    // System services
    private val sensorManager
        get() = requireContext().getSystemService(SensorManager::class.java)

    // Permissions
    private val permissionsUtils by lazy { PermissionsUtils(requireContext()) }
    private val permissionsRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.isNotEmpty()) {
            if (!permissionsUtils.permissionsGranted(optionalPermissions)) {
                Snackbar.make(
                    requireView(),
                    R.string.body_sensors_permission_not_granted,
                    Snackbar.LENGTH_SHORT
                ).apply {
                    this.setAction(android.R.string.ok) {
                        // Do nothing
                    }
                }.show()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val sensors = withContext(Dispatchers.IO) {
                    sensorManager.getSensorList(Sensor.TYPE_ALL)
                }
                sensorsAdapter.submitList(sensors)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = sensorsAdapter
        recyclerView.layoutManager = pairLayoutManager

        permissionsRequestLauncher.launch(optionalPermissions)
    }

    companion object {
        private val optionalPermissions = arrayOf(
            Manifest.permission.BODY_SENSORS,
        )
    }
}
