/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.gpu.models

enum class VkPhysicalDeviceType(val value: ULong) {
    OTHER(0u),
    INTEGRATED_GPU(1u),
    DISCRETE_GPU(2u),
    VIRTUAL_GPU(3u),
    CPU(4u),
}
