/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.categories

import android.content.Context
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.Category
import dev.sebaubuntu.athena.utils.CpuUtils

object CpuCategory : Category() {
    override val name = R.string.section_cpu_name
    override val description = R.string.section_cpu_description
    override val icon = R.drawable.ic_cpu
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
        this["General"] = mapOf(
            "Supported ABIs" to Build.SUPPORTED_ABIS.joinToString(),
            "Supported 64bit ABIs" to Build.SUPPORTED_64_BIT_ABIS.joinToString(),
            "Supported 32bit ABIs" to Build.SUPPORTED_32_BIT_ABIS.joinToString(),
            "Number of cores" to CpuUtils.cpus.size.toString(),
        )

        for (cpu in CpuUtils.cpus) {
            this["CPU ${cpu.id}"] = mapOf(
                "Frequency range" to "${cpu.minFrequency}MHz - ${cpu.maxFrequency}MHz",
                "Core siblings" to cpu.coreSiblings.joinToString(),
            )
        }
    }.toMap()
}
