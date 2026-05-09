/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.ext

@Suppress("NOTHING_TO_INLINE")
inline fun Int.hasFlag(bit: Int) = and(bit) == bit
