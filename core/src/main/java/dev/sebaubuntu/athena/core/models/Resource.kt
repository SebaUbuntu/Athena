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
     * @param component The component that handles this resource, null for root
     * @param path The path of the resource
     * @param queryArgs The query arguments of the resource
     * @param fragment The fragment of the resource
     */
    data class Identifier(
        val component: String?,
        val path: List<String> = listOf(),
        val queryArgs: Map<String, String> = mapOf(),
        val fragment: String? = null,
    ) {
        fun toUri(): Uri = Uri.Builder().apply {
            scheme(SCHEME)
            component?.let(::authority)
            path.forEach {
                appendPath(it)
            }
            queryArgs.forEach {
                appendQueryParameter(it.key, it.value)
            }
            fragment?.let(::fragment)
        }.build()

        operator fun div(pathSegment: String) = Identifier(
            component = component,
            path = path.plus(pathSegment),
            queryArgs = mapOf(),
            fragment = null,
        )

        companion object {
            const val SCHEME = "athena"

            /**
             * The root of the tree.
             */
            val ROOT = Identifier(null)

            /**
             * Get an [Identifier] from a [Uri].
             */
            fun fromUri(uri: Uri): Identifier {
                require(uri.scheme == SCHEME) { "Invalid scheme: ${uri.scheme}" }

                val component = uri.authority ?: return ROOT
                val path = uri.pathSegments
                val queryArgs = uri.queryParameterNames.associateWith {
                    uri.getQueryParameter(it)!!
                }
                val fragment = uri.fragment

                return Identifier(
                    component = component,
                    path = path,
                    queryArgs = queryArgs,
                    fragment = fragment,
                )
            }
        }
    }

    /**
     * The identifier of this resource.
     */
    val identifier: Identifier
}
