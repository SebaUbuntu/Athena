/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.recyclerview

import androidx.recyclerview.widget.DiffUtil
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.vintf.TrebleInterface
import dev.sebaubuntu.athena.ui.dialogs.TrebleInterfaceInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem

class TrebleInterfacesAdapter : SimpleListAdapter<TrebleInterface, ListItem>(
    diffCallback, ::ListItem
) {
    override fun ViewHolder.onPrepareView() {
        view.setTrailingIconImage(R.drawable.ic_arrow_right)
        view.setOnClickListener {
            item?.let {
                TrebleInterfaceInfoAlertDialog(view.context, it).show()
            }
        }
    }

    override fun ViewHolder.onBindView(item: TrebleInterface) {
        view.headlineText = item.name
        view.setSupportingText(item.interfaceTypeStringResId)
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<TrebleInterface>() {
            override fun areItemsTheSame(
                oldItem: TrebleInterface,
                newItem: TrebleInterface
            ) = oldItem.name == newItem.name

            override fun areContentsTheSame(
                oldItem: TrebleInterface,
                newItem: TrebleInterface
            ) = areItemsTheSame(oldItem, newItem)
        }
    }
}
