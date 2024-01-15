/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.toHexString() = joinToString("") {
    it.toString(radix = 16).padStart(2, '0')
}
