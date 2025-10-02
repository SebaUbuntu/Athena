/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.cpu

import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.modules.cpu.models.Cache
import dev.sebaubuntu.athena.modules.cpu.models.LinuxCpu
import dev.sebaubuntu.athena.modules.cpu.models.Midr
import dev.sebaubuntu.athena.modules.cpu.utils.CpuInfoUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class CpuModule : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = CpuModule()
    }

    override val id = "cpu"

    override val name = LocalizedString(R.string.section_cpu_name)

    override val description = LocalizedString(R.string.section_cpu_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_developer_board

    override val requiredPermissions = arrayOf<String>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> pollFlow {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOf(
                    Element.Card(
                        name = "abi",
                        title = LocalizedString(R.string.cpu_abi),
                        elements = listOf(
                            Element.Item(
                                name = "supported_abis",
                                title = LocalizedString(R.string.cpu_supported_abis),
                                value = Value(Build.SUPPORTED_ABIS),
                            ),
                            Element.Item(
                                name = "supported_64_bit_abis",
                                title = LocalizedString(R.string.cpu_supported_64_bit_abis),
                                value = Value(Build.SUPPORTED_64_BIT_ABIS),
                            ),
                            Element.Item(
                                name = "supported_32_bit_abis",
                                title = LocalizedString(R.string.cpu_supported_32_bit_abis),
                                value = Value(Build.SUPPORTED_32_BIT_ABIS),
                            ),
                        ),
                    ),
                    Element.Card(
                        name = "general",
                        title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                        elements = listOfNotNull(
                            CpuInfoUtils.getProcessors().takeIf { it.isNotEmpty() }?.let {
                                Element.Item(
                                    name = "processors",
                                    title = LocalizedString(R.string.cpu_processors),
                                    navigateTo = identifier / "processors",
                                    drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory,
                                    value = Value(it.size),
                                )
                            },
                            CpuInfoUtils.getCores().takeIf { it.isNotEmpty() }?.let {
                                Element.Item(
                                    name = "cores",
                                    title = LocalizedString(R.string.cpu_cores),
                                    navigateTo = identifier / "cores",
                                    drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory,
                                    value = Value(it.size),
                                )
                            },
                            CpuInfoUtils.getClusters().takeIf { it.isNotEmpty() }?.let {
                                Element.Item(
                                    name = "clusters",
                                    title = LocalizedString(R.string.cpu_clusters),
                                    navigateTo = identifier / "clusters",
                                    drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory,
                                    value = Value(it.size),
                                )
                            },
                            CpuInfoUtils.getPackages().takeIf { it.isNotEmpty() }?.let {
                                Element.Item(
                                    name = "packages",
                                    title = LocalizedString(R.string.cpu_packages),
                                    navigateTo = identifier / "packages",
                                    drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_developer_board,
                                    value = Value(it.size),
                                )
                            },
                            CpuInfoUtils.getUarchs().takeIf { it.isNotEmpty() }?.let {
                                Element.Item(
                                    name = "uarchs",
                                    title = LocalizedString(R.string.cpu_uarchs),
                                    navigateTo = identifier / "uarchs",
                                    drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_architecture,
                                    value = Value(it.size),
                                )
                            },
                            CpuInfoUtils.getL1iCaches().takeIf { it.isNotEmpty() }?.let {
                                Element.Item(
                                    name = "l1i_caches",
                                    title = LocalizedString(R.string.cpu_l1i_caches),
                                    navigateTo = identifier / "l1i_caches",
                                    drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory_alt,
                                    value = Value(it.size),
                                )
                            },
                            CpuInfoUtils.getL1dCaches().takeIf { it.isNotEmpty() }?.let {
                                Element.Item(
                                    name = "l1d_caches",
                                    title = LocalizedString(R.string.cpu_l1d_caches),
                                    navigateTo = identifier / "l1d_caches",
                                    drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory_alt,
                                    value = Value(it.size),
                                )
                            },
                            CpuInfoUtils.getL2Caches().takeIf { it.isNotEmpty() }?.let {
                                Element.Item(
                                    name = "l2_caches",
                                    title = LocalizedString(R.string.cpu_l2_caches),
                                    navigateTo = identifier / "l2_caches",
                                    drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory_alt,
                                    value = Value(it.size),
                                )
                            },
                            CpuInfoUtils.getL3Caches().takeIf { it.isNotEmpty() }?.let {
                                Element.Item(
                                    name = "l3_caches",
                                    title = LocalizedString(R.string.cpu_l3_caches),
                                    navigateTo = identifier / "l3_caches",
                                    drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory_alt,
                                    value = Value(it.size),
                                )
                            },
                            CpuInfoUtils.getL4Caches().takeIf { it.isNotEmpty() }?.let {
                                Element.Item(
                                    name = "l4_caches",
                                    title = LocalizedString(R.string.cpu_l4_caches),
                                    navigateTo = identifier / "l4_caches",
                                    drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory_alt,
                                    value = Value(it.size),
                                )
                            },
                        ),
                    ),
                )
            )

            Result.Success<Resource, Error>(screen)
        }

        "clusters" -> when (identifier.path.getOrNull(1)) {
            null -> pollFlow {
                val clusters = CpuInfoUtils.getClusters()

                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.cpu_clusters),
                    elements = clusters.map {
                        Element.Item(
                            name = "${it.clusterId}",
                            title = LocalizedString(
                                R.string.cpu_cluster_title,
                                it.clusterId,
                            ),
                            navigateTo = identifier / "${it.clusterId}",
                            drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory,
                        )
                    },
                )

                Result.Success<Resource, Error>(screen)
            }

            else -> when (identifier.path.getOrNull(2)) {
                null -> pollFlow {
                    val clusterId = identifier.path[1].toUIntOrNull()

                    val cluster = clusterId?.let { clusterId ->
                        CpuInfoUtils.getClusters().firstOrNull {
                            it.clusterId == clusterId
                        }
                    }

                    val screen = cluster?.let { cluster ->
                        Screen.CardListScreen(
                            identifier = identifier,
                            title = LocalizedString(
                                R.string.cpu_cluster_title,
                                cluster.clusterId,
                            ),
                            elements = listOfNotNull(
                                Element.Card(
                                    name = "general",
                                    title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                                    elements = listOfNotNull(
                                        Element.Item(
                                            name = "processor_start",
                                            title = LocalizedString(R.string.cpu_processor_start),
                                            value = Value(cluster.processorStart),
                                        ),
                                        Element.Item(
                                            name = "processor_count",
                                            title = LocalizedString(R.string.cpu_processor_count),
                                            value = Value(cluster.processorCount),
                                        ),
                                        Element.Item(
                                            name = "core_start",
                                            title = LocalizedString(R.string.cpu_core_start),
                                            value = Value(cluster.coreStart),
                                        ),
                                        Element.Item(
                                            name = "core_count",
                                            title = LocalizedString(R.string.cpu_core_count),
                                            value = Value(cluster.coreCount),
                                        ),
                                        Element.Item(
                                            name = "cluster_id",
                                            title = LocalizedString(R.string.cpu_cluster_id),
                                            value = Value(cluster.clusterId),
                                        ),
                                        Element.Item(
                                            name = "vendor",
                                            title = LocalizedString(R.string.cpu_vendor),
                                            value = Value(cluster.vendor),
                                        ),
                                        Element.Item(
                                            name = "uarch",
                                            title = LocalizedString(R.string.cpu_uarch),
                                            value = Value(cluster.uarch),
                                        ),
                                        cluster.cpuid?.let {
                                            Element.Item(
                                                name = "cpuid",
                                                title = LocalizedString(R.string.cpu_cpuid),
                                                value = Value(it),
                                            )
                                        },
                                        Element.Item(
                                            name = "frequency",
                                            title = LocalizedString(R.string.cpu_frequency),
                                            value = Value(cluster.frequency),
                                        ),
                                    ),
                                ),
                                cluster.midr?.getCardElement(),
                            ),
                        )
                    }

                    screen?.let {
                        Result.Success<Resource, Error>(it)
                    } ?: Result.Error(Error.NOT_FOUND)
                }

                else -> flowOf(Result.Error(Error.NOT_FOUND))
            }
        }

        "cores" -> when (identifier.path.getOrNull(1)) {
            null -> pollFlow {
                val cores = CpuInfoUtils.getCores()

                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.cpu_cores),
                    elements = cores.map { core ->
                        Element.Item(
                            name = "${core.coreId}",
                            title = LocalizedString(
                                R.string.cpu_core_title,
                                core.coreId,
                            ),
                            navigateTo = identifier / "${core.coreId}",
                            drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory,
                        )
                    },
                )

                Result.Success<Resource, Error>(screen)
            }

            else -> when (identifier.path.getOrNull(2)) {
                null -> pollFlow {
                    val coreId = identifier.path[1].toUIntOrNull()

                    val core = coreId?.let { coreId ->
                        CpuInfoUtils.getCores().firstOrNull {
                            it.coreId == coreId
                        }
                    }

                    val screen = core?.let { core ->
                        Screen.CardListScreen(
                            identifier = identifier,
                            title = LocalizedString(
                                R.string.cpu_core_title,
                                core.coreId,
                            ),
                            elements = listOfNotNull(
                                Element.Card(
                                    name = "general",
                                    title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                                    elements = listOfNotNull(
                                        Element.Item(
                                            name = "processor_start",
                                            title = LocalizedString(R.string.cpu_processor_start),
                                            value = Value(core.processorStart),
                                        ),
                                        Element.Item(
                                            name = "processor_count",
                                            title = LocalizedString(R.string.cpu_processor_count),
                                            value = Value(core.processorCount),
                                        ),
                                        Element.Item(
                                            name = "core_id",
                                            title = LocalizedString(R.string.cpu_core_id),
                                            value = Value(core.coreId),
                                        ),
                                        Element.Item(
                                            name = "${core.cluster.clusterId}",
                                            title = LocalizedString(R.string.cpu_cluster_id),
                                            value = Value(core.cluster.clusterId),
                                        ),
                                        Element.Item(
                                            name = "vendor",
                                            title = LocalizedString(R.string.cpu_vendor),
                                            value = Value(core.vendor),
                                        ),
                                        Element.Item(
                                            name = "uarch",
                                            title = LocalizedString(R.string.cpu_uarch),
                                            value = Value(core.uarch),
                                        ),
                                        core.cpuid?.let {
                                            Element.Item(
                                                name = "cpuid",
                                                title = LocalizedString(R.string.cpu_cpuid),
                                                value = Value(it),
                                            )
                                        },
                                        Element.Item(
                                            name = "frequency",
                                            title = LocalizedString(R.string.cpu_frequency),
                                            value = Value(core.frequency),
                                        ),
                                    ),
                                ),
                            ),
                        )
                    }

                    screen?.let {
                        Result.Success<Resource, Error>(it)
                    } ?: Result.Error(Error.NOT_FOUND)
                }

                else -> flowOf(Result.Error(Error.NOT_FOUND))
            }
        }

        "l1d_caches" -> cachePath(
            identifier = identifier,
            cachesGetter = CpuInfoUtils::getL1dCaches,
            cachesStringResId = R.string.cpu_l1d_caches,
            cacheStringResId = R.string.cpu_l1d_cache_title,
        )

        "l1i_caches" -> cachePath(
            identifier = identifier,
            cachesGetter = CpuInfoUtils::getL1iCaches,
            cachesStringResId = R.string.cpu_l1i_caches,
            cacheStringResId = R.string.cpu_l1i_cache_title,
        )

        "l2_caches" -> cachePath(
            identifier = identifier,
            cachesGetter = CpuInfoUtils::getL2Caches,
            cachesStringResId = R.string.cpu_l2_caches,
            cacheStringResId = R.string.cpu_l2_cache_title,
        )

        "l3_caches" -> cachePath(
            identifier = identifier,
            cachesGetter = CpuInfoUtils::getL3Caches,
            cachesStringResId = R.string.cpu_l3_caches,
            cacheStringResId = R.string.cpu_l3_cache_title,
        )

        "l4_caches" -> cachePath(
            identifier = identifier,
            cachesGetter = CpuInfoUtils::getL4Caches,
            cachesStringResId = R.string.cpu_l4_caches,
            cacheStringResId = R.string.cpu_l4_cache_title,
        )

        "packages" -> when (identifier.path.getOrNull(1)) {
            null -> pollFlow {
                val packages = CpuInfoUtils.getPackages()

                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.cpu_packages),
                    elements = packages.withIndex().map { (index, value) ->
                        Element.Item(
                            name = "$index",
                            title = LocalizedString(
                                R.string.cpu_package_title,
                                index,
                            ),
                            navigateTo = identifier / "$index",
                            drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_developer_board,
                            value = Value(value.name),
                        )
                    },
                )

                Result.Success<Resource, Error>(screen)
            }

            else -> when (identifier.path.getOrNull(2)) {
                null -> pollFlow {
                    val packageIndex = identifier.path[1].toIntOrNull()

                    val value = packageIndex?.let { packageIndex ->
                        CpuInfoUtils.getPackages().withIndex().firstOrNull { (index, _) ->
                            index == packageIndex
                        }
                    }

                    val screen = value?.let { (index, value) ->
                        Screen.CardListScreen(
                            identifier = identifier,
                            title = LocalizedString(
                                R.string.cpu_package_title,
                                index,
                            ),
                            elements = listOf(
                                Element.Card(
                                    name = "general",
                                    title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                                    elements = listOf(
                                        Element.Item(
                                            name = "name",
                                            title = LocalizedString(R.string.cpu_package_name),
                                            value = Value(value.name),
                                        ),
                                        Element.Item(
                                            name = "processor_start",
                                            title = LocalizedString(R.string.cpu_processor_start),
                                            value = Value(value.processorStart),
                                        ),
                                        Element.Item(
                                            name = "processor_count",
                                            title = LocalizedString(R.string.cpu_processor_count),
                                            value = Value(value.processorCount),
                                        ),
                                        Element.Item(
                                            name = "core_start",
                                            title = LocalizedString(R.string.cpu_core_start),
                                            value = Value(value.coreStart),
                                        ),
                                        Element.Item(
                                            name = "core_count",
                                            title = LocalizedString(R.string.cpu_core_count),
                                            value = Value(value.coreCount),
                                        ),
                                        Element.Item(
                                            name = "cluster_start",
                                            title = LocalizedString(R.string.cpu_cluster_start),
                                            value = Value(value.clusterStart),
                                        ),
                                        Element.Item(
                                            name = "cluster_count",
                                            title = LocalizedString(R.string.cpu_cluster_count),
                                            value = Value(value.clusterCount),
                                        ),
                                    ),
                                ),
                            ),
                        )
                    }

                    screen?.let {
                        Result.Success<Resource, Error>(it)
                    } ?: Result.Error(Error.NOT_FOUND)
                }

                else -> flowOf(Result.Error(Error.NOT_FOUND))
            }
        }

        "processors" -> when (identifier.path.getOrNull(1)) {
            null -> pollFlow {
                val processors = CpuInfoUtils.getProcessors()

                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.cpu_processors),
                    elements = processors.map {
                        Element.Item(
                            name = "${it.linuxId}",
                            title = LocalizedString(
                                R.string.cpu_processor,
                                it.linuxId,
                            ),
                            navigateTo = identifier / "${it.linuxId}",
                            drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory,
                        )
                    },
                )

                Result.Success<Resource, Error>(screen)
            }

            else -> when (identifier.path.getOrNull(2)) {
                null -> pollFlow {
                    val linuxId = identifier.path[1].toUIntOrNull()

                    val processor = linuxId?.let { linuxId ->
                        CpuInfoUtils.getProcessors().firstOrNull {
                            it.linuxId == linuxId
                        }
                    }

                    val screen = processor?.let { processor ->
                        val linuxCpu = LinuxCpu.fromProcessor(processor)

                        Screen.CardListScreen(
                            identifier = identifier,
                            title = LocalizedString(
                                R.string.cpu_processor,
                                processor.linuxId,
                            ),
                            elements = listOf(
                                Element.Card(
                                    name = "general",
                                    title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                                    elements = listOfNotNull(
                                        Element.Item(
                                            name = "smt_id",
                                            title = LocalizedString(R.string.cpu_processor_smt_id),
                                            value = Value(processor.smtId),
                                        ),
                                        Element.Item(
                                            name = "core_id",
                                            title = LocalizedString(R.string.cpu_core_id),
                                            value = Value(processor.core.coreId),
                                        ),
                                        Element.Item(
                                            name = "cluster_id",
                                            title = LocalizedString(R.string.cpu_cluster_id),
                                            value = Value(processor.cluster.clusterId),
                                        ),
                                        Element.Item(
                                            name = "linux_id",
                                            title = LocalizedString(R.string.cpu_processor_linux_id),
                                            value = Value(processor.linuxId),
                                        ),
                                        processor.apicId?.let {
                                            Element.Item(
                                                name = "apic_id",
                                                title = LocalizedString(R.string.cpu_processor_apic_id),
                                                value = Value(it),
                                            )
                                        },
                                        Element.Item(
                                            name = "cache",
                                            title = LocalizedString(R.string.cpu_processor_cache),
                                            value = Value(processor.cache.toString()),
                                        ),
                                    ),
                                ),
                                Element.Card(
                                    name = "status",
                                    title = LocalizedString(R.string.cpu_status),
                                    elements = listOfNotNull(
                                        linuxCpu.isOnline?.let {
                                            Element.Item(
                                                name = "is_online",
                                                title = LocalizedString(R.string.cpu_is_online),
                                                value = Value(it),
                                            )
                                        },
                                        linuxCpu.currentFrequencyHz?.let { currentFrequencyHz ->
                                            Element.Item(
                                                name = "current_frequency_hz",
                                                title = LocalizedString(R.string.cpu_current_frequency),
                                                value = Value.FrequencyValue(currentFrequencyHz),
                                            )
                                        },
                                        linuxCpu.minimumFrequencyHz?.let { minimumFrequencyHz ->
                                            Element.Item(
                                                name = "minimum_frequency_hz",
                                                title = LocalizedString(R.string.cpu_minimum_frequency),
                                                value = Value.FrequencyValue(minimumFrequencyHz),
                                            )
                                        },
                                        linuxCpu.maximumFrequencyHz?.let { maximumFrequencyHz ->
                                            Element.Item(
                                                name = "maximum_frequency_hz",
                                                title = LocalizedString(R.string.cpu_maximum_frequency),
                                                value = Value.FrequencyValue(maximumFrequencyHz),
                                            )
                                        },
                                        linuxCpu.scalingCurrentFrequencyHz?.let { scalingCurrentFrequencyHz ->
                                            Element.Item(
                                                name = "scaling_current_frequency_hz",
                                                title = LocalizedString(R.string.cpu_scaling_current_frequency),
                                                value = Value.FrequencyValue(
                                                    scalingCurrentFrequencyHz
                                                ),
                                            )
                                        },
                                        linuxCpu.scalingMinimumFrequencyHz?.let { scalingMinimumFrequencyHz ->
                                            Element.Item(
                                                name = "scaling_minimum_frequency_hz",
                                                title = LocalizedString(R.string.cpu_scaling_minimum_frequency),
                                                value = Value.FrequencyValue(
                                                    scalingMinimumFrequencyHz
                                                ),
                                            )
                                        },
                                        linuxCpu.scalingMaximumFrequencyHz?.let { scalingMaximumFrequencyHz ->
                                            Element.Item(
                                                name = "scaling_maximum_frequency_hz",
                                                title = LocalizedString(R.string.cpu_scaling_maximum_frequency),
                                                value = Value.FrequencyValue(
                                                    scalingMaximumFrequencyHz
                                                ),
                                            )
                                        },
                                    ),
                                )
                            ),
                        )
                    }

                    screen?.let {
                        Result.Success<Resource, Error>(it)
                    } ?: Result.Error(Error.NOT_FOUND)
                }

                else -> flowOf(Result.Error(Error.NOT_FOUND))
            }
        }

        "uarchs" -> when (identifier.path.getOrNull(1)) {
            null -> pollFlow {
                val uarchs = CpuInfoUtils.getUarchs()

                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.cpu_uarchs),
                    elements = uarchs.withIndex().map { (index, uarchInfo) ->
                        Element.Item(
                            name = "$index",
                            title = LocalizedString(
                                R.string.cpu_uarch_title,
                                index,
                            ),
                            navigateTo = identifier / "$index",
                            drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_architecture,
                            value = Value(uarchInfo.uarch),
                        )
                    },
                )

                Result.Success<Resource, Error>(screen)
            }

            else -> when (identifier.path.getOrNull(2)) {
                null -> pollFlow {
                    val uarchIndex = identifier.path[1].toIntOrNull()

                    val uarch = uarchIndex?.let { uarchIndex ->
                        CpuInfoUtils.getUarchs().withIndex().firstOrNull { (index, _) ->
                            index == uarchIndex
                        }
                    }

                    val screen = uarch?.let { (index, uarch) ->
                        Screen.CardListScreen(
                            identifier = identifier,
                            title = LocalizedString(
                                R.string.cpu_uarch_title,
                                index,
                            ),
                            elements = listOfNotNull(
                                Element.Card(
                                    name = "general",
                                    title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                                    elements = listOfNotNull(
                                        Element.Item(
                                            name = "uarch",
                                            title = LocalizedString(R.string.cpu_uarch),
                                            value = Value(uarch.uarch),
                                        ),
                                        uarch.cpuid?.let {
                                            Element.Item(
                                                name = "cpuid",
                                                title = LocalizedString(R.string.cpu_cpuid),
                                                value = Value(it),
                                            )
                                        },
                                        Element.Item(
                                            name = "processor_count",
                                            title = LocalizedString(R.string.cpu_processor_count),
                                            value = Value(uarch.processorCount),
                                        ),
                                        Element.Item(
                                            name = "core_count",
                                            title = LocalizedString(R.string.cpu_core_count),
                                            value = Value(uarch.coreCount),
                                        ),
                                    ),
                                ),
                                uarch.midr?.getCardElement(),
                            ),
                        )
                    }

                    screen?.let {
                        Result.Success<Resource, Error>(it)
                    } ?: Result.Error(Error.NOT_FOUND)
                }

                else -> flowOf(Result.Error(Error.NOT_FOUND))
            }
        }

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }

    private fun cachePath(
        identifier: Resource.Identifier,
        cachesGetter: () -> List<Cache>,
        @StringRes cachesStringResId: Int,
        @StringRes cacheStringResId: Int,
    ) = when (identifier.path.getOrNull(1)) {
        null -> pollFlow {
            val caches = cachesGetter()

            val screen = Screen.ItemListScreen(
                identifier = identifier,
                title = LocalizedString(cachesStringResId),
                elements = caches.withIndex().map { (index, _) ->
                    Element.Item(
                        name = "$index",
                        title = LocalizedString(
                            cacheStringResId,
                            index,
                        ),
                        navigateTo = identifier / "$index",
                        drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_memory_alt,
                    )
                },
            )

            Result.Success<Resource, Error>(screen)
        }

        else -> when (identifier.path.getOrNull(2)) {
            null -> pollFlow {
                val cacheIndex = identifier.path[1].toIntOrNull()

                val cache = cacheIndex?.let { cacheIndex ->
                    cachesGetter().withIndex().firstOrNull { (index, _) ->
                        index == cacheIndex
                    }
                }

                val screen = cache?.let { (index, cache) ->
                    Screen.CardListScreen(
                        identifier = identifier,
                        title = LocalizedString(
                            cacheStringResId,
                            index,
                        ),
                        elements = listOfNotNull(
                            Element.Card(
                                name = "general",
                                title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                                elements = listOfNotNull(
                                    Element.Item(
                                        name = "size",
                                        title = LocalizedString(R.string.cpu_cache_size),
                                        value = Value.BytesValue(cache.size.toLong()),
                                    ),
                                    Element.Item(
                                        name = "associativity",
                                        title = LocalizedString(R.string.cpu_cache_associativity),
                                        value = Value(cache.associativity),
                                    ),
                                    Element.Item(
                                        name = "sets",
                                        title = LocalizedString(R.string.cpu_cache_sets),
                                        value = Value(cache.sets),
                                    ),
                                    Element.Item(
                                        name = "partitions",
                                        title = LocalizedString(R.string.cpu_cache_partitions),
                                        value = Value(cache.partitions),
                                    ),
                                    Element.Item(
                                        name = "line_size",
                                        title = LocalizedString(R.string.cpu_cache_line_size),
                                        value = Value(cache.lineSize),
                                    ),
                                    Element.Item(
                                        name = "flags",
                                        title = LocalizedString(R.string.cpu_cache_flags),
                                        value = Value(cache.flags),
                                    ),
                                    Element.Item(
                                        name = "processor_start",
                                        title = LocalizedString(R.string.cpu_processor_start),
                                        value = Value(cache.processorStart),
                                    ),
                                    Element.Item(
                                        name = "processor_count",
                                        title = LocalizedString(R.string.cpu_processor_count),
                                        value = Value(cache.processorCount),
                                    ),
                                ),
                            ),
                        ),
                    )
                }

                screen?.let {
                    Result.Success<Resource, Error>(it)
                } ?: Result.Error(Error.NOT_FOUND)
            }

            else -> flowOf(Result.Error(Error.NOT_FOUND))
        }
    }

    private fun Midr.getCardElement() = Element.Card(
        name = "midr",
        title = LocalizedString(R.string.cpu_midr),
        elements = listOf(
            Element.Item(
                name = "implementer",
                title = LocalizedString(R.string.cpu_midr_implementer),
                value = Value(implementer),
            ),
            Element.Item(
                name = "variant",
                title = LocalizedString(R.string.cpu_midr_variant),
                value = Value(variant),
            ),
            Element.Item(
                name = "architecture",
                title = LocalizedString(R.string.cpu_midr_architecture),
                value = Value(architecture),
            ),
            Element.Item(
                name = "primary_part_number",
                title = LocalizedString(R.string.cpu_midr_primary_part_number),
                value = Value(primaryPartNumber),
            ),
            Element.Item(
                name = "revision",
                title = LocalizedString(R.string.cpu_midr_revision),
                value = Value(revision),
            ),
        ),
    )

    private fun <T> pollFlow(
        delayDuration: Duration = 1.seconds,
        block: suspend () -> T,
    ) = flow {
        while (true) {
            emit(block())

            delay(delayDuration)
        }
    }

    companion object {
        init {
            System.loadLibrary("athena_cpu")
        }
    }
}
