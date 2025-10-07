/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty

/**
 * A fragment that holds a single [RecyclerView].
 */
abstract class RecyclerViewFragment : Fragment(R.layout.fragment_recycler_view) {
    // Views
    protected val recyclerView by getViewProperty<RecyclerView>(R.id.recyclerView)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            recyclerView.updatePadding(
                bottom = insets.bottom,
                left = insets.left,
                right = insets.right,
            )

            windowInsets
        }
    }

    @CallSuper
    override fun onDestroyView() {
        // Remove everything that could be bound to the recycler view
        recyclerView.layoutManager = null
        recyclerView.adapter = null

        super.onDestroyView()
    }
}
