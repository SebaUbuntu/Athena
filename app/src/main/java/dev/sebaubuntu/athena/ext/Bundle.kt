/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import android.os.Build
import android.os.Bundle
import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

inline fun <reified T : Serializable> Bundle.getSerializable(key: String?, clazz: KClass<T>) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, clazz.java)
    } else {
        @Suppress("DEPRECATION")
        T::class.safeCast(getSerializable(key))
    }
