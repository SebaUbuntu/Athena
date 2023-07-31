/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.*
import dev.sebaubuntu.athena.sections.Section
import dev.sebaubuntu.athena.utils.PermissionsUtils

class SectionFragment : Fragment(R.layout.fragment_section) {
    // Views
    private val linearLayout by getViewProperty<LinearLayout>(R.id.linearLayout)

    private val permissionsUtils by lazy { PermissionsUtils(requireContext()) }

    private val section by lazy { Section.sections[requireArguments().getInt(KEY_SECTION_ID)]!! }

    private val permissionsRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.isNotEmpty()) {
            if (!permissionsUtils.permissionsGranted(section.requiredPermissions)) {
                Toast.makeText(
                    requireContext(), R.string.permissions_not_granted, Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            } else {
                loadContent()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (section.requiredPermissions.isNotEmpty()) {
            permissionsRequestLauncher.launch(section.requiredPermissions)
        } else {
            loadContent()
        }
    }

    private fun loadContent() {
        linearLayout.removeAllViews()

        for ((section, sectionInfo) in section.getInfo(requireContext())) {
            val sectionCardView = SectionCardView(requireContext()).apply {
                titleText = section
            }

            linearLayout.addView(
                sectionCardView,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )

            for ((k, v) in sectionInfo) {
                sectionCardView.addListItem(
                    ListItem(requireContext()).apply {
                        headlineText = k
                        trailingSupportingText = v
                        showDivider = false
                    }
                )
            }
        }
    }

    companion object {
        private const val KEY_SECTION_ID = "section_id"

        fun createBundle(
            sectionId: Int,
        ) = bundleOf(
            KEY_SECTION_ID to sectionId,
        )
    }
}
