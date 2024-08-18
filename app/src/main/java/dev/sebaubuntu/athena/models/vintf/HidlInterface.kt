/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.vintf

import dev.sebaubuntu.athena.R

data class HidlInterface(
    override val name: String,
    val transport: HidlTransportType,
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
) : TrebleInterface {
    override val interfaceTypeStringResId = R.string.treble_interface_type_hidl
}
