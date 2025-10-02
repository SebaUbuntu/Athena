/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.models

import android.content.Context
import androidx.annotation.DrawableRes
import kotlinx.coroutines.flow.Flow

/**
 * A component providing screens and data.
 */
interface Module {
    /**
     * [Module] factory.
     */
    interface Factory {
        /**
         * Create a new component.
         *
         * @param context The context
         */
        fun create(context: Context): Module
    }

    /**
     * The ID of the module.
     */
    val id: String

    /**
     * The name of the module.
     */
    val name: LocalizedString

    /**
     * The description of the module.
     */
    val description: LocalizedString

    /**
     * The drawable resource ID of the module.
     */
    @get:DrawableRes
    val drawableResId: Int

    /**
     * The required permissions of the module.
     */
    val requiredPermissions: Array<String>

    /**
     * Resolve a resource.
     *
     * @param identifier The identifier of the resource
     * @return A flow of the result of the operation
     */
    fun resolve(identifier: Resource.Identifier): Flow<Result<Resource, Error>>
}
