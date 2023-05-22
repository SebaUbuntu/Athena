/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import dev.sebaubuntu.athena.R

object BiometricsSection : Section() {
    override val name = R.string.section_biometrics_name
    override val description = R.string.section_biometrics_description
    override val icon = R.drawable.ic_biometrics
    override val requiredPermissions = mutableListOf<String>().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.add(Manifest.permission.USE_BIOMETRIC)
        } else {
            @Suppress("DEPRECATION")
            this.add(Manifest.permission.USE_FINGERPRINT)
        }
    }.toTypedArray()

    override fun getInfo(context: Context) = mutableMapOf<String, Map<String, String>>().apply {
        val biometricManager = BiometricManager.from(context)

        this["General"] = mapOf(
            "Can authenticate with device credential" to (biometricErrorToString[
                    biometricManager.canAuthenticate(
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
            ] ?: "Unknown"),
            "Can authenticate with weak biometric" to (biometricErrorToString[
                    biometricManager.canAuthenticate(
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
                    )
            ] ?: "Unknown"),
            "Can authenticate with strong biometric" to (biometricErrorToString[
                    biometricManager.canAuthenticate(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG
                    )
            ] ?: "Unknown")
        )

        for ((authenticator, name) in mapOf(
            BiometricManager.Authenticators.DEVICE_CREDENTIAL to "Device credential",
            BiometricManager.Authenticators.BIOMETRIC_WEAK to "Weak biometric",
            BiometricManager.Authenticators.BIOMETRIC_STRONG to "Strong biometric",
        )) {
            val strings = biometricManager.getStrings(authenticator) ?: continue
            this["$name strings"] = mapOf(
                "Setting name" to strings.settingName.toString(),
                "Button label" to strings.buttonLabel.toString(),
                "Prompt message" to strings.promptMessage.toString(),
            )
        }
    }.toMap()

    private val biometricErrorToString = mapOf(
        BiometricManager.BIOMETRIC_SUCCESS to "Success",
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE to "Hardware unavailable",
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED to "No biometrics enrolled",
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE to "No biometric hardware",
        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED to "Security update required",
    )
}
