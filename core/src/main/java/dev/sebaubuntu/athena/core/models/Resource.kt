/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.models

import android.net.Uri

/**
 * A resource.
 */
sealed interface Resource {
    /**
     * A resource identifier.
     *
     * @param module The [Module] ID that handles this resource, null for root
     * @param path The path of the resource
     */
    data class Identifier(
        val module: String?,
        val path: List<String>,
    ) {
        fun toUri(): Uri = Uri.Builder().apply {
            scheme(SCHEME)
            module?.let(::authority)
            path.forEach(::appendPath)
        }.build()

        operator fun div(pathSegment: String) = Identifier(
            module = module,
            path = path.plus(pathSegment),
        )

        companion object {
            const val SCHEME = "athena"

            /**
             * The root of the tree.
             */
            val ROOT = Identifier(null, listOf())

            /**
             * Get an [Identifier] from a [Uri].
             */
            fun fromUri(uri: Uri): Identifier {
                require(uri.scheme == SCHEME) { "Invalid scheme: ${uri.scheme}" }

                val module = uri.authority ?: return ROOT
                val path = uri.pathSegments

                return Identifier(
                    module = module,
                    path = path,
                )
            }
        }
    }

    /**
     * The identifier of this resource.
     */
    val identifier: Identifier
}
