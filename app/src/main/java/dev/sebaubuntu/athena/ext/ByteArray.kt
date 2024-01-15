/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

@OptIn(ExperimentalUnsignedTypes::class)
fun ByteArray.toHexString() = asUByteArray().toHexString()
