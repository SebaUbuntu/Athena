/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import androidx.recyclerview.widget.DiffUtil
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ui.dialogs.TrebleInterfaceInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.vintf.HIDLInterface

class TrebleInterfacesAdapter : SimpleListAdapter<HIDLInterface, ListItem>(
    diffCallback, ListItem::class.java
) {
    override fun ViewHolder.onPrepareView() {
        view.setTrailingIconImage(R.drawable.ic_arrow_right)
        view.setOnClickListener {
            item?.let {
                TrebleInterfaceInfoAlertDialog(view.context, it).show()
            }
        }
    }

    override fun ViewHolder.onBindView(item: HIDLInterface) {
        view.headlineText = item.name
        view.supportingText = item.transport.name
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<HIDLInterface>() {
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
