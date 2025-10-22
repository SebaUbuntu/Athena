/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.packages

import android.Manifest
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.FeatureInfo
import android.content.pm.PackageManager
import android.os.Build
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

class PackagesModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = PackagesModule(context)
    }

    private val packageManager = context.packageManager

    override val id = "packages"

    override val name = LocalizedString(R.string.section_packages_name)

    override val description = LocalizedString(R.string.section_packages_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_package_2

    override val requiredPermissions = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            add(Manifest.permission.QUERY_ALL_PACKAGES)
        }
    }.toTypedArray()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOfNotNull(
                    Element.Card(
                        name = "general",
                        title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                        elements = listOfNotNull(
                            Element.Item(
                                name = "apps",
                                title = LocalizedString(R.string.packages_applications),
                                navigateTo = identifier / "apps",
                                drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_apps,
                            ),
                            Element.Item(
                                name = "features",
                                title = LocalizedString(R.string.packages_features),
                                navigateTo = identifier / "features",
                                drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_extension,
                            ),
                            Element.Item(
                                name = "modules",
                                title = LocalizedString(R.string.packages_modules),
                                navigateTo = identifier / "modules",
                                drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_apk_document,
                            ),
                        ),
                    ),
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        "apps" -> when (identifier.path.getOrNull(1)) {
            null -> suspend {
                val applicationInfos = packageManager.getInstalledApplications(
                    PackageManager.GET_META_DATA
                ).sortedBy(ApplicationInfo::packageName)

                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.packages_applications),
                    elements = applicationInfos.map { applicationInfo ->
                        Element.Item(
                            name = "${applicationInfo.packageName}",
                            title = applicationInfo.getNameLocalizedString(),
                            navigateTo = identifier / "${applicationInfo.packageName}",
                            drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_apps,
                            value = Value(applicationInfo.packageName),
                        )
                    }
                )

                Result.Success<Resource, Error>(screen)
            }.asFlow()

            else -> when (identifier.path.getOrNull(2)) {
                null -> suspend {
                    val packageName = identifier.path[1]

                    val applicationInfo = runCatching {
                        packageManager.getApplicationInfo(
                            packageName,
                            PackageManager.GET_META_DATA
                                    or PackageManager.GET_SHARED_LIBRARY_FILES,
                        )
                    }.getOrNull()

                    val screen = applicationInfo?.getScreen(
                        identifier = identifier,
                    )

                    screen?.let {
                        Result.Success<Resource, Error>(it)
                    } ?: Result.Error(Error.NOT_FOUND)
                }.asFlow()

                else -> flowOf(Result.Error<Resource, Error>(Error.NOT_FOUND))
            }
        }

        "features" -> when (identifier.path.getOrNull(1)) {
            null -> suspend {
                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.packages_features),
                    elements = packageManager.systemAvailableFeatures
                        .sortedBy(FeatureInfo::name)
                        .map { featureInfo -> featureInfo.getElement() },
                )

                Result.Success<Resource, Error>(screen)
            }.asFlow()

            else -> flowOf(Result.Error<Resource, Error>(Error.NOT_FOUND))
        }

        "modules" -> when (identifier.path.getOrNull(1)) {
            null -> suspend {
                val applicationInfos = packageManager.getInstalledApplications(
                    PackageManager.GET_META_DATA or PackageManager.MATCH_APEX,
                ).sortedBy(ApplicationInfo::packageName)

                val screen = Screen.ItemListScreen(
                    identifier = identifier,
                    title = LocalizedString(R.string.packages_applications),
                    elements = applicationInfos.map { applicationInfo ->
                        Element.Item(
                            name = "${applicationInfo.packageName}",
                            title = applicationInfo.getNameLocalizedString(),
                            navigateTo = identifier / "${applicationInfo.packageName}",
                            drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_apps,
                            value = Value(applicationInfo.packageName),
                        )
                    }
                )

                Result.Success<Resource, Error>(screen)
            }.asFlow()

            else -> when (identifier.path.getOrNull(2)) {
                null -> suspend {
                    val packageName = identifier.path[1]

                    val applicationInfo = runCatching {
                        packageManager.getApplicationInfo(
                            packageName,
                            PackageManager.GET_META_DATA
                                    or PackageManager.GET_SHARED_LIBRARY_FILES
                                    or PackageManager.MATCH_APEX,
                        )
                    }.getOrNull()

                    val screen = applicationInfo?.getScreen(
                        identifier = identifier,
                    )

                    screen?.let {
                        Result.Success<Resource, Error>(it)
                    } ?: Result.Error(Error.NOT_FOUND)
                }.asFlow()

                else -> flowOf(Result.Error<Resource, Error>(Error.NOT_FOUND))
            }
        }

        else -> flowOf(Result.Error<Resource, Error>(Error.NOT_FOUND))
    }

    private fun ApplicationInfo.getScreen(
        identifier: Resource.Identifier,
    ) = Screen.CardListScreen(
        identifier = identifier,
        title = getNameLocalizedString(),
        elements = listOf(
            Element.Card(
                name = "general",
                title = LocalizedString(dev.sebaubuntu.athena.core.R.string.general),
                elements = listOfNotNull(
                    name?.let { name ->
                        Element.Item(
                            name = "name",
                            title = LocalizedString(R.string.packages_application_info_name),
                            value = Value(name),
                        )
                    },
                    Element.Item(
                        name = "package_name",
                        title = LocalizedString(R.string.packages_application_info_package_name),
                        value = Value(packageName),
                    ),
                    Element.Item(
                        name = "label_res",
                        title = LocalizedString(R.string.packages_application_info_label_res),
                        value = Value(labelRes), // TODO
                    ),
                    nonLocalizedLabel?.let { nonLocalizedLabel ->
                        Element.Item(
                            name = "non_localized_label",
                            title = LocalizedString(R.string.packages_application_info_non_localized_label),
                            value = Value(nonLocalizedLabel.toString()),
                        )
                    },
                    Element.Item(
                        name = "icon",
                        title = LocalizedString(R.string.packages_application_info_icon),
                        value = Value(icon), // TODO
                    ),
                    Element.Item(
                        name = "banner",
                        title = LocalizedString(R.string.packages_application_info_banner),
                        value = Value(banner), // TODO
                    ),
                    Element.Item(
                        name = "logo",
                        title = LocalizedString(R.string.packages_application_info_logo),
                        value = Value(logo), // TODO
                    ),
                    metaData?.let { metaData ->
                        Element.Item(
                            name = "meta_data",
                            title = LocalizedString(R.string.packages_application_info_meta_data),
                            value = Value(metaData.toString()), // TODO
                        )
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                        Element.Item(
                            name = "is_archived",
                            title = LocalizedString(R.string.packages_application_info_is_archived),
                            value = Value(isArchived),
                        )
                    } else {
                        null
                    },
                    Element.Item(
                        name = "task_affinity",
                        title = LocalizedString(R.string.packages_application_info_task_affinity),
                        value = Value(taskAffinity),
                    ),
                    permission?.let { permission ->
                        Element.Item(
                            name = "permission",
                            title = LocalizedString(R.string.packages_application_info_permission),
                            value = Value(permission),
                        )
                    },
                    Element.Item(
                        name = "process_name",
                        title = LocalizedString(R.string.packages_application_info_process_name),
                        value = Value(processName),
                    ),
                    className?.let { className ->
                        Element.Item(
                            name = "class_name",
                            title = LocalizedString(R.string.packages_application_info_class_name),
                            value = Value(className),
                        )
                    },
                    Element.Item(
                        name = "description_res",
                        title = LocalizedString(R.string.packages_application_info_description_res),
                        value = Value(descriptionRes),
                    ),
                    Element.Item(
                        name = "theme",
                        title = LocalizedString(R.string.packages_application_info_theme),
                        value = Value(theme),
                    ),
                    manageSpaceActivityName?.let { manageSpaceActivityName ->
                        Element.Item(
                            name = "manage_space_activity_name",
                            title = LocalizedString(R.string.packages_application_info_manage_space_activity_name),
                            value = Value(manageSpaceActivityName),
                        )
                    },
                    backupAgentName?.let { backupAgentName ->
                        Element.Item(
                            name = "backup_agent_name",
                            title = LocalizedString(R.string.packages_application_info_backup_agent_name),
                            value = Value(backupAgentName),
                        )
                    },
                    Element.Item(
                        name = "flags",
                        title = LocalizedString(R.string.packages_application_info_flags),
                        value = Value(flags), // TODO
                    ),
                    Element.Item(
                        name = "requires_smallest_width_dp",
                        title = LocalizedString(R.string.packages_application_info_requires_smallest_width_dp),
                        value = Value(requiresSmallestWidthDp), // TODO
                    ),
                    Element.Item(
                        name = "compatible_width_limit_dp",
                        title = LocalizedString(R.string.packages_application_info_compatible_width_limit_dp),
                        value = Value(compatibleWidthLimitDp), // TODO
                    ),
                    Element.Item(
                        name = "largest_width_limit_dp",
                        title = LocalizedString(R.string.packages_application_info_largest_width_limit_dp),
                        value = Value(largestWidthLimitDp), // TODO
                    ),
                    Element.Item(
                        name = "storage_uuid",
                        title = LocalizedString(R.string.packages_application_info_storage_uuid),
                        value = Value(storageUuid.toString()),
                    ),
                    Element.Item(
                        name = "source_dir",
                        title = LocalizedString(R.string.packages_application_info_source_dir),
                        value = Value(sourceDir),
                    ),
                    Element.Item(
                        name = "public_source_dir",
                        title = LocalizedString(R.string.packages_application_info_public_source_dir),
                        value = Value(publicSourceDir),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        splitNames?.let { splitNames ->
                            Element.Item(
                                name = "split_names",
                                title = LocalizedString(R.string.packages_application_info_split_names),
                                value = Value(splitNames),
                            )
                        }
                    } else {
                        null
                    },
                    splitSourceDirs?.let { splitSourceDirs ->
                        Element.Item(
                            name = "split_source_dirs",
                            title = LocalizedString(R.string.packages_application_info_split_source_dirs),
                            value = Value(splitSourceDirs),
                        )
                    },
                    splitPublicSourceDirs?.let { splitPublicSourceDirs ->
                        Element.Item(
                            name = "split_public_source_dirs",
                            title = LocalizedString(R.string.packages_application_info_split_public_source_dirs),
                            value = Value(splitPublicSourceDirs),
                        )
                    },
                    sharedLibraryFiles?.let { sharedLibraryFiles ->
                        Element.Item(
                            name = "shared_library_files",
                            title = LocalizedString(R.string.packages_application_info_shared_library_files),
                            value = Value(sharedLibraryFiles),
                        )
                    },
                    Element.Item(
                        name = "data_dir",
                        title = LocalizedString(R.string.packages_application_info_data_dir),
                        value = Value(dataDir),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        deviceProtectedDataDir?.let { deviceProtectedDataDir ->
                            Element.Item(
                                name = "device_protected_data_dir",
                                title = LocalizedString(R.string.packages_application_info_device_protected_data_dir),
                                value = Value(deviceProtectedDataDir),
                            )
                        }
                    } else {
                        null
                    },
                    Element.Item(
                        name = "native_library_dir",
                        title = LocalizedString(R.string.packages_application_info_native_library_dir),
                        value = Value(nativeLibraryDir),
                    ),
                    Element.Item(
                        name = "uid",
                        title = LocalizedString(R.string.packages_application_info_uid),
                        value = Value(uid),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Element.Item(
                            name = "min_sdk_version",
                            title = LocalizedString(R.string.packages_application_info_min_sdk_version),
                            value = Value(minSdkVersion),
                        )
                    } else {
                        null
                    },
                    Element.Item(
                        name = "target_sdk_version",
                        title = LocalizedString(R.string.packages_application_info_target_sdk_version),
                        value = Value(targetSdkVersion),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Element.Item(
                            name = "compile_sdk_version",
                            title = LocalizedString(R.string.packages_application_info_compile_sdk_version),
                            value = Value(compileSdkVersion),
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        compileSdkVersionCodename?.let { compileSdkVersionCodename ->
                            Element.Item(
                                name = "compile_sdk_version_codename",
                                title = LocalizedString(R.string.packages_application_info_compile_sdk_version_codename),
                                value = Value(compileSdkVersionCodename),
                            )
                        }
                    } else {
                        null
                    },
                    Element.Item(
                        name = "enabled",
                        title = LocalizedString(R.string.packages_application_info_enabled),
                        value = Value(enabled),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        appComponentFactory?.let { appComponentFactory ->
                            Element.Item(
                                name = "app_component_factory",
                                title = LocalizedString(R.string.packages_application_info_app_component_factory),
                                value = Value(appComponentFactory),
                            )
                        }
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Element.Item(
                            name = "category",
                            title = LocalizedString(R.string.packages_application_info_category),
                            value = Value(category),
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Element.Item(
                            name = "request_raw_external_storage_access",
                            title = LocalizedString(R.string.packages_application_info_request_raw_external_storage_access),
                            value = Value(requestRawExternalStorageAccess),
                        )
                    } else {
                        null
                    },
                ),
            )
        ),
    )

    private fun FeatureInfo.getElement() = Element.Item(
        name = name ?: "req_gles_version",
        title = getNameLocalizedString(),
        drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_extension,
        value = when (name) {
            null -> when (reqGlEsVersion) {
                FeatureInfo.GL_ES_VERSION_UNDEFINED -> null
                else -> Value(reqGlEsVersion)
            }

            else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                version.takeIf {
                    it != FeatureInfo.GL_ES_VERSION_UNDEFINED
                }?.let(Value.Companion::invoke)
            } else {
                null
            }
        },
    )

    companion object {
        private fun ApplicationInfo.getNameLocalizedString() = name?.let(
            LocalizedString.Companion::invoke
        ) ?: LocalizedString(dev.sebaubuntu.athena.core.R.string.unknown)

        private fun FeatureInfo.getNameLocalizedString() = name?.let(
            LocalizedString.Companion::invoke
        ) ?: LocalizedString(R.string.packages_feature_required_gles_version)
    }
}
