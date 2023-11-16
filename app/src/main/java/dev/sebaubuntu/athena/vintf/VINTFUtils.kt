/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.vintf

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object VINTFUtils {
    private val LOG_TAG = this::class.simpleName!!

    private const val LSHAL_EXECUTABLE_PATH = "/system/bin/lshal"
    private val LSHAL_CMD = listOf(LSHAL_EXECUTABLE_PATH, "--neat", "-itparelVc", "--types=a")

    private val LSHAL_COLUMN_SEPARATOR = "\\s+".toRegex()

    private const val LSHAL_NULL = "N/A"
    private const val LSHAL_SEPARATOR = ";"

    val halInterfaces: Set<HIDLInterface>
        get() {
            val interfaces = mutableSetOf<HIDLInterface>()

            var process: Process? = null
            var bufferedReader: BufferedReader? = null

            try {
                process = ProcessBuilder()
                    .command(LSHAL_CMD)
                    .redirectErrorStream(false)
                    .start()

                process?.let { p ->
                    bufferedReader = BufferedReader(InputStreamReader(p.inputStream))
                    bufferedReader?.forEachLine { line ->
                        try {
                            val values = line.split(LSHAL_COLUMN_SEPARATOR).map {
                                it.trim()
                            }

                            var i = 0
                            var name = values[i++]
                            if (values[i].startsWith("(")) {
                                // e.g. "(/vendor/lib/hw/)"
                                name = "$name ${values[i++]}"
                            }
                            if (values[i].startsWith("(-")) {
                                // e.g. "(-google)"
                                name = "$name ${values[i++]}"
                            }
                            val transport = values[i++]
                            val serverProcessId = values[i++]
                            val address = values[i++]
                            val arch = values[i++]
                            val threads = values[i++]
                            val released = values[i++]
                            val vintf = values[i++]
                            val clientsProcessIds = values[i]

                            val (currentThreads, maxThreads) = threads.takeUnless {
                                it == LSHAL_NULL
                            }?.split("/")?.map {
                                runCatching { it.toInt() }.getOrNull()
                            } ?: listOf(null, null)
                            val vintfInfo = vintf.takeUnless {
                                it == LSHAL_NULL
                            }?.split(";") ?: listOf()

                            interfaces.add(
                                HIDLInterface(
                                    name,
                                    HIDLTransportType.fromLshalValue(transport)!!,
                                    serverProcessId.takeUnless { it == LSHAL_NULL }?.toInt(),
                                    address.takeUnless { it == LSHAL_NULL },
                                    arch.takeUnless { it == LSHAL_NULL },
                                    currentThreads,
                                    maxThreads,
                                    when (released) {
                                        "Y" -> true
                                        "N" -> false
                                        "?" -> null
                                        else -> throw Exception("Unknown released value: $released")
                                    },
                                    vintfInfo.contains("DM"),
                                    vintfInfo.contains("DC"),
                                    vintfInfo.contains("FM"),
                                    vintfInfo.contains("FC"),
                                    clientsProcessIds.takeUnless {
                                        it.isEmpty()
                                    }?.split(LSHAL_SEPARATOR)?.map {
                                        it.toInt()
                                    }
                                )
                            )
                        } catch (e: Exception) {
                            Log.e(LOG_TAG, "Failed to parse line: $line")
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to read system properties: $e")
                e.printStackTrace()
            } finally {
                try {
                    bufferedReader?.close()
                } catch (_: IOException) {
                    // Do nothing
                }
                process?.destroy()
            }

            return interfaces.toSet()
        }
}
