/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.ext.stringRes
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.utils.DeviceInfo

class TrebleFragment : Fragment(R.layout.fragment_treble) {
    // Views
    private val interfacesListItem by getViewProperty<ListItem>(R.id.interfacesListItem)
    private val trebleEnabledListItem by getViewProperty<ListItem>(R.id.trebleEnabledListItem)
    private val vndkVersionListItem by getViewProperty<ListItem>(R.id.vndkVersionListItem)

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

        trebleEnabledListItem.supportingText = getString(DeviceInfo.trebleEnabled.stringRes)
        vndkVersionListItem.supportingText = DeviceInfo.vndkVersion

        interfacesListItem.setOnClickListener {
            findNavController().navigate(R.id.action_trebleFragment_to_trebleInterfacesFragment)
        }
    }
}
