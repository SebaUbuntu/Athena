/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.repositories

import dev.sebaubuntu.athena.models.Preference
import dev.sebaubuntu.athena.models.Preference.Companion.enumPreference
import dev.sebaubuntu.athena.models.Preference.Companion.primitivePreference
import dev.sebaubuntu.athena.models.Theme
import dev.sebaubuntu.athena.utils.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * User preferences repository.
 */
class PreferencesRepository(
    private val preferencesManager: PreferencesManager,
    coroutineScope: CoroutineScope,
) : Repository(coroutineScope) {
    inner class PreferenceHolder<T>(
        private val preference: Preference<T>,
    ) : Flow<T> by preferencesManager.valueFlow(preference) {
        suspend fun getValue() = preferencesManager.getValue(preference)

        suspend fun setValue(value: T) = preferencesManager.setValue(preference, value)
    }

    // App theming

    val theme = enumPreference(
        "theme",
        Theme.SYSTEM,
    ).asPreferenceHolder()

    val dynamicColors = primitivePreference(
        "dynamic_colors",
        true,
    ).asPreferenceHolder()

    private fun <T> Preference<T>.asPreferenceHolder() = PreferenceHolder(this)
}
