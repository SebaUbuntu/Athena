/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.ui.ListItem
import dev.sebaubuntu.athena.viewmodels.GpuViewModel
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GpuFragment : Fragment(R.layout.fragment_gpu) {
    // View models
    private val model by viewModels<GpuViewModel>()

    // Views
    private val glSurfaceView by getViewProperty<GLSurfaceView>(R.id.glSurfaceView)
    private val gpuExtensionsListItem by getViewProperty<ListItem>(R.id.gpuExtensionsListItem)
    private val gpuRendererListItem by getViewProperty<ListItem>(R.id.gpuRendererListItem)
    private val gpuVendorListItem by getViewProperty<ListItem>(R.id.gpuVendorListItem)
    private val gpuVersionListItem by getViewProperty<ListItem>(R.id.gpuVersionListItem)

    // Renderer
    private val glRenderer by lazy { GLRenderer(model) }

    // Observers
    private val gpuRendererObserver = Observer { gpuRenderer: String ->
        gpuRendererListItem.supportingText = gpuRenderer
    }

    private val gpuVendorObserver = Observer { gpuVendor: String ->
        gpuVendorListItem.supportingText = gpuVendor
    }

    private val gpuVersionObserver = Observer { gpuVersion: String ->
        gpuVersionListItem.supportingText = gpuVersion
    }

    private val gpuExtensionsObserver = Observer { gpuExtensions: String ->
        gpuExtensionsListItem.supportingText = gpuExtensions
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.gpuRenderer.observe(viewLifecycleOwner, gpuRendererObserver)
        model.gpuVendor.observe(viewLifecycleOwner, gpuVendorObserver)
        model.gpuVersion.observe(viewLifecycleOwner, gpuVersionObserver)
        model.gpuExtensions.observe(viewLifecycleOwner, gpuExtensionsObserver)

        glSurfaceView.setRenderer(glRenderer)
        glSurfaceView.requestRender()
    }

    class GLRenderer(private val model: GpuViewModel) : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            gl?.let {
                model.gpuRenderer.postValue(it.glGetString(GL10.GL_RENDERER))
                model.gpuVendor.postValue(it.glGetString(GL10.GL_VENDOR))
                model.gpuVersion.postValue(it.glGetString(GL10.GL_VERSION))
                model.gpuExtensions.postValue(it.glGetString(GL10.GL_EXTENSIONS))
            }
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            // Do nothing
        }

        override fun onDrawFrame(gl: GL10?) {
            // Do nothing
        }
    }
}
