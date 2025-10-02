/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.user

import android.content.Context
import android.os.Build
import android.os.Process
import android.os.UserManager
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
import java.util.Date

class UserModule(context: Context) : Module {
    class Factory : Module.Factory {
        override fun create(context: Context) = UserModule(context)
    }

    private val userManager = context.getSystemService(UserManager::class.java)

    private val userHandle = Process.myUserHandle()

    override val id = "user"

    override val name = LocalizedString(R.string.section_user_name)

    override val description = LocalizedString(R.string.section_user_description)

    override val drawableResId = dev.sebaubuntu.athena.core.R.drawable.ic_supervised_user_circle

    override val requiredPermissions = arrayOf<String>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.CardListScreen(
                identifier = identifier,
                title = name,
                elements = listOf(
                    Element.Card(
                        name = "current_user",
                        title = LocalizedString(R.string.current_user),
                        elements = listOfNotNull(
                            Element.Item(
                                name = "user_id",
                                title = LocalizedString(R.string.user_id),
                                value = Value(userHandle.toString()),
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                Element.Item(
                                    name = "is_admin_user",
                                    title = LocalizedString(R.string.is_admin_user),
                                    value = Value(userManager.isAdminUser),
                                )
                            } else {
                                null
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                Element.Item(
                                    name = "is_demo_user",
                                    title = LocalizedString(R.string.is_demo_user),
                                    value = Value(userManager.isDemoUser),
                                )
                            } else {
                                null
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                Element.Item(
                                    name = "is_managed_profile_user",
                                    title = LocalizedString(R.string.is_managed_profile_user),
                                    value = Value(userManager.isManagedProfile),
                                )
                            } else {
                                null
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Element.Item(
                                    name = "is_profile",
                                    title = LocalizedString(R.string.is_profile),
                                    value = Value(userManager.isProfile),
                                )
                            } else {
                                null
                            },
                            Element.Item(
                                name = "is_system_user",
                                title = LocalizedString(R.string.is_system_user),
                                value = Value(userManager.isSystemUser),
                            ),
                            Element.Item(
                                name = "is_user_a_goat",
                                title = LocalizedString(R.string.is_user_a_goat),
                                value = Value(userManager.isUserAGoat),
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                Element.Item(
                                    name = "is_user_foreground",
                                    title = LocalizedString(R.string.is_user_foreground),
                                    value = Value(userManager.isUserForeground),
                                )
                            } else {
                                null
                            },
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Element.Item(
                                    name = "is_user_unlocked",
                                    title = LocalizedString(R.string.is_user_unlocked),
                                    value = Value(userManager.isUserUnlocked),
                                )
                            } else {
                                null
                            },
                            Element.Item(
                                name = "user_serial_number",
                                title = LocalizedString(R.string.user_serial_number),
                                value = Value(userManager.getSerialNumberForUser(userHandle)),
                            ),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Element.Item(
                                    name = "quiet_mode_enabled",
                                    title = LocalizedString(R.string.quiet_mode_enabled),
                                    value = Value(userManager.isQuietModeEnabled(userHandle)),
                                )
                            } else {
                                null
                            },
                            Element.Item(
                                name = "user_creation_time",
                                title = LocalizedString(R.string.user_creation_time),
                                value = Value(
                                    Date(userManager.getUserCreationTime(userHandle))
                                ),
                            ),
                        ),
                    ),
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }
}
