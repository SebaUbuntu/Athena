/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.sections.Section
import dev.sebaubuntu.athena.ui.ListItem
import dev.sebaubuntu.athena.ui.SectionLayout
import dev.sebaubuntu.athena.utils.PermissionsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SectionFragment : Fragment(R.layout.fragment_section) {
    // Views
    private val linearLayout by getViewProperty<LinearLayout>(R.id.linearLayout)

    private val permissionsUtils by lazy { PermissionsUtils(requireContext()) }

    private val section by lazy { Section.sections[requireArguments().getInt(KEY_SECTION_ID)]!! }

    private val ioScope = CoroutineScope(Job() + Dispatchers.IO)

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
        ioScope.run {
            val info = section.getInfo(requireContext())

            viewLifecycleOwner.lifecycleScope.launch {
                linearLayout.removeAllViews()

                for ((section, sectionInfo) in info) {
                    val sectionLayout = SectionLayout(requireContext()).apply {
                        titleText = section
                    }

                    linearLayout.addView(
                        sectionLayout,
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    )

                    for ((k, v) in sectionInfo) {
                        sectionLayout.addListItem(
                            ListItem(requireContext()).apply {
                                headlineText = k
                                v?.takeIf { it.isNotEmpty() }.also {
                                    supportingText = it
                                } ?: run {
                                    setSupportingText(R.string.unknown)
                                }
                            }
                        )
                    }
                }
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
