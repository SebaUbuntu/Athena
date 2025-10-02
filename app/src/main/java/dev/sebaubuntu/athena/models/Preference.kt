/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

/**
 * Preference definition.
 *
 * @param T The type of the preference
 */
sealed interface Preference<T> {
    /**
     * Get the value of the preference from the preferences.
     *
     * @param preferences The [Preferences] to get the value from
     * @return The value of the preference
     */
    fun getValue(preferences: Preferences): T

    /**
     * Update the preferences with the given value.
     *
     * @param mutablePreferences The preferences to update
     * @param value The value of the preference
     */
    suspend fun setValue(mutablePreferences: MutablePreferences, value: T)

    /**
     * Preference backed by a [Preferences.Key].
     *
     * @param T The type of the preference
     */
    sealed class PrimitivePreference<T>(
        private val preferencesKey: Preferences.Key<T & Any>,
        private val defaultValue: T,
    ) : Preference<T> {
        override fun getValue(
            preferences: Preferences,
        ) = when (preferences.contains(preferencesKey)) {
            true -> preferences[preferencesKey] ?: error("Preference value is null")
            false -> defaultValue
        }

        override suspend fun setValue(mutablePreferences: MutablePreferences, value: T) {
            value?.also {
                mutablePreferences[preferencesKey] = it
            } ?: mutablePreferences.remove(preferencesKey)
        }
    }

    /**
     * [Boolean] preference.
     */
    class BooleanPreference<T : Boolean?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        booleanPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * [ByteArray] preference.
     */
    class ByteArrayPreference<T : ByteArray?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        byteArrayPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * [Double] preference.
     */
    class DoublePreference<T : Double?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        doublePreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * [Float] preference.
     */
    class FloatPreference<T : Float?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        floatPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * [Int] preference.
     */
    class IntPreference<T : Int?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        intPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * [Long] preference.
     */
    class LongPreference<T : Long?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        longPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * [String] preference.
     */
    class StringPreference<T : String?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        stringPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * [Set] of [String] preference.
     */
    class StringSetPreference<T : Set<String>?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        stringSetPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * [Enum] preference.
     */
    class EnumPreference<T : E?, E : Enum<E>>(
        name: String,
        private val defaultValue: T,
        private val enumValueOf: (String) -> T,
    ) : Preference<T> {
        private val backingPreference = primitivePreference<String?>(name, null)

        override fun getValue(
            preferences: Preferences
        ) = backingPreference.getValue(preferences)?.let(enumValueOf) ?: defaultValue

        override suspend fun setValue(mutablePreferences: MutablePreferences, value: T) {
            backingPreference.setValue(mutablePreferences, value?.name)
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private fun <T> Preferences.Key<*>.forceCast() = this as Preferences.Key<T>

        @Suppress("UNCHECKED_CAST")
        inline fun <reified T> primitivePreference(
            name: String,
            defaultValue: T,
        ): Preference<T> = when (T::class) {
            Boolean::class -> when (null is T) {
                true -> BooleanPreference(name, defaultValue as Boolean?)
                false -> BooleanPreference(name, defaultValue as Boolean)
            }

            ByteArray::class -> when (null is T) {
                true -> ByteArrayPreference(name, defaultValue as ByteArray?)
                false -> ByteArrayPreference(name, defaultValue as ByteArray)
            }

            Double::class -> when (null is T) {
                true -> DoublePreference(name, defaultValue as Double?)
                false -> DoublePreference(name, defaultValue as Double)
            }

            Float::class -> when (null is T) {
                true -> FloatPreference(name, defaultValue as Float?)
                false -> FloatPreference(name, defaultValue as Float)
            }

            Int::class -> when (null is T) {
                true -> IntPreference(name, defaultValue as Int?)
                false -> IntPreference(name, defaultValue as Int)
            }

            Long::class -> when (null is T) {
                true -> LongPreference(name, defaultValue as Long?)
                false -> LongPreference(name, defaultValue as Long)
            }

            Set::class -> when (null is T) {
                true -> StringSetPreference(name, defaultValue as Set<String>?)
                false -> StringSetPreference(name, defaultValue as Set<String>)
            }

            String::class -> when (null is T) {
                true -> StringPreference(name, defaultValue as String?)
                false -> StringPreference(name, defaultValue as String)
            }

            else -> error("Unsupported type")
        } as Preference<T>

        @Suppress("UNCHECKED_CAST")
        inline fun <T : E?, reified E : Enum<E>> enumPreference(
            name: String,
            defaultValue: T,
        ) = EnumPreference(
            name,
            defaultValue,
        ) { enumValueOf<E>(it) as T }
    }
}
