/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

object SystemProperties {
    private const val LOG_TAG = "SystemProperties"

    /**
     * From system/libbase/parsebool.cpp
     */
    private val YES_VALUES = setOf(
        "1",
        "y",
        "yes",
        "on",
        "true",
    )

    /**
     * From system/libbase/parsebool.cpp
     */
    private val NO_VALUES = setOf(
        "0",
        "n",
        "no",
        "off",
        "false",
    )

    private abstract class Provider {
        /**
         * Return whether or not the implementation works.
         */
        abstract fun isValid(): Boolean

        /**
         * Get a string property.
         * Implementation must do everything possible to figure out if the property exists
         * (empty string is considered existing).
         *
         * @param key The property key
         * @return The property value or null if it doesn't exist
         */
        abstract fun getString(key: String): String?
    }

    @Suppress("PrivateApi")
    private object SystemPropertiesReflectionProvider : Provider() {
        private const val CLASS_NAME = "android.os.SystemProperties"

        private const val DEFAULT = "default"
        private const val ALT_DEFAULT = "alt_default"

        private val systemPropertiesClass = runCatching {
            Class.forName(CLASS_NAME)
        }.onFailure {
            Log.e(LOG_TAG, "Failed to get $CLASS_NAME", it)
        }.getOrNull()

        private val _getMethod = runCatching {
            systemPropertiesClass?.getMethod(
                "get", String::class.java, String::class.java
            )
        }.getOrNull()
        private val getMethod get() = _getMethod!!

        override fun isValid() = listOf(
            systemPropertiesClass,
            _getMethod,
        ).all { it != null }

        override fun getString(key: String): String? {
            val value = getMethod.invoke(systemPropertiesClass, key, DEFAULT) as String
            val altValue = getMethod.invoke(systemPropertiesClass, key, ALT_DEFAULT) as String

            return value.takeIf {
                // If both values stays the same when changing default value, something is set
                // and the value didn't change between the calls
                it == altValue
            } ?: when (value == DEFAULT && altValue == ALT_DEFAULT) {
                true -> null // Both are equal to the defaults
                false -> {
                    // Let's just repeat the process, we checked at a bad time
                    getString(key)
                }
            }
        }
    }

    private object GetPropProvider : Provider() {
        private const val GETPROP_EXECUTABLE_PATH = "/system/bin/getprop"
        private val GETPROP_LINE_PATTERN = Pattern.compile("\\[(.*?)]: \\[(.*?)]")

        override fun isValid() = true

        override fun getString(key: String) = getProps()[key]

        fun getProps() = mutableMapOf<String, String>().apply {
            var process: Process? = null

            try {
                process = ProcessBuilder()
                    .command(GETPROP_EXECUTABLE_PATH)
                    .redirectErrorStream(true)
                    .start()

                process?.let { p ->
                    BufferedReader(InputStreamReader(p.inputStream)).forEachLine { line ->
                        val matcher = GETPROP_LINE_PATTERN.matcher(line)
                        if (matcher.find()) {
                            matcher.group(1)?.let { key ->
                                this[key] = matcher.group(2) ?: ""
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to read system properties: $e")
            } finally {
                process?.destroy()
            }
        }.toMap()
    }

    private object DummyProvider : Provider() {
        override fun isValid() = true

        override fun getString(key: String) = null
    }

    private val provider = listOf(
        SystemPropertiesReflectionProvider,
        GetPropProvider,
    ).firstOrNull {
        it.isValid()
    } ?: run {
        Log.e(LOG_TAG, "No working system properties provider found")

        DummyProvider
    }

    fun getProps() = GetPropProvider.getProps()

    fun getString(key: String) = provider.getString(key)

    fun getString(key: String, default: String) = getString(key) ?: default

    fun getInt(key: String) = provider.getString(key)?.let { parseNumber(it, String::toIntOrNull) }

    fun getInt(key: String, default: Int) = getInt(key) ?: default

    fun getLong(key: String) =
        provider.getString(key)?.let { parseNumber(it, String::toLongOrNull) }

    fun getLong(key: String, default: Long) = getLong(key) ?: default

    fun getBoolean(key: String) = getString(key)?.let {
        when {
            YES_VALUES.contains(it) -> true
            NO_VALUES.contains(it) -> false
            else -> null
        }
    }

    fun getBoolean(key: String, default: Boolean) = getBoolean(key) ?: default

    private inline fun <reified T : Number> parseNumber(
        value: String, toTOrNull: String.(radix: Int) -> T?
    ): T? {
        val trimmedValue = value.trimStart()

        if (trimmedValue.isEmpty()) {
            return null
        }

        val isHex = trimmedValue.length > 2
                && trimmedValue[0] == '0'
                && trimmedValue[1] in listOf('x', 'X')

        val radix = when (isHex) {
            true -> 16
            false -> 10
        }

        return trimmedValue.toTOrNull(radix)
    }
}
