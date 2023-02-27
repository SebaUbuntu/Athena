/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale
import java.util.regex.Pattern

object SystemProperties {
    private const val LOG_TAG = "Athena"

    private const val GETPROP_EXECUTABLE_PATH = "/system/bin/getprop"
    private val GETPROP_LINE_PATTERN = Pattern.compile("\\[(.*?)]: \\[(.*?)]")

    val props by lazy {
        mutableMapOf<String, String>().apply {
            var process: Process? = null
            var bufferedReader: BufferedReader? = null

            try {
                process = ProcessBuilder()
                    .command(GETPROP_EXECUTABLE_PATH)
                    .redirectErrorStream(true)
                    .start()

                process?.let { p ->
                    bufferedReader = BufferedReader(InputStreamReader(p.inputStream))
                    bufferedReader?.forEachLine { line ->
                        val matcher = GETPROP_LINE_PATTERN.matcher(line)
                        if (matcher.find()) {
                            val key = matcher.group(1)
                            val value = matcher.group(2) ?: ""

                            key?.let {
                                this[it] = value
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to read system properties: $e")
            } finally {
                try {
                    bufferedReader?.close()
                } catch (_: IOException) {
                    // Do nothing
                }
                process?.destroy()
            }
        }.toMap()
    }

    fun <T : String> getProp(key: String, default: String = ""): String {
        return props[key] ?: default
    }

    fun <T : Boolean?> getProp(key: String): Boolean? {
        val value = getProp<String>(key)

        return if (listOf(
                "1",
                "y",
                "yes",
                "true",
                "on",
            ).contains(value)
        ) {
            true
        } else if (listOf(
                "0",
                "n",
                "no",
                "false",
                "off",
            ).contains(value)
        ) {
            false
        } else {
            null
        }
    }

    fun <T : Boolean> getProp(key: String, default: Boolean = false): Boolean {
        return getProp<Boolean?>(key) ?: default
    }

    fun <T : Int> getProp(key: String, default: Int = 0): Int {
        val value = getProp<String>(key)

        if (value.isEmpty()) {
            return default
        }

        return try {
            value.toInt()
        } catch (_: Exception) {
            default
        }
    }
}
