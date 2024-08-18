/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.vintf

import dev.sebaubuntu.athena.R

data class AidlInterface(
    override val name: String,
) : TrebleInterface {
    override val interfaceTypeStringResId = R.string.treble_interface_type_aidl
}
