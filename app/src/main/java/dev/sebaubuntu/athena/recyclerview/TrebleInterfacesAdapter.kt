/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ui.dialogs.TrebleInterfaceInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.vintf.HIDLInterface

class TrebleInterfacesAdapter :
    ListAdapter<HIDLInterface, TrebleInterfacesAdapter.TrebleInterfaceViewHolder>(PAIR_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TrebleInterfaceViewHolder(ListItem(parent.context))

    override fun onBindViewHolder(holder: TrebleInterfaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TrebleInterfaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItem = itemView as ListItem

        private var trebleInterface: HIDLInterface? = null

        init {
            listItem.trailingIconImage = ResourcesCompat.getDrawable(
                listItem.resources, R.drawable.ic_arrow_right, null
            )
            listItem.setOnClickListener {
                trebleInterface?.let {
                    TrebleInterfaceInfoAlertDialog(listItem.context, it).show()
                }
            }
        }

        fun bind(trebleInterface: HIDLInterface) {
            this.trebleInterface = trebleInterface

            listItem.headlineText = trebleInterface.name
            listItem.supportingText = trebleInterface.transport.name
        }
    }

    companion object {
        val PAIR_COMPARATOR = object : DiffUtil.ItemCallback<HIDLInterface>() {
            override fun areItemsTheSame(
                oldItem: HIDLInterface,
                newItem: HIDLInterface
            ) = oldItem.name == newItem.name

            override fun areContentsTheSame(
                oldItem: HIDLInterface,
                newItem: HIDLInterface
            ) = oldItem == newItem
        }
    }
}
