/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.os.Build
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.cpu.Cache
import dev.sebaubuntu.athena.models.cpu.LinuxCpu
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import dev.sebaubuntu.athena.utils.CpuInfoUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow

object CpuSection : Section(
    "cpu",
    R.string.section_cpu_name,
    R.string.section_cpu_description,
    R.drawable.ic_developer_board,
) {
    override fun dataFlow(context: Context) = channelFlow {
        val processors = CpuInfoUtils.getProcessors()
        val cores = CpuInfoUtils.getCores()
        val clusters = CpuInfoUtils.getClusters()
        val packages = CpuInfoUtils.getPackages()
        val uarchs = CpuInfoUtils.getUarchs()
        val l1iCaches = CpuInfoUtils.getL1iCaches()
        val l1dCaches = CpuInfoUtils.getL1dCaches()
        val l2Caches = CpuInfoUtils.getL2Caches()
        val l3Caches = CpuInfoUtils.getL3Caches()
        val l4Caches = CpuInfoUtils.getL4Caches()

        val getData = {
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
                        uarchs.takeIf { it.isNotEmpty() }?.let {
                            Information(
                                "uarchs",
                                InformationValue.IntValue(it.size),
                                R.string.cpu_uarchs,
                            )
                        },
                        packages.takeIf { it.isNotEmpty() }?.let {
                            Information(
                                "physical_packages",
                                InformationValue.IntValue(it.size),
                                R.string.cpu_packages,
                            )
                        },
                        clusters.takeIf { it.isNotEmpty() }?.let {
                            Information(
                                "clusters",
                                InformationValue.IntValue(it.size),
                                R.string.cpu_clusters,
                            )
                        },
                        cores.takeIf { it.isNotEmpty() }?.let {
                            Information(
                                "cores",
                                InformationValue.IntValue(it.size),
                                R.string.cpu_cores,
                            )
                        },
                        processors.takeIf { it.isNotEmpty() }?.let {
                            Information(
                                "processors",
                                InformationValue.IntValue(it.size),
                                R.string.cpu_processors,
                            )
                        },
                        l1iCaches.takeIf { it.isNotEmpty() }?.let {
                            Information(
                                "l1i_caches",
                                InformationValue.IntValue(it.size),
                                R.string.cpu_l1i_caches,
                            )
                        },
                        l1dCaches.takeIf { it.isNotEmpty() }?.let {
                            Information(
                                "l1d_caches",
                                InformationValue.IntValue(it.size),
                                R.string.cpu_l1d_caches,
                            )
                        },
                        l2Caches.takeIf { it.isNotEmpty() }?.let {
                            Information(
                                "l2_caches",
                                InformationValue.IntValue(it.size),
                                R.string.cpu_l2_caches,
                            )
                        },
                        l3Caches.takeIf { it.isNotEmpty() }?.let {
                            Information(
                                "l3_caches",
                                InformationValue.IntValue(it.size),
                                R.string.cpu_l3_caches,
                            )
                        },
                        l4Caches.takeIf { it.isNotEmpty() }?.let {
                            Information(
                                "l4_caches",
                                InformationValue.IntValue(it.size),
                                R.string.cpu_l4_caches,
                            )
                        },
                    ),
                    R.string.cpu_general,
                ),
                *uarchs.withIndex().map { (i, uarchInfo) ->
                    Subsection(
                        "uarch_$i",
                        listOfNotNull(
                            Information(
                                "uarch",
                                InformationValue.StringValue(uarchInfo.uarch.toString()),
                                R.string.cpu_uarch,
                            ),
                            uarchInfo.cpuid?.let {
                                Information(
                                    "cpuid",
                                    InformationValue.IntValue(it.toInt()),
                                    R.string.cpu_cpuid,
                                )
                            },
                            uarchInfo.midr?.let { midr ->
                                Information(
                                    "midr",
                                    InformationValue.StringValue(midr.toString()),
                                    R.string.cpu_midr,
                                )
                            },
                            Information(
                                "processor_count",
                                InformationValue.IntValue(uarchInfo.processorCount.toInt()),
                                R.string.cpu_processor_count,
                            ),
                            Information(
                                "core_count",
                                InformationValue.IntValue(uarchInfo.coreCount.toInt()),
                                R.string.cpu_core_count,
                            ),
                        ),
                        R.string.cpu_uarch_title,
                        arrayOf(i),
                    )
                }.toTypedArray(),
                *packages.withIndex().map { (i, cpuPackage) ->
                    Subsection(
                        "package_$i",
                        listOfNotNull(
                            Information(
                                "name",
                                InformationValue.StringValue(cpuPackage.name),
                                R.string.cpu_package_name,
                            ),
                            Information(
                                "processor_start",
                                InformationValue.UIntValue(cpuPackage.processorStart),
                                R.string.cpu_processor_start,
                            ),
                            Information(
                                "processor_count",
                                InformationValue.UIntValue(cpuPackage.processorCount),
                                R.string.cpu_processor_count,
                            ),
                            Information(
                                "core_start",
                                InformationValue.UIntValue(cpuPackage.coreStart),
                                R.string.cpu_core_start,
                            ),
                            Information(
                                "core_count",
                                InformationValue.UIntValue(cpuPackage.coreCount),
                                R.string.cpu_core_count,
                            ),
                            Information(
                                "cluster_start",
                                InformationValue.UIntValue(cpuPackage.clusterStart),
                                R.string.cpu_cluster_start,
                            ),
                            Information(
                                "cluster_count",
                                InformationValue.UIntValue(cpuPackage.clusterCount),
                                R.string.cpu_cluster_count,
                            ),
                        ),
                        R.string.cpu_package_title,
                        arrayOf(i),
                    )
                }.toTypedArray(),
                *clusters.sortedBy { it.clusterId }.map { cluster ->
                    Subsection(
                        "cluster_${cluster.clusterId}",
                        listOfNotNull(
                            Information(
                                "processor_start",
                                InformationValue.UIntValue(cluster.processorStart),
                                R.string.cpu_processor_start,
                            ),
                            Information(
                                "processor_count",
                                InformationValue.UIntValue(cluster.processorCount),
                                R.string.cpu_processor_count,
                            ),
                            Information(
                                "core_start",
                                InformationValue.UIntValue(cluster.coreStart),
                                R.string.cpu_core_start,
                            ),
                            Information(
                                "core_count",
                                InformationValue.UIntValue(cluster.coreCount),
                                R.string.cpu_core_count,
                            ),
                            /*
                            Information(
                                "package",
                                InformationValue.StringValue(cluster.cpuPackage.toString()),
                                R.string.cpu_package,
                            ),
                            */
                            Information(
                                "vendor",
                                InformationValue.EnumValue(cluster.vendor),
                                R.string.cpu_vendor,
                            ),
                            Information(
                                "uarch",
                                InformationValue.EnumValue(cluster.uarch),
                                R.string.cpu_uarch,
                            ),
                            cluster.cpuid?.let {
                                Information(
                                    "cpuid",
                                    InformationValue.UIntValue(it),
                                    R.string.cpu_cpuid,
                                )
                            },
                            cluster.midr?.let { midr ->
                                Information(
                                    "midr",
                                    InformationValue.StringValue(midr.toString()),
                                    R.string.cpu_midr,
                                )
                            },
                            Information(
                                "frequency",
                                InformationValue.ULongValue(cluster.frequency),
                                R.string.cpu_frequency,
                            ),
                        ),
                        R.string.cpu_cluster_title,
                        arrayOf(cluster.clusterId),
                    )
                }.toTypedArray(),
                *cores.sortedBy { it.coreId }.map { core ->
                    Subsection(
                        "core_${core.coreId}",
                        listOfNotNull(
                            Information(
                                "processor_start",
                                InformationValue.UIntValue(core.processorStart),
                                R.string.cpu_processor_start,
                            ),
                            Information(
                                "processor_count",
                                InformationValue.UIntValue(core.processorCount),
                                R.string.cpu_processor_count,
                            ),
                            Information(
                                "cluster_id",
                                InformationValue.UIntValue(core.cluster.clusterId),
                                R.string.cpu_cluster_id,
                            ),
                            /*
                            Information(
                                "package",
                                InformationValue.StringValue(core.cpuPackage.toString()),
                                R.string.cpu_package,
                            ),
                            */
                            Information(
                                "vendor",
                                InformationValue.EnumValue(core.vendor),
                                R.string.cpu_vendor,
                            ),
                            Information(
                                "uarch",
                                InformationValue.EnumValue(core.uarch),
                                R.string.cpu_uarch,
                            ),
                            core.cpuid?.let {
                                Information(
                                    "cpuid",
                                    InformationValue.UIntValue(it),
                                    R.string.cpu_cpuid,
                                )
                            },
                            core.midr?.let { midr ->
                                Information(
                                    "midr",
                                    InformationValue.StringValue(midr.toString()),
                                    R.string.cpu_midr,
                                )
                            },
                            Information(
                                "frequency",
                                InformationValue.ULongValue(core.frequency),
                                R.string.cpu_frequency,
                            ),
                        ),
                        R.string.cpu_core_title,
                        arrayOf(core.coreId),
                    )
                }.toTypedArray(),
                *processors.sortedWith(
                    compareBy({ it.core.coreId }, { it.smtId })
                ).map { processor ->
                    val linuxCpu = LinuxCpu.fromProcessor(processor)

                    Subsection(
                        "processor_${processor.core.coreId}-${processor.smtId}",
                        listOfNotNull(
                            Information(
                                "smt_id",
                                InformationValue.UIntValue(processor.smtId),
                                R.string.cpu_processor_smt_id,
                            ),
                            Information(
                                "core_id",
                                InformationValue.UIntValue(processor.core.coreId),
                                R.string.cpu_core_id,
                            ),
                            Information(
                                "cluster_id",
                                InformationValue.UIntValue(processor.cluster.clusterId),
                                R.string.cpu_cluster_id,
                            ),
                            /*
                            Information(
                                "package",
                                InformationValue.StringValue(processor.cpuPackage.toString()),
                                R.string.cpu_package,
                            ),
                            */
                            Information(
                                "linux_id",
                                InformationValue.UIntValue(processor.linuxId),
                                R.string.cpu_processor_linux_id,
                            ),
                            processor.apicId?.let {
                                Information(
                                    "apic_id",
                                    InformationValue.UIntValue(it),
                                    R.string.cpu_processor_apic_id,
                                )
                            },
                            /*
                            Information(
                                "cache",
                                InformationValue.StringValue(processor.cache.toString()),
                                R.string.cpu_processor_cache,
                            ),
                            */
                            linuxCpu.isOnline?.let {
                                Information(
                                    "is_online",
                                    InformationValue.BooleanValue(it),
                                    R.string.cpu_is_online
                                )
                            },
                            linuxCpu.currentFrequencyHz?.let { currentFrequencyHz ->
                                Information(
                                    "current_frequency_hz",
                                    InformationValue.FrequencyValue(currentFrequencyHz),
                                    R.string.cpu_current_frequency,
                                )
                            },
                            linuxCpu.minimumFrequencyHz?.let { minimumFrequencyHz ->
                                Information(
                                    "minimum_frequency_hz",
                                    InformationValue.FrequencyValue(minimumFrequencyHz),
                                    R.string.cpu_minimum_frequency,
                                )
                            },
                            linuxCpu.maximumFrequencyHz?.let { maximumFrequencyHz ->
                                Information(
                                    "maximum_frequency_hz",
                                    InformationValue.FrequencyValue(maximumFrequencyHz),
                                    R.string.cpu_maximum_frequency,
                                )
                            },
                            linuxCpu.scalingCurrentFrequencyHz?.let { scalingCurrentFrequencyHz ->
                                Information(
                                    "scaling_current_frequency_hz",
                                    InformationValue.FrequencyValue(scalingCurrentFrequencyHz),
                                    R.string.cpu_scaling_current_frequency,
                                )
                            },
                            linuxCpu.scalingMinimumFrequencyHz?.let { scalingMinimumFrequencyHz ->
                                Information(
                                    "scaling_minimum_frequency_hz",
                                    InformationValue.FrequencyValue(scalingMinimumFrequencyHz),
                                    R.string.cpu_scaling_minimum_frequency,
                                )
                            },
                            linuxCpu.scalingMaximumFrequencyHz?.let { scalingMaximumFrequencyHz ->
                                Information(
                                    "scaling_maximum_frequency_hz",
                                    InformationValue.FrequencyValue(scalingMaximumFrequencyHz),
                                    R.string.cpu_scaling_maximum_frequency,
                                )
                            },
                        ),
                        R.string.cpu_processor_title,
                        arrayOf(processor.core.coreId, processor.smtId),
                    )
                }.toTypedArray(),
                *l1iCaches.withIndex().map { (i, cache) ->
                    Subsection(
                        "l1i_cache_${i}",
                        formatCacheInformation(cache),
                        R.string.cpu_l1i_cache_title,
                        arrayOf(i),
                    )
                }.toTypedArray(),
                *l1dCaches.withIndex().map { (i, cache) ->
                    Subsection(
                        "l1d_cache_${i}",
                        formatCacheInformation(cache),
                        R.string.cpu_l1d_cache_title,
                        arrayOf(i),
                    )
                }.toTypedArray(),
                *l2Caches.withIndex().map { (i, cache) ->
                    Subsection(
                        "l2_cache_${i}",
                        formatCacheInformation(cache),
                        R.string.cpu_l2_cache_title,
                        arrayOf(i),
                    )
                }.toTypedArray(),
                *l3Caches.withIndex().map { (i, cache) ->
                    Subsection(
                        "l3_cache_${i}",
                        formatCacheInformation(cache),
                        R.string.cpu_l3_cache_title,
                        arrayOf(i),
                    )
                }.toTypedArray(),
                *l4Caches.withIndex().map { (i, cache) ->
                    Subsection(
                        "l4_cache_${i}",
                        formatCacheInformation(cache),
                        R.string.cpu_l4_cache_title,
                        arrayOf(i),
                    )
                }.toTypedArray(),
            )
        }

        while (true) {
            trySend(getData())

            delay(1000)
        }
    }

    private fun formatCacheInformation(cache: Cache) = listOfNotNull(
        Information(
            "size",
            InformationValue.BytesValue(cache.size.toLong()),
            R.string.cpu_cache_size,
        ),
        Information(
            "associativity",
            InformationValue.UIntValue(cache.associativity),
            R.string.cpu_cache_associativity,
        ),
        Information(
            "sets",
            InformationValue.UIntValue(cache.sets),
            R.string.cpu_cache_sets,
        ),
        Information(
            "partitions",
            InformationValue.UIntValue(cache.partitions),
            R.string.cpu_cache_partitions,
        ),
        Information(
            "line_size",
            InformationValue.BytesValue(cache.lineSize.toLong()),
            R.string.cpu_cache_line_size,
        ),
        Information(
            "flags",
            InformationValue.UIntValue(cache.flags),
            R.string.cpu_cache_flags,
        ),
        Information(
            "processor_start",
            InformationValue.UIntValue(cache.processorStart),
            R.string.cpu_processor_start,
        ),
        Information(
            "processor_count",
            InformationValue.UIntValue(cache.processorCount),
            R.string.cpu_processor_count,
        ),
    )
}
