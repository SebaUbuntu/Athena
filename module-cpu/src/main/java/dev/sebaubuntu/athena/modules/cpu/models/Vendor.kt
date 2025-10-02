/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.cpu.models

/**
 * `enum cpuinfo_vendor`
 *
 * Vendor of processor core design
 */
enum class Vendor(
    val value: UInt,
) {
    /**
     * Processor vendor is not known to the library, or the library failed
     * to get vendor information from the OS.
     */
    UNKNOWN(0U),

    /**
     * Intel Corporation. Vendor of x86, x86-64, IA64, and ARM processor
     * microarchitectures.
     *
     * Sold its ARM design subsidiary in 2006. The last ARM processor design
     * was released in 2004.
     */
    INTEL(1U),

    /**
     * Advanced Micro Devices, Inc. Vendor of x86 and x86-64 processor
     * microarchitectures.
     */
    AMD(2U),

    /**
     * ARM Holdings plc. Vendor of ARM and ARM64 processor
     * microarchitectures.
     */
    ARM(3U),

    /**
     * Qualcomm Incorporated. Vendor of ARM and ARM64 processor
     * microarchitectures.
     */
    QUALCOMM(4U),

    /**
     * Apple Inc. Vendor of ARM and ARM64 processor microarchitectures.
     */
    APPLE(5U),

    /**
     * Samsung Electronics Co., Ltd. Vendir if ARM64 processor
     * microarchitectures.
     */
    SAMSUNG(6U),

    /**
     * Nvidia Corporation. Vendor of ARM64-compatible processor
     * microarchitectures.
     */
    NVIDIA(7U),

    /**
     * MIPS Technologies, Inc. Vendor of MIPS processor microarchitectures.
     */
    MIPS(8U),

    /**
     * International Business Machines Corporation. Vendor of PowerPC
     * processor microarchitectures.
     */
    IBM(9U),

    /**
     * Ingenic Semiconductor. Vendor of MIPS processor microarchitectures.
     */
    INGENIC(10U),

    /**
     * VIA Technologies, Inc. Vendor of x86 and x86-64 processor
     * microarchitectures.
     *
     * Processors are designed by Centaur Technology, a subsidiary of VIA
     * Technologies.
     */
    VIA(11U),

    /**
     * Cavium, Inc. Vendor of ARM64 processor microarchitectures.
     */
    CAVIUM(12U),

    /**
     * Broadcom, Inc. Vendor of ARM processor microarchitectures.
     */
    BROADCOM(13U),

    /**
     * Applied Micro Circuits Corporation (APM). Vendor of ARM64 processor
     * microarchitectures.
     */
    APM(14U),

    /**
     * Huawei Technologies Co., Ltd. Vendor of ARM64 processor
     * microarchitectures.
     *
     * Processors are designed by HiSilicon, a subsidiary of Huawei.
     */
    HUAWEI(15U),

    /**
     * Hygon (Chengdu Haiguang Integrated Circuit Design Co., Ltd), Vendor
     * of x86-64 processor microarchitectures.
     *
     * Processors are variants of AMD cores.
     */
    HYGON(16U),

    /**
     * SiFive, Inc. Vendor of RISC-V processor microarchitectures.
     */
    SIFIVE(17U),

    /**
     * Texas Instruments Inc. Vendor of ARM processor microarchitectures.
     */
    TEXAS_INSTRUMENTS(30U),

    /**
     * Marvell Technology Group Ltd. Vendor of ARM processor
     * microarchitectures.
     */
    MARVELL(31U),

    /**
     * RDC Semiconductor Co., Ltd. Vendor of x86 processor
     * microarchitectures.
     */
    RDC(32U),

    /**
     * DM&P Electronics Inc. Vendor of x86 processor microarchitectures.
     */
    DMP(33U),

    /**
     * Motorola, Inc. Vendor of PowerPC and ARM processor
     * microarchitectures.
     */
    MOTOROLA(34U),

    /**
     * Transmeta Corporation. Vendor of x86 processor microarchitectures.
     *
     * Now defunct. The last processor design was released in 2004.
     * Transmeta processors implemented VLIW ISA and used binary translation
     * to execute x86 code.
     */
    TRANSMETA(50U),

    /**
     * Cyrix Corporation. Vendor of x86 processor microarchitectures.
     *
     * Now defunct. The last processor design was released in 1996.
     */
    CYRIX(51U),

    /**
     * Rise Technology. Vendor of x86 processor microarchitectures.
     *
     * Now defunct. The last processor design was released in 1999.
     */
    RISE(52U),

    /**
     * National Semiconductor. Vendor of x86 processor microarchitectures.
     *
     * Sold its x86 design subsidiary in 1999. The last processor design was
     * released in 1998.
     */
    NSC(53U),

    /**
     * Silicon Integrated Systems. Vendor of x86 processor
     * microarchitectures.
     *
     * Sold its x86 design subsidiary in 2001. The last processor design was
     * released in 2001.
     */
    SIS(54U),

    /**
     * NexGen. Vendor of x86 processor microarchitectures.
     *
     * Now defunct. The last processor design was released in 1994.
     * NexGen designed the first x86 microarchitecture which decomposed x86
     * instructions into simple microoperations.
     */
    NEXGEN(55U),

    /**
     * United Microelectronics Corporation. Vendor of x86 processor
     * microarchitectures.
     *
     * Ceased x86 in the early 1990s. The last processor design was released
     * in 1991. Designed U5C and U5D processors. Both are 486 level.
     */
    UMC(56U),

    /**
     * Digital Equipment Corporation. Vendor of ARM processor
     * microarchitecture.
     *
     * Sold its ARM designs in 1997. The last processor design was released
     * in 1997.
     */
    DEC(57U);

    companion object {
        @JvmStatic
        fun fromCpuInfo(value: Int) = value.toUInt().let {
            entries.firstOrNull { vendor -> vendor.value == it } ?: UNKNOWN
        }
    }
}
