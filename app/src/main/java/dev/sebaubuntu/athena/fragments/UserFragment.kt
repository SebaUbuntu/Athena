/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.fragments

import android.os.Build
import android.os.Bundle
import android.os.Process
import android.os.UserManager
import android.view.View
import androidx.fragment.app.Fragment
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.getViewProperty
import dev.sebaubuntu.athena.ext.stringRes
import dev.sebaubuntu.athena.ui.views.ListItem
import java.text.SimpleDateFormat
import java.util.Date

class UserFragment : Fragment(R.layout.fragment_user) {
    // Views
    private val isAdminUserListItem by getViewProperty<ListItem>(R.id.isAdminUserListItem)
    private val isDemoUserListItem by getViewProperty<ListItem>(R.id.isDemoUserListItem)
    private val isManagedProfileListItem by getViewProperty<ListItem>(R.id.isManagedProfileListItem)
    private val isProfileListItem by getViewProperty<ListItem>(R.id.isProfileListItem)
    private val isSystemUserListItem by getViewProperty<ListItem>(R.id.isSystemUserListItem)
    private val isUserAGoatListItem by getViewProperty<ListItem>(R.id.isUserAGoatListItem)
    private val isUserForegroundListItem by getViewProperty<ListItem>(R.id.isUserForegroundListItem)
    private val isUserUnlockedListItem by getViewProperty<ListItem>(R.id.isUserUnlockedListItem)
    private val quietModeEnabledListItem by getViewProperty<ListItem>(R.id.quietModeEnabledListItem)
    private val userCreationTimeListItem by getViewProperty<ListItem>(R.id.userCreationTimeListItem)
    private val userIdListItem by getViewProperty<ListItem>(R.id.userIdListItem)
    private val userSerialNumberListItem by getViewProperty<ListItem>(R.id.userSerialNumberListItem)

    // System services
    private val userManager by lazy { requireContext().getSystemService(UserManager::class.java) }

    private val simpleDateFormatter = SimpleDateFormat.getDateTimeInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userHandle = Process.myUserHandle()

        userIdListItem.supportingText = userHandle.toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            isAdminUserListItem.setSupportingText(userManager.isAdminUser.stringRes)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            isDemoUserListItem.setSupportingText(userManager.isDemoUser.stringRes)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isManagedProfileListItem.setSupportingText(userManager.isManagedProfile.stringRes)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isProfileListItem.setSupportingText(userManager.isProfile.stringRes)
        }

        isSystemUserListItem.setSupportingText(userManager.isSystemUser.stringRes)

        isUserAGoatListItem.setSupportingText(userManager.isUserAGoat.stringRes)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isUserForegroundListItem.setSupportingText(userManager.isUserForeground.stringRes)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isUserUnlockedListItem.setSupportingText(userManager.isUserUnlocked.stringRes)
        }

        userSerialNumberListItem.supportingText =
            userManager.getSerialNumberForUser(userHandle).toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            quietModeEnabledListItem.setSupportingText(
                userManager.isQuietModeEnabled(userHandle).stringRes
            )
        }

        userCreationTimeListItem.supportingText =
            simpleDateFormatter.format(
                Date(userManager.getUserCreationTime(userHandle))
            )
    }
}
