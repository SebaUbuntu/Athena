/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.mutablePreferencesOf
import dev.sebaubuntu.athena.models.Preference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import okio.Path.Companion.toPath

/**
 * App preferences manager.
 *
 * @param preferenceDataStore [DataStore] containing the preferences.
 */
class PreferencesManager(private val preferenceDataStore: DataStore<Preferences>) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> valueFlow(preference: Preference<T>) = preferenceDataStore.data
        .mapLatest(preference::getValue)

    suspend fun <T> getValue(preference: Preference<T>) = valueFlow(preference).first()

    suspend fun <T> setValue(preference: Preference<T>, value: T) {
        preferenceDataStore.edit {
            preference.setValue(it, value)
        }
    }

    companion object {
        private const val DATASTORE_FILE_NAME = "athena.preferences_pb"

        fun get(context: Context) = PreferencesManager(
            PreferenceDataStoreFactory.createWithPath(
                corruptionHandler = ReplaceFileCorruptionHandler { mutablePreferencesOf() },
            ) {
                context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath()
            }
        )
    }
}
