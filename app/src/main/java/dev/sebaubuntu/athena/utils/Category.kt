/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import android.content.Context
import dev.sebaubuntu.athena.categories.AudioCategory
import dev.sebaubuntu.athena.categories.BiometricsCategory
import dev.sebaubuntu.athena.categories.BluetoothCategory
import dev.sebaubuntu.athena.categories.BuildCategory
import dev.sebaubuntu.athena.categories.CameraCategory
import dev.sebaubuntu.athena.categories.CpuCategory
import dev.sebaubuntu.athena.categories.DeviceCategory
import dev.sebaubuntu.athena.categories.DisplayCategory
import dev.sebaubuntu.athena.categories.DrmCategory
import dev.sebaubuntu.athena.categories.GnssCategory
import dev.sebaubuntu.athena.categories.GpuCategory
import dev.sebaubuntu.athena.categories.PropsCategory
import dev.sebaubuntu.athena.categories.RilCategory
import dev.sebaubuntu.athena.categories.StorageCategory
import dev.sebaubuntu.athena.categories.TrebleCategory
import dev.sebaubuntu.athena.categories.WifiCategory

interface Category {
    val name: Int
    val description: Int
    val icon: Int
    val requiredPermissions: Array<String>

    fun getInfo(context: Context): Map<String, Map<String, String>>

    companion object {
        enum class CategoryEnum(val clazz: Category) {
            DEVICE(DeviceCategory),
            STORAGE(StorageCategory),
            BUILD(BuildCategory),
            CPU(CpuCategory),
            GPU(GpuCategory),
            DISPLAY(DisplayCategory),
            WIFI(WifiCategory),
            BLUETOOTH(BluetoothCategory),
            RIL(RilCategory),
            GNSS(GnssCategory),
            AUDIO(AudioCategory),
            CAMERA(CameraCategory),
            BIOMETRICS(BiometricsCategory),
            DRM(DrmCategory),
            TREBLE(TrebleCategory),
            PROPS(PropsCategory),
        }

        val categories = CategoryEnum.values().associate {
            it.ordinal to it.clazz
        }
    }
}
