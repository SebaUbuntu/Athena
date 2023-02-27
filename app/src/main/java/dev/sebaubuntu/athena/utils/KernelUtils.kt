/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

object KernelUtils {
    val formattedKernelVersion = System.getProperty("os.version") ?: "Unknown"
}
