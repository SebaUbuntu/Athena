/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.treble

import android.content.Context
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Error
import dev.sebaubuntu.athena.core.models.LocalizedString
import dev.sebaubuntu.athena.core.models.Module
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.core.models.Value
import dev.sebaubuntu.athena.modules.systemproperties.utils.SystemProperties
import dev.sebaubuntu.athena.modules.treble.models.TrebleInterface
import dev.sebaubuntu.athena.modules.treble.utils.VintfUtils
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class TrebleModule : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = TrebleModule()
    }

    override val id = "treble"

    override val name = LocalizedString(R.string.section_treble_name)

    override val description = LocalizedString(R.string.section_treble_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_construction

    override val requiredPermissions = arrayOf<String>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOf(
                    Element.Card(
                        name = "general",
                        title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                        elements = listOfNotNull(
                            SystemProperties.getBoolean("ro.treble.enabled")?.let {
                                Element.Item(
                                    name = "treble_enabled",
                                    title = LocalizedString(R.string.treble_enabled),
                                    value = Value(it),
                                )
                            },
                            SystemProperties.getString("ro.vndk.version")?.let {
                                Element.Item(
                                    name = "vndk_version",
                                    title = LocalizedString(R.string.treble_vndk_version),
                                    value = Value(it),
                                )
                            },
                            Element.Item(
                                name = "interfaces",
                                title = LocalizedString(R.string.treble_interfaces),
                                navigateTo = identifier / "interfaces",
                            ),
                        ),
                    ),
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        "interfaces" -> when (identifier.path.getOrNull(1)) {
            null -> VintfUtils::getTrebleInterfaces.asFlow()
                .map { trebleInterfaces ->
                    trebleInterfaces.sortedBy(TrebleInterface::name)
                }
                .map { trebleInterfaces ->
                    val screen = Screen.ItemListScreen(
                        identifier = identifier,
                        title = LocalizedString(R.string.treble_interfaces),
                        elements = trebleInterfaces.map { trebleInterface ->
                            Element.Item(
                                name = trebleInterface.name,
                                title = LocalizedString(trebleInterface.name),
                                navigateTo = identifier / trebleInterface.name,
                                value = Value(
                                    trebleInterface.type,
                                    trebleInterfaceTypeToStringResId,
                                ),
                            )
                        },
                    )

                    Result.Success<Resource, Error>(screen)
                }

            else -> when (identifier.path.getOrNull(2)) {
                null -> {
                    val name = identifier.path[1]

                    VintfUtils::getTrebleInterfaces.asFlow()
                        .map { trebleInterfaces ->
                            trebleInterfaces.firstOrNull { it.name == name }
                        }
                        .map { trebleInterface ->
                            trebleInterface?.let { trebleInterface ->
                                val screen = trebleInterface.getScreen(
                                    identifier = identifier,
                                )

                                Result.Success<Resource, Error>(screen)
                            } ?: Result.Error(Error.NOT_FOUND)
                        }
                }

                else -> flowOf(Result.Error(Error.NOT_FOUND))
            }
        }

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }

    private fun TrebleInterface.getScreen(
        identifier: Resource.Identifier,
    ) = Screen.CardListScreen(
        identifier = identifier,
        title = LocalizedString(name),
        elements = listOf(
            Element.Card(
                name = "general",
                title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                elements = listOf(
                    Element.Item(
                        name = "name",
                        title = LocalizedString(R.string.treble_interface_name),
                        value = Value(name),
                    ),
                    Element.Item(
                        name = "type",
                        title = LocalizedString(R.string.treble_interface_type),
                        value = Value(
                            type,
                            trebleInterfaceTypeToStringResId,
                        ),
                    ),
                    *when (this) {
                        is TrebleInterface.Aidl -> listOf()
                        is TrebleInterface.Hidl -> listOfNotNull(
                            Element.Item(
                                name = "transport",
                                title = LocalizedString(R.string.treble_interface_transport),
                                value = Value(
                                    transport,
                                    hidlInterfaceTransportTypeToStringResId,
                                ),
                            ),
                            serverProcessId?.let {
                                Element.Item(
                                    name = "server_process_id",
                                    title = LocalizedString(R.string.treble_interface_server_process_id),
                                    value = Value(it),
                                )
                            },
                            address?.let {
                                Element.Item(
                                    name = "address",
                                    title = LocalizedString(R.string.treble_interface_address),
                                    value = Value(it),
                                )
                            },
                            arch?.let {
                                Element.Item(
                                    name = "arch",
                                    title = LocalizedString(R.string.treble_interface_arch),
                                    value = Value(it),
                                )
                            },
                            currentThreads?.let {
                                Element.Item(
                                    name = "current_threads",
                                    title = LocalizedString(R.string.treble_interface_current_threads),
                                    value = Value(it),
                                )
                            },
                            maxThreads?.let {
                                Element.Item(
                                    name = "max_threads",
                                    title = LocalizedString(R.string.treble_interface_max_threads),
                                    value = Value(it),
                                )
                            },
                            released?.let {
                                Element.Item(
                                    name = "released",
                                    title = LocalizedString(R.string.treble_interface_released),
                                    value = Value(it),
                                )
                            },
                            Element.Item(
                                name = "in_device_manifest",
                                title = LocalizedString(R.string.treble_interface_in_device_manifest),
                                value = Value(inDeviceManifest),
                            ),
                            Element.Item(
                                name = "in_device_compatibility_matrix",
                                title = LocalizedString(R.string.treble_interface_in_device_compatibility_matrix),
                                value = Value(inDeviceCompatibilityMatrix),
                            ),
                            Element.Item(
                                name = "in_framework_manifest",
                                title = LocalizedString(R.string.treble_interface_in_framework_manifest),
                                value = Value(inFrameworkManifest),
                            ),
                            Element.Item(
                                name = "in_framework_compatibility_matrix",
                                title = LocalizedString(R.string.treble_interface_in_framework_compatibility_matrix),
                                value = Value(inFrameworkCompatibilityMatrix),
                            ),
                            clientsProcessIds?.let {
                                Element.Item(
                                    name = "clients_process_ids",
                                    title = LocalizedString(R.string.treble_interface_clients_process_ids),
                                    value = Value(it.toTypedArray()),
                                )
                            },
                        )
                    }.toTypedArray()
                ),
            ),
        ),
    )

    companion object {
        private val trebleInterfaceTypeToStringResId = mapOf(
            TrebleInterface.Type.AIDL to R.string.treble_interface_type_aidl,
            TrebleInterface.Type.HIDL to R.string.treble_interface_type_hidl,
        )

        private val hidlInterfaceTransportTypeToStringResId = mapOf(
            TrebleInterface.Hidl.TransportType.PASSTHROUGH to R.string.treble_interface_transport_passthrough,
            TrebleInterface.Hidl.TransportType.HWBINDER to R.string.treble_interface_transport_hwbinder,
        )
    }
}
