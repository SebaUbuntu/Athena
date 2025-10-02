/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
import androidx.biometric.BiometricManager
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.components.Component
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Permission
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class BiometricsComponent(context: Context) : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = BiometricsComponent(context)
    }

    private val biometricManager = BiometricManager.from(context)

    override val name = "biometrics"

    override val title = LocalizedString(R.string.section_biometrics_name)

    override val description = LocalizedString(R.string.section_biometrics_description)

    override val drawableResId = R.drawable.ic_fingerprint

    override val permissions = setOf(Permission.BIOMETRICS)

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = title,
                elements = listOf(
                    Element.Card(
                        identifier = identifier / "general",
                        title = LocalizedString(R.string.biometrics_general),
                        elements = listOf(
                            Element.Item(
                                identifier = identifier / "can_authenticate_with_device_credential",
                                title = LocalizedString(R.string.biometrics_can_authenticate_with_device_credential),
                                value = Value(
                                    biometricManager.canAuthenticate(
                                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                                    ),
                                    biometricErrorToStringResId,
                                ),
                            ),
                            Element.Item(
                                identifier = identifier / "can_authenticate_with_weak_biometric",
                                title = LocalizedString(R.string.biometrics_can_authenticate_with_weak_biometric),
                                value = Value(
                                    biometricManager.canAuthenticate(
                                        BiometricManager.Authenticators.BIOMETRIC_WEAK
                                    ),
                                    biometricErrorToStringResId,
                                ),
                            ),
                            Element.Item(
                                identifier = identifier / "can_authenticate_with_strong_biometric",
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

    private val biometricErrorToStringResId = mapOf(
        BiometricManager.BIOMETRIC_SUCCESS to R.string.biometric_error_success,
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE to R.string.biometric_error_hw_unavaliable,
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED to R.string.biometric_error_none_enrolled,
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE to R.string.biometric_error_no_hardware,
        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED to
                R.string.biometric_error_security_update_required,
    )
}
