/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models

import dev.sebaubuntu.athena.core.models.Resource

sealed interface NavDestination {
    data class Resource(val identifier: Resource.Identifier) : NavDestination

    data object Settings : NavDestination

    companion object {
        val DEFAULT = Resource(
            dev.sebaubuntu.athena.core.models.Resource.Identifier.ROOT
        )
    }
}
