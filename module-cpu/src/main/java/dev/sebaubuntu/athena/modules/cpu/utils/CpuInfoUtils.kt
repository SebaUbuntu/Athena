/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.cpu.utils

import dev.sebaubuntu.athena.modules.cpu.models.Cache
import dev.sebaubuntu.athena.modules.cpu.models.Cluster
import dev.sebaubuntu.athena.modules.cpu.models.Core
import dev.sebaubuntu.athena.modules.cpu.models.Package
import dev.sebaubuntu.athena.modules.cpu.models.Processor
import dev.sebaubuntu.athena.modules.cpu.models.UarchInfo

object CpuInfoUtils {
    fun getProcessors() = ArrayList<Processor>().apply {
        getProcessors(this)
    }.toList()

    fun getCores() = ArrayList<Core>().apply {
        getCores(this)
    }.toList()

    fun getClusters() = ArrayList<Cluster>().apply {
        getClusters(this)
    }.toList()

    fun getPackages() = ArrayList<Package>().apply {
        getPackages(this)
    }.toList()

    fun getUarchs() = ArrayList<UarchInfo>().apply {
        getUarchs(this)
    }.toList()

    fun getL1iCaches() = ArrayList<Cache>().apply {
        getL1iCaches(this)
    }.toList()

    fun getL1dCaches() = ArrayList<Cache>().apply {
        getL1dCaches(this)
    }.toList()

    fun getL2Caches() = ArrayList<Cache>().apply {
        getL2Caches(this)
    }.toList()

    fun getL3Caches() = ArrayList<Cache>().apply {
        getL3Caches(this)
    }.toList()

    fun getL4Caches() = ArrayList<Cache>().apply {
        getL4Caches(this)
    }.toList()

    private external fun getProcessors(processors: ArrayList<Processor>)
    private external fun getCores(cores: ArrayList<Core>)
    private external fun getClusters(clusters: ArrayList<Cluster>)
    private external fun getPackages(packages: ArrayList<Package>)
    private external fun getUarchs(uarchs: ArrayList<UarchInfo>)
    private external fun getL1iCaches(l1iCaches: ArrayList<Cache>)
    private external fun getL1dCaches(l1dCaches: ArrayList<Cache>)
    private external fun getL2Caches(l2Caches: ArrayList<Cache>)
    private external fun getL3Caches(l3Caches: ArrayList<Cache>)
    private external fun getL4Caches(l4Caches: ArrayList<Cache>)
}
