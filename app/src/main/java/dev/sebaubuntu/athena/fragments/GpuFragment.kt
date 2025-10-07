/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.recyclerview.PairLayoutManager
import dev.sebaubuntu.athena.recyclerview.SubsectionAdapter
import dev.sebaubuntu.athena.sections.GpuSection
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.viewmodels.GpuViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GpuFragment : Fragment(R.layout.fragment_gpu) {
    // View models
    private val model: GpuViewModel by viewModels()

    // Views
    private val glSurfaceView by getViewProperty<GLSurfaceView>(R.id.glSurfaceView)
    private val gpuExtensionsListItem by getViewProperty<ListItem>(R.id.gpuExtensionsListItem)
    private val gpuRendererListItem by getViewProperty<ListItem>(R.id.gpuRendererListItem)
    private val gpuVendorListItem by getViewProperty<ListItem>(R.id.gpuVendorListItem)
    private val gpuVersionListItem by getViewProperty<ListItem>(R.id.gpuVersionListItem)
    private val recyclerView by getViewProperty<RecyclerView>(R.id.recyclerView)

    // Adapters
    private val subsectionAdapter by lazy { SubsectionAdapter() }
    private val pairLayoutManager by lazy { PairLayoutManager(requireContext()) }

    private val section = GpuSection

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

        recyclerView.adapter = subsectionAdapter
        recyclerView.layoutManager = pairLayoutManager

        model.gpuRenderer.observe(viewLifecycleOwner) { gpuRenderer: String ->
            gpuRendererListItem.supportingText = gpuRenderer
        }
        model.gpuVendor.observe(viewLifecycleOwner) { gpuVendor: String ->
            gpuVendorListItem.supportingText = gpuVendor
        }
        model.gpuVersion.observe(viewLifecycleOwner) { gpuVersion: String ->
            gpuVersionListItem.supportingText = gpuVersion
        }
        model.gpuExtensions.observe(viewLifecycleOwner) { gpuExtensions: String ->
            gpuExtensionsListItem.supportingText = gpuExtensions
        }

        model.gpuParsingCompleted.observe(viewLifecycleOwner) { gpuParsingCompleted: Boolean ->
            glSurfaceView.isVisible = !gpuParsingCompleted
        }

        glSurfaceView.setRenderer(model.glRenderer)
        glSurfaceView.requestRender()

        viewLifecycleOwner.lifecycleScope.launch {
            model.section.value = section

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.sectionData.collectLatest { subsections ->
                    subsectionAdapter.submitList(subsections)
                }
            }
        }
    }

    override fun onDestroyView() {
        recyclerView.adapter = null
        recyclerView.layoutManager = null

        super.onDestroyView()
    }
}
