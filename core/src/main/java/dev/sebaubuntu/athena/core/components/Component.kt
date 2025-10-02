/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.components

import android.content.Context
import androidx.annotation.DrawableRes
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Permission
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import kotlinx.coroutines.flow.Flow

/**
 * A component providing screens and data.
 */
interface Component {
    /**
     * [Component] factory.
     */
    interface Factory {
        /**
         * Create a new component.
         *
         * @param context The context
         */
        fun create(context: Context): Component
    }

    /**
     * The name of the component.
     */
    val name: String

    /**
     * The title of the component.
     */
    val title: LocalizedString

    /**
     * The description of the component.
     */
    val description: LocalizedString

    /**
     * The drawable resource ID of the component.
     */
    @get:DrawableRes
    val drawableResId: Int

    /**
     * The required permissions of the component.
     */
    val permissions: Set<Permission>

    /**
     * Resolve a resource.
     *
     * @param identifier The identifier of the resource
     * @return A flow of the result of the operation
     */
    fun resolve(identifier: Resource.Identifier): Flow<Result<Resource, Error>>
}
