/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context

abstract class Section {
    abstract val name: Int
    abstract val description: Int
    abstract val icon: Int
    abstract val requiredPermissions: Array<String>

    abstract fun getInfo(context: Context): Map<String, Map<String, String>>

    companion object {
        enum class SectionEnum(val clazz: Section) {
            DEVICE(DeviceSection),
            STORAGE(StorageSection),
            BUILD(BuildSection),
            CPU(CpuSection),
            GPU(GpuSection),
            DISPLAY(DisplaySection),
            WIFI(WifiSection),
            BLUETOOTH(BluetoothSection),
            RIL(RilSection),
            GNSS(GnssSection),
            AUDIO(AudioSection),
            CAMERA(CameraSection),
            SENSORS(SensorsSection),
            BIOMETRICS(BiometricsSection),
            DRM(DrmSection),
            TREBLE(TrebleSection),
            INIT(InitSection),
            PROPS(PropsSection),
        }

        val sections = SectionEnum.values().associate {
            it.ordinal to it.clazz
        }
    }
}
