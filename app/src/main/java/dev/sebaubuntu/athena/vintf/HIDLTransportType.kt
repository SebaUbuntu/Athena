/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.vintf

enum class HIDLTransportType(private val lshalValue: String) {
    PASSTHROUGH("passthrough"),
    HWBINDER("hwbinder");

    companion object {
        fun fromLshalValue(value: String): HIDLTransportType? {
            return values().firstOrNull {
                it.lshalValue == value
            }
        }
    }
}
