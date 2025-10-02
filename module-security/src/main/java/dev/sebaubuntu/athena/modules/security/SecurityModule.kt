/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.security

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.security.state.SecurityPatchState
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
import okhttp3.OkHttpClient
import okhttp3.Request

class SecurityModule(private val context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = SecurityModule(context)
    }

    private val okHttpClient = OkHttpClient()

    override val id = "security"

    override val name = LocalizedString(R.string.section_security_name)

    override val description = LocalizedString(R.string.section_security_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_security

    override val requiredPermissions = arrayOf(
        Manifest.permission.INTERNET,
    )

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            // Create SecurityPatchState object
            val securityPatchState = SecurityPatchState(
                context,
                SecurityPatchState.DEFAULT_SYSTEM_MODULES,
            )

            val vulnerabilityReportLoaded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vulnerabilityReportUrl = SecurityPatchState.getVulnerabilityReportUrl()

                val request = Request.Builder()
                    .url(vulnerabilityReportUrl.toString())
                    .build()

                val jsonString = runCatching {
                    okHttpClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            response.body.string()
                        } else {
                            null
                        }
                    }
                }.onFailure {
                    Log.e(LOG_TAG, "Failed to fetch information", it)
                }.getOrNull()

                jsonString?.let { jsonString ->
                    runCatching {
                        securityPatchState.loadVulnerabilityReport(jsonString)
                    }.onFailure {
                        Log.e(
                            LOG_TAG,
                            "Failed to load the vulnerability report",
                            it,
                        )
                    }.isSuccess
                } ?: false
            } else {
                false
            }

            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOf(
                    Element.Card(
                        name = "general",
                        title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                        elements = listOfNotNull(
                            runCatching {
                                securityPatchState.isDeviceFullyUpdated()
                            }.getOrNull()?.let {
                                Element.Item(
                                    name = "is_device_fully_updated",
                                    title = LocalizedString(R.string.security_is_device_fully_updated),
                                    value = Value(it),
                                )
                            },
                        )
                    ),
                    *componentToStringResId.entries.map {
                        securityPatchState.getComponentCard(
                            component = it.key,
                            vulnerabilityReportLoaded = vulnerabilityReportLoaded,
                        )
                    }.toTypedArray()
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }

    private fun SecurityPatchState.getComponentCard(
        component: String,
        vulnerabilityReportLoaded: Boolean,
    ): Element.Card {
        val deviceSecurityPatchLevel = runCatching {
            getDeviceSecurityPatchLevel(component)
        }.getOrNull()

        return Element.Card(
            name = component,
            title = LocalizedString(componentToStringResId.getValue(component)),
            elements = listOfNotNull(
                deviceSecurityPatchLevel?.let {
                    Element.Item(
                        name = "device_security_patch_level",
                        title = LocalizedString(R.string.security_device_security_patch_level),
                        value = Value(it.toString()),
                    )
                },
                *if (vulnerabilityReportLoaded) {
                    listOfNotNull(
                        runCatching {
                            getPublishedSecurityPatchLevel(component)
                        }.getOrNull()?.let { publishedSecurityPatchLevel ->
                            Element.Item(
                                name = "published_security_patch_level",
                                title = LocalizedString(R.string.security_published_security_patch_level),
                                value = Value(
                                    publishedSecurityPatchLevel.map {
                                        it.toString()
                                    }.toTypedArray()
                                ),
                            )
                        },
                        runCatching {
                            deviceSecurityPatchLevel?.let { spl ->
                                getPatchedCves(
                                    component = component,
                                    spl = spl,
                                )
                            }
                        }.getOrNull()?.let { patchedCves ->
                            Element.Item(
                                name = "patched_cves",
                                title = LocalizedString(R.string.security_patched_cves),
                                value = Value(
                                    patchedCves.map { (severity, cves) ->
                                        cves.map {
                                            "$it (${severity.name})"
                                        }
                                    }.flatten().toTypedArray(),
                                ),
                            )
                        },
                    ).toTypedArray()
                } else {
                    arrayOf()
                },
            ),
        )
    }

    companion object {
        private val LOG_TAG = this::class.simpleName!!

        private val componentToStringResId = mapOf(
            SecurityPatchState.COMPONENT_SYSTEM to R.string.security_component_system,
            SecurityPatchState.COMPONENT_SYSTEM_MODULES to R.string.security_component_system_modules,
            //SecurityPatchState.COMPONENT_VENDOR to R.string.security_component_vendor,
            SecurityPatchState.COMPONENT_KERNEL to R.string.security_component_kernel,
        )
    }
}
