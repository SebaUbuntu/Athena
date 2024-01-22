/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.recyclerview.SensorsAdapter
import kotlinx.coroutines.launch

class SensorsFragment : Fragment(R.layout.fragment_sensors) {
    // Views
    private val sensorsRecyclerView by getViewProperty<RecyclerView>(R.id.sensorsRecyclerView)

    // Recyclerview
    private val sensorsAdapter by lazy { SensorsAdapter() }
    private val pairLayoutManager by lazy { PairLayoutManager(requireContext()) }

    // System services
    private val sensorManager
        get() = requireContext().getSystemService(SensorManager::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorsRecyclerView.adapter = sensorsAdapter
        sensorsRecyclerView.layoutManager = pairLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
            sensorsAdapter.submitList(sensors)
        }
    }
}
