/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category
import dev.sebaubuntu.athena.utils.PermissionsUtils

class CategoryFragment(private val categoryId: Int) : Fragment(R.layout.fragment_category) {
    // Views
    private val rootView by lazy { requireView() as NestedScrollView }
    private val linearLayout by lazy { rootView.findViewById<LinearLayout>(R.id.linearLayout) }

    private val permissionsUtils by lazy { PermissionsUtils(requireContext()) }

    private val category = Category.categories[categoryId]!!

    private val permissionsRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.isNotEmpty()) {
            if (!permissionsUtils.permissionsGranted(category.requiredPermissions)) {
                linearLayout.addView(getSectionTitle("Permissions not granted"))
            } else {
                loadContent()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (category.requiredPermissions.isNotEmpty()) {
            permissionsRequestLauncher.launch(category.requiredPermissions)
        } else {
            loadContent()
        }
    }

    private fun loadContent() {
        linearLayout.removeAllViews()

        for ((section, sectionInfo) in category.getCachedInfo(requireContext())) {
            linearLayout.addView(
                getSectionTitle(section),
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )

            for ((k, v) in sectionInfo) {
                linearLayout.addView(
                    ListItem(requireContext()).apply {
                        headlineText = k
                        trailingSupportingText = v
                        showDivider = false
                    },
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }
    }

    private fun getSectionTitle(title: String) =
        (layoutInflater.inflate(
            R.layout.section_title_text_view, linearLayout, false
        ) as TextView).apply {
            text = title
        }
}
