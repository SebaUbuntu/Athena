/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.components

import android.content.Context
import android.os.Build
import android.os.Process
import android.os.UserManager
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
import java.util.Date

class UserComponent(context: Context) : Component {
    class Factory : Component.Factory {
        override fun create(context: Context) = UserComponent(context)
    }

    private val userManager = context.getSystemService(UserManager::class.java)

    private val userHandle = Process.myUserHandle()

    override val name = "user"

    override val title = LocalizedString(R.string.section_user_name)

    override val description = LocalizedString(R.string.section_user_description)

    override val drawableResId = R.drawable.ic_supervised_user_circle

    override val permissions = setOf<Permission>()

    override fun resolve(identifier: Resource.Identifier) = when (identifier.path.firstOrNull()) {
        null -> suspend {
            val screen = Screen.ItemListScreen(
                identifier = identifier,
                title = title,
                elements = listOfNotNull(
                    Element.Item(
                        identifier = identifier / "user_id",
                        title = LocalizedString(R.string.user_id),
                        value = Value(userHandle.toString()),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        Element.Item(
                            identifier = identifier / "is_admin_user",
                            title = LocalizedString(R.string.is_admin_user),
                            value = Value(userManager.isAdminUser),
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        Element.Item(
                            identifier = identifier / "is_demo_user",
                            title = LocalizedString(R.string.is_demo_user),
                            value = Value(userManager.isDemoUser),
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Element.Item(
                            identifier = identifier / "is_managed_profile_user",
                            title = LocalizedString(R.string.is_managed_profile_user),
                            value = Value(userManager.isManagedProfile),
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Element.Item(
                            identifier = identifier / "is_profile",
                            title = LocalizedString(R.string.is_profile),
                            value = Value(userManager.isProfile),
                        )
                    } else {
                        null
                    },
                    Element.Item(
                        identifier = identifier / "is_system_user",
                        title = LocalizedString(R.string.is_system_user),
                        value = Value(userManager.isSystemUser),
                    ),
                    Element.Item(
                        identifier = identifier / "is_user_a_goat",
                        title = LocalizedString(R.string.is_user_a_goat),
                        value = Value(userManager.isUserAGoat),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Element.Item(
                            identifier = identifier / "is_user_foreground",
                            title = LocalizedString(R.string.is_user_foreground),
                            value = Value(userManager.isUserForeground),
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Element.Item(
                            identifier = identifier / "is_user_unlocked",
                            title = LocalizedString(R.string.is_user_unlocked),
                            value = Value(userManager.isUserUnlocked),
                        )
                    } else {
                        null
                    },
                    Element.Item(
                        identifier = identifier / "user_serial_number",
                        title = LocalizedString(R.string.user_serial_number),
                        value = Value(userManager.getSerialNumberForUser(userHandle)),
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Element.Item(
                            identifier = identifier / "quiet_mode_enabled",
                            title = LocalizedString(R.string.quiet_mode_enabled),
                            value = Value(userManager.isQuietModeEnabled(userHandle)),
                        )
                    } else {
                        null
                    },
                    Element.Item(
                        identifier = identifier / "user_creation_time",
                        title = LocalizedString(R.string.user_creation_time),
                        value = Value(
                            Date(userManager.getUserCreationTime(userHandle))
                        ),
                    ),
                ),
            )

            Result.Success<Resource, Error>(screen)
        }.asFlow()

        else -> flowOf(Result.Error(Error.NOT_FOUND))
    }
}
