/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.cpu.Cpus

object CpuSection : Section() {
    override val name = R.string.section_cpu_name
    override val description = R.string.section_cpu_description
    override val icon = R.drawable.ic_cpu
    override val requiredPermissions = arrayOf<String>()

    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
        val cpus = Cpus.get()

        this["ABI"] = mapOf(
            "Supported ABIs" to Build.SUPPORTED_ABIS.joinToString(),
            "Supported 64bit ABIs" to Build.SUPPORTED_64_BIT_ABIS.joinToString(),
            "Supported 32bit ABIs" to Build.SUPPORTED_32_BIT_ABIS.joinToString(),
        )

        this["General"] = mutableMapOf<String, String>().apply {
            cpus.physicalPackages?.let {
                this["Number of physical packages"] = it.size.toString()
            }

            cpus.clusters?.let {
                this["Number of clusters"] = it.size.toString()
            }

            cpus.dies?.let {
                this["Number of dies"] = it.size.toString()
            }

            this["Number of cores"] = cpus.cpus.size.toString()
        }.toMap()

        for (cpu in cpus.cpus.sortedBy { it.id }) {
            this["CPU ${cpu.id}"] = mutableMapOf<String, String>().apply {
                this["Frequency range"] = "${
                    cpu.minimumFrequencyHz?.div(1000)
                }MHz - ${
                    cpu.maximumFrequencyHz?.div(1000)
                }MHz"

                cpu.physicalPackageId?.let {
                    this["Physical package ID"] = "$it"
                }

                cpu.clusterId?.let {
                    this["Cluster ID"] = "$it"
                }

                cpu.dieId?.let {
                    this["Die ID"] = "$it"
                }

                this["Core ID"] = "${cpu.coreId}"
            }.toMap()
        }
    }.toMap()
}
