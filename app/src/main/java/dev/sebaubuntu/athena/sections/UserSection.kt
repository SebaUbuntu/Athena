/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import android.content.Context
import android.os.Build
import android.os.Process
import android.os.UserManager
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Information
import dev.sebaubuntu.athena.models.data.InformationValue
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Subsection
import kotlinx.coroutines.flow.asFlow
import java.util.Date

object UserSection : Section(
    "user",
    R.string.section_user_name,
    R.string.section_user_description,
    R.drawable.ic_supervised_user_circle,
) {
    override fun dataFlow(context: Context) = {
        val userManager = context.getSystemService(UserManager::class.java)

        val userHandle = Process.myUserHandle()

        listOf(
            Subsection(
                "current_user",
                listOfNotNull(
                    Information(
                        "user_id",
                        InformationValue.StringValue(userHandle.toString()),
                        R.string.user_id,
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        Information(
                            "is_admin_user",
                            InformationValue.BooleanValue(userManager.isAdminUser),
                            R.string.is_admin_user,
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        Information(
                            "is_demo_user",
                            InformationValue.BooleanValue(userManager.isDemoUser),
                            R.string.is_demo_user,
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Information(
                            "is_managed_profile_user",
                            InformationValue.BooleanValue(userManager.isManagedProfile),
                            R.string.is_managed_profile_user,
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Information(
                            "is_profile",
                            InformationValue.BooleanValue(userManager.isProfile),
                            R.string.is_profile,
                        )
                    } else {
                        null
                    },
                    Information(
                        "is_system_user",
                        InformationValue.BooleanValue(userManager.isSystemUser),
                        R.string.is_system_user,
                    ),
                    Information(
                        "is_user_a_goat",
                        InformationValue.BooleanValue(userManager.isUserAGoat),
                        R.string.is_user_a_goat,
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Information(
                            "is_user_foreground",
                            InformationValue.BooleanValue(userManager.isUserForeground),
                            R.string.is_user_foreground,
                        )
                    } else {
                        null
                    },
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Information(
                            "is_user_unlocked",
                            InformationValue.BooleanValue(userManager.isUserUnlocked),
                            R.string.is_user_unlocked,
                        )
                    } else {
                        null
                    },
                    Information(
                        "user_serial_number",
                        InformationValue.LongValue(userManager.getSerialNumberForUser(userHandle)),
                        R.string.user_serial_number,
                    ),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Information(
                            "quiet_mode_enabled",
                            InformationValue.BooleanValue(
                                userManager.isQuietModeEnabled(userHandle)
                            ),
                            R.string.quiet_mode_enabled,
                        )
                    } else {
                        null
                    },
                    Information(
                        "user_creation_time",
                        InformationValue.DateValue(
                            Date(userManager.getUserCreationTime(userHandle))
                        ),
                        R.string.user_creation_time,
                    ),
                ),
                R.string.current_user,
            )
        )
    }.asFlow()
}
