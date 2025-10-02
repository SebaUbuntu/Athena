/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.gpu.models

class GlInformation(
    val glVendor: String?,
    val glRenderer: String?,
    val glVersion: String?,
    val glExtensions: Array<String>?,
)
