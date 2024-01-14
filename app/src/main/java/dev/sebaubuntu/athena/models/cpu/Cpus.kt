/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.cpu

import java.util.regex.Pattern
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

data class Cpus(
    val cpus: Set<Cpu>,
) {
    val physicalPackages: Map<Int, Set<Cpu>>?
        get() = cpus.mapNotNull {
            it.physicalPackageId
        }.associateWith { physicalPackageId ->
            cpus.filter { it.physicalPackageId == physicalPackageId }.toSet()
        }.takeIf { it.isNotEmpty() }

    val clusters: Map<Int, Set<Cpu>>?
        get() = cpus.mapNotNull {
            it.clusterId
        }.associateWith { clusterId ->
            cpus.filter { it.clusterId == clusterId }.toSet()
        }.takeIf { it.isNotEmpty() }

    val dies: Map<Int, Set<Cpu>>?
        get() = cpus.mapNotNull {
            it.dieId
        }.associateWith { dieId ->
            cpus.filter { it.dieId == dieId }.toSet()
        }.takeIf { it.isNotEmpty() }

    companion object {
        fun get() = Cpus(
            cpus = mutableSetOf<Cpu>().apply {
                val cpusDirs = Cpu.CPUINFO_BASE_DIR.listDirectoryEntries().filter {
                    Pattern.matches("cpu[0-9]+", it.name)
                }
                for (dir in cpusDirs) {
                    val cpuId = runCatching {
                        dir.name.removePrefix("cpu").toInt()
                    }.getOrNull() ?: continue

                    add(Cpu(cpuId))
                }
            }.toSet()
        )
    }
}
