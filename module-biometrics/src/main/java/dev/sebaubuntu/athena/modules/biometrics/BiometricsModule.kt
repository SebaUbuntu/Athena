/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.biometrics

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class BiometricsModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = BiometricsModule(context)
    }

    private val biometricManager = BiometricManager.from(context)

    override val id = "biometrics"

    override val name = LocalizedString(R.string.section_biometrics_name)

    override val description = LocalizedString(R.string.section_biometrics_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_fingerprint

    override val requiredPermissions = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            add(Manifest.permission.USE_BIOMETRIC)
        } else {
            @Suppress("DEPRECATION")
            add(Manifest.permission.USE_FINGERPRINT)
        }
    }.toTypedArray()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOf(
                    Element.Card(
                        name = "general",
                        title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                        elements = listOf(
                            Element.Item(
                                name = "can_authenticate_with_device_credential",
                                title = LocalizedString(R.string.biometrics_can_authenticate_with_device_credential),
                                value = Value(
                                    biometricManager.canAuthenticate(
                                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                                    ),
                                    biometricErrorToStringResId,
                                ),
                            ),
                            Element.Item(
                                name = "can_authenticate_with_weak_biometric",
                                title = LocalizedString(R.string.biometrics_can_authenticate_with_weak_biometric),
                                value = Value(
                                    biometricManager.canAuthenticate(
                                        BiometricManager.Authenticators.BIOMETRIC_WEAK
                                    ),
                                    biometricErrorToStringResId,
                                ),
                            ),
                            Element.Item(
                                name = "can_authenticate_with_strong_biometric",
                                title = LocalizedString(R.string.biometrics_can_authenticate_with_strong_biometric),
                                value = Value(
                                    biometricManager.canAuthenticate(
                                        BiometricManager.Authenticators.BIOMETRIC_STRONG
                                    ),
                                    biometricErrorToStringResId,
                                ),
                            ),
                        ),
                    )
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }

    companion object {
        private val biometricErrorToStringResId = mapOf(
            BiometricManager.BIOMETRIC_SUCCESS to R.string.biometric_error_success,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE to R.string.biometric_error_hw_unavaliable,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED to R.string.biometric_error_none_enrolled,
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE to R.string.biometric_error_no_hardware,
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED to
                    R.string.biometric_error_security_update_required,
        )
    }
}
