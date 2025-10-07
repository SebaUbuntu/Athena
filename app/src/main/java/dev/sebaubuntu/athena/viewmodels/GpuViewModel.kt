/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import android.opengl.GLSurfaceView
import androidx.lifecycle.MutableLiveData
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GpuViewModel(application: Application) : SectionViewModel(application) {
    val gpuRenderer = MutableLiveData<String>()
    val gpuVendor = MutableLiveData<String>()
    val gpuVersion = MutableLiveData<String>()
    val gpuExtensions = MutableLiveData<String>()

    val gpuParsingCompleted = MutableLiveData<Boolean>()

    val glRenderer = object : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            gl?.let {
                gpuRenderer.postValue(it.glGetString(GL10.GL_RENDERER))
                gpuVendor.postValue(it.glGetString(GL10.GL_VENDOR))
                gpuVersion.postValue(it.glGetString(GL10.GL_VERSION))
                gpuExtensions.postValue(it.glGetString(GL10.GL_EXTENSIONS))

                gpuParsingCompleted.postValue(true)
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
