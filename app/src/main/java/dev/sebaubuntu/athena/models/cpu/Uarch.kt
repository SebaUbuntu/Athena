/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

/**
 * `enum cpuinfo_uarch`
 *
 * Processor microarchitecture
 *
 * Processors with different microarchitectures often have different instruction
 * performance characteristics, and may have dramatically different pipeline
 * organization.
 */
enum class Uarch(
    val value: UInt,
) {
    /**
     * Microarchitecture is unknown, or the library failed to get
     * information about the microarchitecture from OS
     */
    UNKNOWN(0U),

    /**
     * Pentium and Pentium MMX microarchitecture.
     */
    P5(0x00100100U),

    /**
     * Intel Quark microarchitecture.
     */
    QUARK(0x00100101U),

    /**
     * Pentium Pro, Pentium II, and Pentium III.
     */
    P6(0x00100200U),

    /**
     * Pentium M.
     */
    DOTHAN(0x00100201U),

    /**
     * Intel Core microarchitecture.
     */
    YONAH(0x00100202U),

    /**
     * Intel Core 2 microarchitecture on 65 nm process.
     */
    CONROE(0x00100203U),

    /**
     * Intel Core 2 microarchitecture on 45 nm process.
     */
    PENRYN(0x00100204U),

    /**
     * Intel Nehalem and Westmere microarchitectures (Core i3/i5/i7 1st
     * gen).
     */
    NEHALEM(0x00100205U),

    /**
     * Intel Sandy Bridge microarchitecture (Core i3/i5/i7 2nd gen).
     */
    SANDY_BRIDGE(0x00100206U),

    /**
     * Intel Ivy Bridge microarchitecture (Core i3/i5/i7 3rd gen).
     */
    IVY_BRIDGE(0x00100207U),

    /**
     * Intel Haswell microarchitecture (Core i3/i5/i7 4th gen).
     */
    HASWELL(0x00100208U),

    /**
     * Intel Broadwell microarchitecture.
     */
    BROADWELL(0x00100209U),

    /**
     * Intel Sky Lake microarchitecture (14 nm, including
     * Kaby/Coffee/Whiskey/Amber/Comet/Cascade/Cooper Lake).
     */
    SKY_LAKE(0x0010020AU),

    /**
     * DEPRECATED (Intel Kaby Lake microarchitecture).
     */
    //KABY_LAKE(0x0010020AU),

    /**
     * Intel Palm Cove microarchitecture (10 nm, Cannon Lake).
     */
    PALM_COVE(0x0010020BU),

    /**
     * Intel Sunny Cove microarchitecture (10 nm, Ice Lake).
     */
    SUNNY_COVE(0x0010020CU),

    /**
     * Pentium 4 with Willamette, Northwood, or Foster cores.
     */
    WILLAMETTE(0x00100300U),

    /**
     * Pentium 4 with Prescott and later cores.
     */
    PRESCOTT(0x00100301U),

    /**
     * Intel Atom on 45 nm process.
     */
    BONNELL(0x00100400U),

    /**
     * Intel Atom on 32 nm process.
     */
    SALTWELL(0x00100401U),

    /**
     * Intel Silvermont microarchitecture (22 nm out-of-order Atom).
     */
    SILVERMOUNT(0x00100402U),

    /**
     * Intel Airmont microarchitecture (14 nm out-of-order Atom).
     */
    AIRMONT(0x00100403U),

    /**
     * Intel Goldmont microarchitecture (Denverton, Apollo Lake).
     */
    GOLDMONT(0x00100404U),

    /**
     * Intel Goldmont Plus microarchitecture (Gemini Lake).
     */
    GOLDMONT_PLUS(0x00100405U),

    /**
     * Intel Knights Ferry HPC boards.
     */
    KNIGHTS_FERRY(0x00100500U),

    /**
     * Intel Knights Corner HPC boards (aka Xeon Phi).
     */
    KNIGHTS_CORNER(0x00100501U),

    /**
     * Intel Knights Landing microarchitecture (second-gen MIC).
     */
    KNIGHTS_LANDING(0x00100502U),

    /**
     * Intel Knights Hill microarchitecture (third-gen MIC).
     */
    KNIGHTS_HILL(0x00100503U),

    /**
     * Intel Knights Mill Xeon Phi.
     */
    KNIGHTS_MILL(0x00100504U),

    /**
     * Intel/Marvell XScale series.
     */
    XSCALE(0x00100600U),

    /**
     * AMD K5.
     */
    K5(0x00200100U),

    /**
     * AMD K6 and alike.
     */
    K6(0x00200101U),

    /**
     * AMD Athlon and Duron.
     */
    K7(0x00200102U),

    /**
     * AMD Athlon 64, Opteron 64.
     */
    K8(0x00200103U),

    /**
     * AMD Family 10h (Barcelona, Istambul, Magny-Cours).
     */
    K10(0x00200104U),

    /**
     * AMD Bulldozer microarchitecture
     * Zambezi FX-series CPUs, Zurich, Valencia and Interlagos Opteron CPUs.
     */
    BULLDOZER(0x00200105U),

    /**
     * AMD Piledriver microarchitecture
     * Vishera FX-series CPUs, Trinity and Richland APUs, Delhi, Seoul, Abu
     * Dhabi Opteron CPUs.
     */
    PILEDRIVER(0x00200106U),

    /**
     * AMD Steamroller microarchitecture (Kaveri APUs).
     */
    STEAMROLLER(0x00200107U),

    /**
     * AMD Excavator microarchitecture (Carizzo APUs).
     */
    EXCAVATOR(0x00200108U),

    /**
     * AMD Zen microarchitecture (12/14 nm Ryzen and EPYC CPUs).
     */
    ZEN(0x00200109U),

    /**
     * AMD Zen 2 microarchitecture (7 nm Ryzen and EPYC CPUs).
     */
    ZEN2(0x0020010AU),

    /**
     * AMD Zen 3 microarchitecture.
     */
    ZEN3(0x0020010BU),

    /**
     * AMD Zen 4 microarchitecture.
     */
    ZEN4(0x0020010CU),

    /**
     * NSC Geode and AMD Geode GX and LX.
     */
    GEODE(0x00200200U),

    /**
     * AMD Bobcat mobile microarchitecture.
     */
    BOBCAT(0x00200201U),

    /**
     * AMD Jaguar mobile microarchitecture.
     */
    JAGUAR(0x00200202U),

    /**
     * AMD Puma mobile microarchitecture.
     */
    PUMA(0x00200203U),

    /**
     * ARM7 series.
     */
    ARM7(0x00300100U),

    /**
     * ARM9 series.
     */
    ARM9(0x00300101U),

    /**
     * ARM 1136, ARM 1156, ARM 1176, or ARM 11MPCore.
     */
    ARM11(0x00300102U),

    /**
     * ARM Cortex-A5.
     */
    CORTEX_A5(0x00300205U),

    /**
     * ARM Cortex-A7.
     */
    CORTEX_A7(0x00300207U),

    /**
     * ARM Cortex-A8.
     */
    CORTEX_A8(0x00300208U),

    /**
     * ARM Cortex-A9.
     */
    CORTEX_A9(0x00300209U),

    /**
     * ARM Cortex-A12.
     */
    CORTEX_A12(0x00300212U),

    /**
     * ARM Cortex-A15.
     */
    CORTEX_A15(0x00300215U),

    /**
     * ARM Cortex-A17.
     */
    CORTEX_A17(0x00300217U),

    /**
     * ARM Cortex-A32.
     */
    CORTEX_A32(0x00300332U),

    /**
     * ARM Cortex-A35.
     */
    CORTEX_A35(0x00300335U),

    /**
     * ARM Cortex-A53.
     */
    CORTEX_A53(0x00300353U),

    /**
     * ARM Cortex-A55 revision 0 (restricted dual-issue capabilities
     * compared to revision 1+).
     */
    CORTEX_A55R0(0x00300354U),

    /**
     * ARM Cortex-A55.
     */
    CORTEX_A55(0x00300355U),

    /**
     * ARM Cortex-A57.
     */
    CORTEX_A57(0x00300357U),

    /**
     * ARM Cortex-A65.
     */
    CORTEX_A65(0x00300365U),

    /**
     * ARM Cortex-A72.
     */
    CORTEX_A72(0x00300372U),

    /**
     * ARM Cortex-A73.
     */
    CORTEX_A73(0x00300373U),

    /**
     * ARM Cortex-A75.
     */
    CORTEX_A75(0x00300375U),

    /**
     * ARM Cortex-A76.
     */
    CORTEX_A76(0x00300376U),

    /**
     * ARM Cortex-A77.
     */
    CORTEX_A77(0x00300377U),

    /**
     * ARM Cortex-A78.
     */
    CORTEX_A78(0x00300378U),

    /**
     * ARM Neoverse N1.
     */
    NEOVERSE_N1(0x00300400U),

    /**
     * ARM Neoverse E1.
     */
    NEOVERSE_E1(0x00300401U),

    /**
     * ARM Neoverse V1.
     */
    NEOVERSE_V1(0x00300402U),

    /**
     * ARM Neoverse N2.
     */
    NEOVERSE_N2(0x00300403U),

    /**
     * ARM Neoverse V2.
     */
    NEOVERSE_V2(0x00300404U),

    /**
     * ARM Cortex-X1.
     */
    CORTEX_X1(0x00300501U),

    /**
     * ARM Cortex-X2.
     */
    CORTEX_X2(0x00300502U),

    /**
     * ARM Cortex-X3.
     */
    CORTEX_X3(0x00300503U),

    /**
     * ARM Cortex-X4.
     */
    CORTEX_X4(0x00300504U),

    /**
     * ARM Cortex-A510.
     */
    CORTEX_A510(0x00300551U),

    /**
     * ARM Cortex-A520.
     */
    CORTEX_A520(0x00300552U),

    /**
     * ARM Cortex-A710.
     */
    CORTEX_A710(0x00300571U),

    /**
     * ARM Cortex-A715.
     */
    CORTEX_A715(0x00300572U),

    /**
     * ARM Cortex-A720.
     */
    CORTEX_A720(0x00300573U),

    /**
     * Qualcomm Scorpion.
     */
    SCORPION(0x00400100U),

    /**
     * Qualcomm Krait.
     */
    KRAIT(0x00400101U),

    /**
     * Qualcomm Kryo.
     */
    KRYO(0x00400102U),

    /**
     * Qualcomm Falkor.
     */
    FALKOR(0x00400103U),

    /**
     * Qualcomm Saphira.
     */
    SAPHIRA(0x00400104U),

    /**
     * Nvidia Denver.
     */
    DENVER(0x00500100U),

    /**
     * Nvidia Denver 2.
     */
    DENVER2(0x00500101U),

    /**
     * Nvidia Carmel.
     */
    CARMEL(0x00500102U),

    /**
     * Samsung Exynos M1 (Exynos 8890 big cores).
     */
    EXYNOS_M1(0x00600100U),

    /**
     * Samsung Exynos M2 (Exynos 8895 big cores).
     */
    EXYNOS_M2(0x00600101U),

    /**
     * Samsung Exynos M3 (Exynos 9810 big cores).
     */
    EXYNOS_M3(0x00600102U),

    /**
     * Samsung Exynos M4 (Exynos 9820 big cores).
     */
    EXYNOS_M4(0x00600103U),

    /**
     * Samsung Exynos M5 (Exynos 9830 big cores).
     */
    EXYNOS_M5(0x00600104U),

    /**
     * Deprecated synonym for Cortex-A76
     */
    //CORTEX_A76AE(0x00300376U),

    /**
     * Deprecated names for Exynos.
     */
    //MONGOOSE_M1(0x00600100U),
    //MONGOOSE_M2(0x00600101U),
    //MEERKAT_M3(0x00600102U),
    //MEERKAT_M4(0x00600103U),

    /**
     * Apple A6 and A6X processors.
     */
    SWIFT(0x00700100U),

    /**
     * Apple A7 processor.
     */
    CYCLONE(0x00700101U),

    /**
     * Apple A8 and A8X processor.
     */
    TYPHOON(0x00700102U),

    /**
     * Apple A9 and A9X processor.
     */
    TWISTER(0x00700103U),

    /**
     * Apple A10 and A10X processor.
     */
    HURRICANE(0x00700104U),

    /**
     * Apple A11 processor (big cores).
     */
    MONSOON(0x00700105U),

    /**
     * Apple A11 processor (little cores).
     */
    MISTRAL(0x00700106U),

    /**
     * Apple A12 processor (big cores).
     */
    VORTEX(0x00700107U),

    /**
     * Apple A12 processor (little cores).
     */
    TEMPEST(0x00700108U),

    /**
     * Apple A13 processor (big cores).
     */
    LIGHTNING(0x00700109U),

    /**
     * Apple A13 processor (little cores).
     */
    THUNDER(0x0070010AU),

    /**
     * Apple A14 / M1 processor (big cores).
     */
    FIRESTORM(0x0070010BU),

    /**
     * Apple A14 / M1 processor (little cores).
     */
    ICESTORM(0x0070010CU),

    /**
     * Apple A15 / M2 processor (big cores).
     */
    AVALANCHE(0x0070010DU),

    /**
     * Apple A15 / M2 processor (little cores).
     */
    BLIZZARD(0x0070010EU),

    /**
     * Cavium ThunderX.
     */
    THUNDERX(0x00800100U),

    /**
     * Cavium ThunderX2 (originally Broadcom Vulkan).
     */
    THUNDERX2(0x00800200U),

    /**
     * Marvell PJ4.
     */
    PJ4(0x00900100U),

    /**
     * Broadcom Brahma B15.
     */
    BRAHMA_B15(0x00A00100U),

    /**
     * Broadcom Brahma B53.
     */
    BRAHMA_B53(0x00A00101U),

    /**
     * Applied Micro X-Gene.
     */
    XGENE(0x00B00100U),

    /**
     * Hygon Dhyana (a modification of AMD Zen for Chinese market).
     */
    DHYANA(0x01000100U),

    /**
     * HiSilicon TaiShan v110 (Huawei Kunpeng 920 series processors).
     */
    TAISHAN_V110(0x00C00100U);

    companion object {
        @JvmStatic
        fun fromCpuInfo(value: Int) = value.toUInt().let {
            entries.firstOrNull { uarch -> uarch.value == it } ?: UNKNOWN
        }
    }
}
