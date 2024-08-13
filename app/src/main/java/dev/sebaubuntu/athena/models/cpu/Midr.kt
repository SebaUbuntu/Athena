/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

/**
 * ARM and ARM64 only
 *
 * B4.1.105 MIDR, Main ID Register, VMSA
 */
data class Midr(
    val implementer: Implementer,
    val variant: UByte,
    val architecture: Architecture,
    val primaryPartNumber: UInt,
    val revision: UByte,
) {
    enum class Implementer(val value: UByte) {
        UNKNOWN(0x00U),

        // From ARM spec
        ARM(0x41U),
        DEC(0x44U),
        MOTOROLA(0x4DU),
        QUALCOMM(0x51U),
        MARVELL(0x56U),
        INTEL(0x69U),

        // Implementers not declared in the spec, taken from cpuinfo's arm/uarch.c
        BROADCOM(0x42U),
        CAVIUM(0x43U),
        HUAWEI(0x48U),
        NVIDIA(0x4EU),
        APM(0x50U),
        SAMSUNG(0x53U);

        companion object {
            fun fromValue(value: UByte) = values().firstOrNull { it.value == value } ?: UNKNOWN
        }
    }

    enum class Architecture(val value: UByte) {
        ARMV4(0x1U),
        ARMV4T(0x2U),
        ARMV5(0x3U),
        ARMV5T(0x4U),
        ARMV5TE(0x5U),
        ARMV5TEJ(0x6U),
        ARMV6(0x7U),
        DEFINED_BY_CPUID(0xFU);

        companion object {
            fun fromValue(value: UByte) = values().first { it.value == value }
        }
    }

    companion object {
        fun fromCpuInfo(value: Int) = Midr(
            Implementer.fromValue(value.shr(24).and(0xFF).toUByte()),
            value.shr(20).and(0xF).toUByte(),
            Architecture.fromValue(value.shr(16).and(0xF).toUByte()),
            value.shr(4).and(0xFFF).toUInt(),
            value.and(0xF).toUByte(),
        )
    }
}
