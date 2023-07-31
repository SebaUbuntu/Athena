/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.cpu.CPU

object CpuSection : Section() {
    override val name = R.string.section_cpu_name
    override val description = R.string.section_cpu_description
    override val icon = R.drawable.ic_cpu
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
        val cpus = CPU.getCPUs()

        this["General"] = mapOf(
            "Supported ABIs" to Build.SUPPORTED_ABIS.joinToString(),
            "Supported 64bit ABIs" to Build.SUPPORTED_64_BIT_ABIS.joinToString(),
            "Supported 32bit ABIs" to Build.SUPPORTED_32_BIT_ABIS.joinToString(),
            "Number of cores" to cpus.size.toString(),
        )

        for (cpu in cpus) {
            this["CPU ${cpu.id}"] = mapOf(
                "Frequency range" to
                        "${cpu.minimumFrequency / 1000}MHz - ${cpu.maximumFrequency / 1000}MHz",
                "Core siblings" to cpu.coreSiblings.joinToString(),
            )
        }
    }.toMap()
}
