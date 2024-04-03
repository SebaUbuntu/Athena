/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.cpu.Cpus
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow

object CpuSection : Section() {
    override val title = R.string.section_cpu_name
    override val description = R.string.section_cpu_description
    override val icon = R.drawable.ic_developer_board

    override fun dataFlow(context: Context) = channelFlow {
        while(true) {
            val cpus = Cpus.get()

            trySend(
                listOf(
                    Subsection(
                        "abi",
                        listOf(
                            Information(
                                "supported_abis",
                                InformationValue.StringArrayValue(Build.SUPPORTED_ABIS),
                                R.string.cpu_supported_abis,
                            ),
                            Information(
                                "supported_64_bit_abis",
                                InformationValue.StringArrayValue(Build.SUPPORTED_64_BIT_ABIS),
                                R.string.cpu_supported_64_bit_abis,
                            ),
                            Information(
                                "supported_32_bit_abis",
                                InformationValue.StringArrayValue(Build.SUPPORTED_32_BIT_ABIS),
                                R.string.cpu_supported_32_bit_abis,
                            ),
                        ),
                        R.string.cpu_abi,
                    ),
                    Subsection(
                        "general",
                        listOfNotNull(
                            cpus.physicalPackages?.let {
                                Information(
                                    "physical_packages",
                                    InformationValue.IntValue(it.size),
                                    R.string.cpu_physical_packages,
                                )
                            },
                            cpus.clusters?.let {
                                Information(
                                    "clusters",
                                    InformationValue.IntValue(it.size),
                                    R.string.cpu_clusters,
                                )
                            },
                            cpus.dies?.let {
                                Information(
                                    "dies",
                                    InformationValue.IntValue(it.size),
                                    R.string.cpu_dies,
                                )
                            },
                            Information(
                                "cores",
                                InformationValue.IntValue(cpus.cpus.size),
                                R.string.cpu_cores,
                            ),
                        ),
                        R.string.cpu_general,
                    ),
                    *cpus.cpus.sortedBy { it.id }.map { cpu ->
                        Subsection(
                            "cpu_${cpu.id}",
                            listOfNotNull(
                                cpu.physicalPackageId?.let { physicalPackageId ->
                                    Information(
                                        "physical_package_id",
                                        InformationValue.IntValue(physicalPackageId),
                                        R.string.cpu_physical_package_id,
                                    )
                                },
                                cpu.clusterId?.let { clusterId ->
                                    Information(
                                        "cluster_id",
                                        InformationValue.IntValue(clusterId),
                                        R.string.cpu_cluster_id,
                                    )
                                },
                                cpu.dieId?.let { dieId ->
                                    Information(
                                        "die_id",
                                        InformationValue.IntValue(dieId),
                                        R.string.cpu_die_id,
                                    )
                                },
                                Information(
                                    "core_id",
                                    InformationValue.IntValue(cpu.coreId),
                                    R.string.cpu_core_id,
                                ),
                                cpu.isOnline?.let {
                                    Information(
                                        "is_online",
                                        InformationValue.BooleanValue(it),
                                        R.string.cpu_is_online
                                    )
                                },
                                cpu.currentFrequencyHz?.let { currentFrequencyHz ->
                                    Information(
                                        "current_frequency_hz",
                                        InformationValue.StringResValue(
                                            R.string.cpu_frequency_mhz,
                                            arrayOf(currentFrequencyHz / 1000),
                                            currentFrequencyHz.toString()
                                        ),
                                        R.string.cpu_current_frequency,
                                    )
                                },
                                cpu.minimumFrequencyHz?.let { minimumFrequencyHz ->
                                    Information(
                                        "minimum_frequency_hz",
                                        InformationValue.StringResValue(
                                            R.string.cpu_frequency_mhz,
                                            arrayOf(minimumFrequencyHz / 1000),
                                            minimumFrequencyHz.toString()
                                        ),
                                        R.string.cpu_minimum_frequency,
                                    )
                                },
                                cpu.maximumFrequencyHz?.let { maximumFrequencyHz ->
                                    Information(
                                        "maximum_frequency_hz",
                                        InformationValue.StringResValue(
                                            R.string.cpu_frequency_mhz,
                                            arrayOf(maximumFrequencyHz / 1000),
                                            maximumFrequencyHz.toString()
                                        ),
                                        R.string.cpu_maximum_frequency,
                                    )
                                },
                                cpu.scalingCurrentFrequencyHz?.let { scalingCurrentFrequencyHz ->
                                    Information(
                                        "scaling_current_frequency_hz",
                                        InformationValue.StringResValue(
                                            R.string.cpu_frequency_mhz,
                                            arrayOf(scalingCurrentFrequencyHz / 1000),
                                            scalingCurrentFrequencyHz.toString()
                                        ),
                                        R.string.cpu_scaling_current_frequency,
                                    )
                                },
                                cpu.scalingMinimumFrequencyHz?.let { scalingMinimumFrequencyHz ->
                                    Information(
                                        "scaling_minimum_frequency_hz",
                                        InformationValue.StringResValue(
                                            R.string.cpu_frequency_mhz,
                                            arrayOf(scalingMinimumFrequencyHz / 1000),
                                            scalingMinimumFrequencyHz.toString()
                                        ),
                                        R.string.cpu_scaling_minimum_frequency,
                                    )
                                },
                                cpu.scalingMaximumFrequencyHz?.let { scalingMaximumFrequencyHz ->
                                    Information(
                                        "scaling_maximum_frequency_hz",
                                        InformationValue.StringResValue(
                                            R.string.cpu_frequency_mhz,
                                            arrayOf(scalingMaximumFrequencyHz / 1000),
                                            scalingMaximumFrequencyHz.toString()
                                        ),
                                        R.string.cpu_scaling_maximum_frequency,
                                    )
                                },
                            ),
                            R.string.cpu_title,
                            arrayOf(cpu.id),
                        )
                    }.toTypedArray()
                )
            )
            delay(1000)
        }
    }
}
