/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object VintfUtils {
    private val LOG_TAG = this::class.simpleName!!

    private const val LSHAL_EXECUTABLE_PATH = "/system/bin/lshal"
    private val LSHAL_CMD = listOf("--neat", "-it", "--types=v")

    class Hal(val name: String, val type: HalType) {
        enum class HalType {
            HIDL_PASSTHROUGH,
            HIDL_HWBINDER,
            AIDL,
        }
    }

    val halList by lazy {
        val hals = mutableListOf<Hal>()

        var process: Process? = null
        var bufferedReader: BufferedReader? = null

        try {
            process = ProcessBuilder()
                .command(listOf(LSHAL_EXECUTABLE_PATH) + LSHAL_CMD)
                .redirectErrorStream(false)
                .start()

            process?.let { p ->
                bufferedReader = BufferedReader(InputStreamReader(p.inputStream))
                bufferedReader?.forEachLine { line ->
                    val (name, transport) = line.split(" ", limit = 2)

                    hals.add(
                        Hal(
                            name.trim(), when (transport.trim()) {
                                "passthrough" -> Hal.HalType.HIDL_PASSTHROUGH
                                "hwbinder" -> Hal.HalType.HIDL_HWBINDER
                                else -> throw Exception("Unknown HAL transport $transport")
                            }
                        )
                    )
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

        hals
    }
}
