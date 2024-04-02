/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Subsection
import dev.sebaubuntu.athena.recyclerview.InformationAdapter
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager

class SubsectionLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    // Views
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView)!! }
    private val titleTextView by lazy { findViewById<TextView>(R.id.titleTextView)!! }

    // Adapters
    private val informationAdapter by lazy { InformationAdapter() }
    private val pairLayoutManager by lazy { PairLayoutManager(context) }

    var subsection: Subsection? = null
        set(value) {
            if (field == value) {
                return
            }

            field = value

            informationAdapter.submitList(null)

            value?.let { subsection ->
                titleTextView.text = subsection.getDisplayTitle(context)

                informationAdapter.submitList(subsection.information)
            }
        }

    init {
        inflate(context, R.layout.subsection_layout, this)

        recyclerView.adapter = informationAdapter
        recyclerView.layoutManager = pairLayoutManager
    }
}
