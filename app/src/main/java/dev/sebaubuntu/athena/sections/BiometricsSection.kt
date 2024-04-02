/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.biometric.BiometricManager
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.flow.flowOf

object BiometricsSection : Section() {
    override val title = R.string.section_biometrics_name
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

    override fun dataFlow(context: Context) = flowOf(
        BiometricManager.from(context).let { biometricManager ->
            listOfNotNull(
                Subsection(
                    "general",
                    listOf(
                        Information(
                            "can_authenticate_with_device_credential",
                            biometricErrorToString[
                                biometricManager.canAuthenticate(
                                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
                                )
                            ]?.let {
                                InformationValue.StringResValue(it)
                            },
                            R.string.biometrics_can_authenticate_with_device_credential,
                        ),
                        Information(
                            "can_authenticate_with_weak_biometric",
                            biometricErrorToString[
                                biometricManager.canAuthenticate(
                                    BiometricManager.Authenticators.BIOMETRIC_WEAK
                                )
                            ]?.let {
                                InformationValue.StringResValue(it)
                            },
                            R.string.biometrics_can_authenticate_with_weak_biometric,
                        ),
                        Information(
                            "can_authenticate_with_strong_biometric",
                            biometricErrorToString[
                                biometricManager.canAuthenticate(
                                    BiometricManager.Authenticators.BIOMETRIC_STRONG
                                )
                            ]?.let {
                                InformationValue.StringResValue(it)
                            },
                            R.string.biometrics_can_authenticate_with_strong_biometric,
                        ),
                    ),
                    R.string.biometrics_general,
                ),
                biometricManager.getAuthenticatorSubsection(
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL,
                    "device_credential",
                    R.string.device_credential_strings,
                ),
                biometricManager.getAuthenticatorSubsection(
                    BiometricManager.Authenticators.BIOMETRIC_WEAK,
                    "biometric_weak",
                    R.string.biometric_weak_strings,
                ),
                biometricManager.getAuthenticatorSubsection(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG,
                    "biometric_strong",
                    R.string.biometric_strong_strings,
                ),
            )
        }
    )

    private fun BiometricManager.getAuthenticatorSubsection(
        authenticator: Int,
        name: String,
        @StringRes titleResId: Int,
    ) = getStrings(authenticator)?.let { strings ->
        Subsection(
            "${name}_strings",
            listOf(
                Information(
                    "setting_name",
                    strings.settingName?.toString()?.let {
                        InformationValue.StringValue(it)
                    },
                    R.string.biometrics_setting_name,
                ),
                Information(
                    "button_label",
                    strings.buttonLabel?.toString()?.let {
                        InformationValue.StringValue(it)
                    },
                    R.string.biometrics_button_label,
                ),
                Information(
                    "prompt_message",
                    strings.promptMessage?.toString()?.let {
                        InformationValue.StringValue(it)
                    },
                    R.string.biometrics_prompt_message,
                ),
            ),
            titleResId,
        )
    }

    private val biometricErrorToString = mapOf(
        BiometricManager.BIOMETRIC_SUCCESS to R.string.biometric_error_success,
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE to R.string.biometric_error_hw_unavaliable,
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED to R.string.biometric_error_none_enrolled,
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE to R.string.biometric_error_no_hardware,
        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED to
                R.string.biometric_error_security_update_required,
    )
}
