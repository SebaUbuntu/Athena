/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.ext

fun <T> T.hashCodeOf(
    vararg selectors: T.() -> Any?,
) = selectors.fold(0) { hash, property -> 31 * hash + property().hashCode() }
