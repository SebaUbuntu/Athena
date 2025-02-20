/*
 * SPDX-FileCopyrightText: 2023-2025 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.vintf.TrebleInterface
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.recyclerview.SimpleListAdapter
import dev.sebaubuntu.athena.ui.dialogs.TrebleInterfaceInfoAlertDialog
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.viewmodels.TrebleInterfacesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TrebleInterfacesFragment : RecyclerViewFragment() {
    // Views
    private val model: TrebleInterfacesViewModel by viewModels()

    // RecyclerView
    private val trebleInterfacesAdapter by lazy {
        object : SimpleListAdapter<TrebleInterface, ListItem>(
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
        }
    }
    private val gridLayoutManager by lazy { PairLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = trebleInterfacesAdapter
        recyclerView.layoutManager = gridLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.trebleInterfaces.collectLatest {
                    trebleInterfacesAdapter.submitList(it)
                }
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<TrebleInterface>() {
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
