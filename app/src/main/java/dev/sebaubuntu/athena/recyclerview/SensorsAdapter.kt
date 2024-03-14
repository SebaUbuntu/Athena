/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import android.hardware.Sensor
import androidx.recyclerview.widget.DiffUtil
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ui.dialogs.SensorInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem

class SensorsAdapter : SimpleListAdapter<Sensor, ListItem>(diffCallback, ListItem::class.java) {
    override fun SimpleListAdapter<Sensor, ListItem>.ViewHolder.onPrepareView() {
        view.setTrailingIconImage(R.drawable.ic_arrow_right)
        view.setOnClickListener {
            item?.let {
                SensorInfoAlertDialog(view.context, it).show()
            }
        }
    }

    override fun SimpleListAdapter<Sensor, ListItem>.ViewHolder.onBindView(item: Sensor) {
        view.headlineText = item.name
        view.supportingText = item.stringType
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Sensor>() {
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
