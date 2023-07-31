/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.vintf

data class HIDLInterface(
    val name: String,
    val transport: HIDLTransportType,
    val serverProcessId: Int?,
    val address: String?,
    val arch: String?,
    val currentThreads: Int?,
    val maxThreads: Int?,
    val released: Boolean?,
    val inDeviceManifest: Boolean,
    val inDeviceCompatibilityMatrix: Boolean,
    val inFrameworkManifest: Boolean,
    val inFrameworkCompatibilityMatrix: Boolean,
    val clientsProcessIds: List<Int>?,
)
