/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.util.Log
import dev.sebaubuntu.athena.models.vintf.AidlInterface
import dev.sebaubuntu.athena.models.vintf.HidlInterface
import dev.sebaubuntu.athena.models.vintf.HidlTransportType
import dev.sebaubuntu.athena.models.vintf.TrebleInterface
import java.io.BufferedReader
import java.io.InputStreamReader

object VintfUtils {
    private val LOG_TAG = this::class.simpleName!!

    private val DUMPSYS_CMD = listOf("/system/bin/dumpsys", "-l")

    private val LSHAL_CMD = listOf("/system/bin/lshal", "--neat", "-itparelVc", "--types=a")

    private val LSHAL_COLUMN_SEPARATOR = "\\s+".toRegex()

    private const val LSHAL_NULL = "N/A"
    private const val LSHAL_SEPARATOR = ";"

    private fun getAidlInterfaces() = mutableSetOf<AidlInterface>().apply {
        var process: Process? = null

        try {
            process = ProcessBuilder()
                .command(DUMPSYS_CMD)
                .redirectErrorStream(false)
                .start()

            process?.let { p ->
                BufferedReader(InputStreamReader(p.inputStream)).forEachLine { line ->
                    try {
                        val name = line.trim()

                        val aidlInterface = AidlInterface(
                            name,
                        )

                        // Skip non-stable AIDLs
                        if (!aidlInterface.name.contains("/")) {
                            return@forEachLine
                        }

                        // Skip framework AIDL interfaces
                        if (aidlInterface.name.startsWith("android.")
                            && !aidlInterface.name.startsWith("android.hardware.")
                        ) {
                            return@forEachLine
                        }

                        add(aidlInterface)
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "Failed to parse line: $line", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to parse AIDL interfaces", e)
        } finally {
            process?.destroy()
        }
    }.toSet()

    private fun getHidlInterfaces() = mutableSetOf<HidlInterface>().apply {
        var process: Process? = null

        try {
            process = ProcessBuilder()
                .command(LSHAL_CMD)
                .redirectErrorStream(false)
                .start()

            process?.let { p ->
                BufferedReader(InputStreamReader(p.inputStream)).forEachLine { line ->
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

                        val hidlInterface = HidlInterface(
                            name,
                            HidlTransportType.fromLshalValue(transport)!!,
                            serverProcessId.takeUnless { it == LSHAL_NULL }?.toInt(),
                            address.takeUnless { it == LSHAL_NULL },
                            arch.takeUnless { it == LSHAL_NULL },
                            currentThreads,
                            maxThreads,
                            when (released) {
                                "Y" -> true
                                "N" -> false
                                "?" -> null
                                else -> null.also {
                                    Log.i(LOG_TAG, "Unknown released value: $released")
                                }
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

                        // Skip framework HIDL interfaces
                        if (hidlInterface.name.startsWith("android.")
                            && !hidlInterface.name.startsWith("android.hardware.")
                        ) {
                            return@forEachLine
                        }

                        add(hidlInterface)
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "Failed to parse line: $line", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to parse HIDL interfaces", e)
        } finally {
            process?.destroy()
        }
    }.toSet()

    fun getTrebleInterfaces() = mutableSetOf<TrebleInterface>()
        .plus(getAidlInterfaces())
        .plus(getHidlInterfaces())
        .toSet()
}
