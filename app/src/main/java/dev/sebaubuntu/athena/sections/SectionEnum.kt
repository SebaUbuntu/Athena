/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

enum class SectionEnum(val clazz: Section) {
    DEVICE(DeviceSection),
    STORAGE(StorageSection),
    BUILD(BuildSection),
    USER(UserSection),
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
    THERMAL(ThermalSection),
    BIOMETRICS(BiometricsSection),
    MEDIA(MediaSection),
    DRM(DrmSection),
    TREBLE(TrebleSection),
    SERVICES(ServicesSection),
    PROPS(PropsSection),
}
