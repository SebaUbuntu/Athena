/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getSerializable
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.recyclerview.SubsectionAdapter
import dev.sebaubuntu.athena.sections.SectionEnum
import dev.sebaubuntu.athena.utils.PermissionsUtils
import dev.sebaubuntu.athena.viewmodels.SectionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SectionFragment : Fragment(R.layout.fragment_section) {
    // View models
    private val model: SectionViewModel by viewModels()

    // Views
    private val linearProgressIndicator by getViewProperty<LinearProgressIndicator>(R.id.linearProgressIndicator)
    private val recyclerView by getViewProperty<RecyclerView>(R.id.recyclerView)

    // Adapters
    private val subsectionAdapter by lazy { SubsectionAdapter() }
    private val pairLayoutManager by lazy { PairLayoutManager(requireContext()) }

    private val permissionsUtils by lazy { PermissionsUtils(requireContext()) }

    private val section by lazy {
        requireArguments().getSerializable(KEY_SECTION_ENUM, SectionEnum::class)!!.clazz
    }

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

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            recyclerView.updatePadding(
                bottom = insets.bottom,
                left = insets.left,
                right = insets.right,
            )

            windowInsets
        }

        recyclerView.adapter = subsectionAdapter
        recyclerView.layoutManager = pairLayoutManager

        if (section.requiredPermissions.isNotEmpty()) {
            permissionsRequestLauncher.launch(section.requiredPermissions)
        } else {
            loadContent()
        }
    }

    override fun onDestroyView() {
        recyclerView.adapter = null
        recyclerView.layoutManager = null

        super.onDestroyView()
    }

    private fun loadContent() {
        viewLifecycleOwner.lifecycleScope.launch {
            model.section.value = section

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.sectionData.collectLatest { subsections ->
                    subsectionAdapter.submitList(subsections)
                    linearProgressIndicator.isVisible = false
                }
            }
        }
    }

    companion object {
        private const val KEY_SECTION_ENUM = "section_enum"

        fun createBundle(
            sectionEnum: SectionEnum,
        ) = bundleOf(
            KEY_SECTION_ENUM to sectionEnum,
        )
    }
}
