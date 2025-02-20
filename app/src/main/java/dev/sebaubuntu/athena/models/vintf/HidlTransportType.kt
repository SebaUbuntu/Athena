/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.vintf

enum class HidlTransportType(private val lshalValue: String) {
    PASSTHROUGH("passthrough"),
    HWBINDER("hwbinder");

    companion object {
        fun fromLshalValue(value: String) = entries.firstOrNull {
            it.lshalValue == value
        }
    }
}
