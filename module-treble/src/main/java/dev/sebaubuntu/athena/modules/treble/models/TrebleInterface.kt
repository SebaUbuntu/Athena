/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.treble.models

sealed interface TrebleInterface {
    data class Aidl(
        override val name: String,
    ) : TrebleInterface {
        override val type = Type.AIDL
    }

    data class Hidl(
        override val name: String,
        val transport: TransportType,
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
        enum class TransportType {
            PASSTHROUGH,
            HWBINDER,
        }

        override val type = Type.HIDL
    }

    enum class Type {
        AIDL,
        HIDL,
    }

    val name: String

    val type: Type
}
