/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.StringRes
import androidx.security.state.SecurityPatchState
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.flow.asFlow
import okhttp3.OkHttpClient
import okhttp3.Request

object SecuritySection : Section(
    "security",
    R.string.section_security_name,
    R.string.section_security_description,
    R.drawable.ic_security,
    arrayOf(
        Manifest.permission.INTERNET,
    )
) {
    private val LOG_TAG = this::class.simpleName!!

    enum class Error(
        @StringRes val stringResId: Int,
    ) {
        ANDROID_VERSION_NOT_SUPPORTED(
            R.string.security_error_android_version_not_supported
        ),
        FAILED_TO_FETCH_INFORMATION(
            R.string.security_error_failed_to_fetch_information
        );

        companion object {
            val errorToStringResId = values().associateWith {
                it.stringResId
            }
        }
    }

    enum class Component(
        val value: String,
        @StringRes val stringResId: Int,
        val supportsCve: Boolean = false,
    ) {
        SYSTEM(
            SecurityPatchState.COMPONENT_SYSTEM,
            R.string.security_component_system,
            true,
        ),
        SYSTEM_MODULES(
            SecurityPatchState.COMPONENT_SYSTEM_MODULES,
            R.string.security_component_system_modules,
            true,
        ),
        VENDOR(
            SecurityPatchState.COMPONENT_VENDOR,
            R.string.security_component_vendor,
            true,
        ),
        KERNEL(
            SecurityPatchState.COMPONENT_KERNEL,
            R.string.security_component_kernel,
        ),
        // TODO: SecurityPatchState.COMPONENT_WEBVIEW
    }

    override fun dataFlow(context: Context) = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create SecurityPatchState object
            val securityPatchState = SecurityPatchState(
                context,
                SecurityPatchState.DEFAULT_SYSTEM_MODULES,
            )

            // Call getVulnerabilityReportUrl()
            val vulnerabilityReportUrl = securityPatchState.getVulnerabilityReportUrl(
                Uri.parse(SecurityPatchState.DEFAULT_VULNERABILITY_REPORTS_URL)
            )

            // Download JSON file containing vulnerability report data
            val okHttpClient = OkHttpClient()
            val request = Request.Builder()
                .url(vulnerabilityReportUrl.toString())
                .build()

            runCatching {
                okHttpClient.newCall(request).execute().use { response ->
                    response.body?.string()
                }
            }.onFailure {
                Log.e(LOG_TAG, "Failed to fetch information", it)
            }.getOrNull()?.let { jsonString ->
                // Call loadVulnerabilityReport()
                runCatching {
                    securityPatchState.loadVulnerabilityReport(jsonString)
                }.exceptionOrNull()?.let {
                    Log.e(LOG_TAG, "Failed to load the vulnerability report", it)

                    onErrorData(Error.FAILED_TO_FETCH_INFORMATION)
                } ?: listOf(
                    Subsection(
                        "general",
                        listOf(
                            Information(
                                "available_updates",
                                InformationValue.StringArrayValue(
                                    securityPatchState.listAvailableUpdates().map {
                                        it.toString()
                                    }.toTypedArray(),
                                ),
                                R.string.security_available_updates,
                            ),
                            Information(
                                "is_device_fully_updated",
                                InformationValue.BooleanValue(
                                    securityPatchState.isDeviceFullyUpdated()
                                ),
                                R.string.security_is_device_fully_updated,
                            ),
                        ),
                        R.string.security_general,
                    ),
                    *Component.values().map {
                        securityPatchState.getComponentSubsection(it)
                    }.toTypedArray(),
                )
            } ?: onErrorData(Error.FAILED_TO_FETCH_INFORMATION)
        } else {
            onErrorData(Error.ANDROID_VERSION_NOT_SUPPORTED)
        }
    }.asFlow()

    private fun onErrorData(error: Error) = listOf(
        Subsection(
            "general",
            listOf(
                Information(
                    "error",
                    InformationValue.EnumValue(
                        error,
                        Error.errorToStringResId,
                    ),
                    R.string.security_error,
                ),
            ),
            R.string.security_general,
        ),
    )

    private fun SecurityPatchState.getComponentSubsection(component: Component) = Subsection(
        component.value.lowercase(),
        listOfNotNull(
            Information(
                "device_security_patch_level",
                InformationValue.StringValue(
                    getDeviceSecurityPatchLevel(component.value).toString(),
                ),
                R.string.security_device_security_patch_level,
            ),
            Information(
                "published_security_patch_level",
                InformationValue.StringArrayValue(
                    getPublishedSecurityPatchLevel(component.value).map {
                        it.toString()
                    }.toTypedArray()
                ),
                R.string.security_published_security_patch_level,
            ),
            Information(
                "available_security_patch_level",
                InformationValue.StringValue(
                    getAvailableSecurityPatchLevel(component.value).toString(),
                ),
                R.string.security_available_security_patch_level,
            ),
            if (component.supportsCve) {
                Information(
                    "patched_cves",
                    InformationValue.StringArrayValue(
                        getPatchedCves(
                            component.value,
                            getDeviceSecurityPatchLevel(component.value),
                        ).map { (severity, cves) ->
                            cves.map {
                                "$it (${severity.name})"
                            }
                        }.flatten().toTypedArray(),
                    ),
                    R.string.security_patched_cves,
                )
            } else {
                null
            },
        ),
        component.stringResId,
    )
}
