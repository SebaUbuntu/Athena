/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

fun <K, ListE> MutableMap<K, MutableList<ListE>>.getOrCreate(
    key: K,
) = getOrPut(key, ::mutableListOf)

fun <K, MapK, MapV> MutableMap<K, MutableMap<MapK, MapV>>.getOrCreate(
    key: K,
) = getOrPut(key, ::mutableMapOf)

fun <K, SetE> MutableMap<K, MutableSet<SetE>>.getOrCreate(
    key: K,
) = getOrPut(key, ::mutableSetOf)
