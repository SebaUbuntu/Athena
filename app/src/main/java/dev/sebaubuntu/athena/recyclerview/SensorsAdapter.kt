/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import android.hardware.Sensor
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ui.dialogs.SensorInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem

class SensorsAdapter : ListAdapter<Sensor, SensorsAdapter.SensorViewHolder>(PAIR_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SensorViewHolder(ListItem(parent.context))

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItem = itemView as ListItem

        private var sensor: Sensor? = null

        init {
            listItem.trailingIconImage = ResourcesCompat.getDrawable(
                listItem.resources, R.drawable.ic_arrow_right, null
            )
            listItem.setOnClickListener {
                sensor?.let {
                    SensorInfoAlertDialog(listItem.context, it).show()
                }
            }
        }

        fun bind(sensor: Sensor) {
            this.sensor = sensor

            listItem.headlineText = sensor.name
            listItem.supportingText = sensor.stringType
        }
    }

    companion object {
        val PAIR_COMPARATOR = object : DiffUtil.ItemCallback<Sensor>() {
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