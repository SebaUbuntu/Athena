/*
 * SPDX-FileCopyrightText: 2024-2025 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.Manifest
import android.hardware.Sensor
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.snackbar.Snackbar
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.sensorType
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.recyclerview.SimpleListAdapter
import dev.sebaubuntu.athena.ui.dialogs.SensorInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.utils.PermissionsUtils
import dev.sebaubuntu.athena.viewmodels.SensorsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SensorsFragment : RecyclerViewFragment() {
    // View models
    private val model: SensorsViewModel by viewModels()

    // RecyclerView
    private val sensorsAdapter by lazy {
        object : SimpleListAdapter<Sensor, ListItem>(
            diffCallback, ::ListItem
        ) {
            override fun SimpleListAdapter<Sensor, ListItem>.ViewHolder.onPrepareView() {
                view.setTrailingIconImage(R.drawable.ic_arrow_right)
                view.setOnClickListener {
                    item?.let {
                        SensorInfoAlertDialog(view.context, it).show()
                    }
                }
            }

            override fun SimpleListAdapter<Sensor, ListItem>.ViewHolder.onBindView(item: Sensor) {
                val sensorType = item.sensorType

                view.setLeadingIconImage(sensorType?.drawableResId ?: R.drawable.ic_sensors)

                sensorType?.stringResId?.also {
                    view.setHeadlineText(it)
                    view.supportingText = item.name
                } ?: run {
                    view.headlineText = item.name
                    view.supportingText = item.stringType
                }
            }
        }
    }
    private val pairLayoutManager by lazy { PairLayoutManager(requireContext()) }

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
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    model.sensors.collectLatest { sensors ->
                        sensorsAdapter.submitList(sensors)
                    }
                }
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

        private val diffCallback = object : DiffUtil.ItemCallback<Sensor>() {
            override fun areItemsTheSame(
                oldItem: Sensor,
                newItem: Sensor
            ) = oldItem.name == newItem.name

            override fun areContentsTheSame(
                oldItem: Sensor,
                newItem: Sensor
            ) = oldItem.name == newItem.name
        }
    }
}
