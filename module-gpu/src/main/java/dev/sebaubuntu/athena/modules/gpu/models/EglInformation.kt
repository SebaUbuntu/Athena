/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.gpu.models

data class EglInformation(
    val eglVendor: String?,
    val eglVersion: String?,
    val eglExtensions: List<String>?,
    val eglClientApi: List<String>?,
    val glInformation: GlInformation?,
) {
    class Builder(
        private val eglVendor: String?,
        private val eglVersion: String?,
        private val eglExtensions: String?,
        private val eglClientApi: String?,
    ) {
        private var glInformation: GlInformation? = null

        fun addGlInformation(
            glVendor: String?,
            glRenderer: String?,
            glVersion: String?,
            glExtensions: String?,
        ) {
            glInformation = GlInformation(
                glVendor,
                glRenderer,
                glVersion,
                glExtensions?.split(" ")?.toTypedArray(),
            )
        }

        fun build() = EglInformation(
            eglVendor = eglVendor,
            eglVersion = eglVersion,
            eglExtensions = eglExtensions?.split(" "),
            eglClientApi = eglClientApi?.split(" "),
            glInformation = glInformation,
        )
    }
}
