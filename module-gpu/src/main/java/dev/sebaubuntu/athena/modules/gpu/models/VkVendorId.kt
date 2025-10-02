/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.gpu.models

enum class VkVendorId(
    val value: ULong,
) {
    KHRONOS(0x10000U),
    VIV(0x10001U),
    VSI(0x10002U),
    KAZAN(0x10003U),
    CODEPLAY(0x10004U),
    MESA(0x10005U),
    POCL(0x10006U),
    MOBILEYE(0x10007U);

    companion object {
        fun fromValue(value: ULong) = entries.firstOrNull {
            it.value == value
        }
    }
}
