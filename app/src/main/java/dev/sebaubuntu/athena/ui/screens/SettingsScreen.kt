/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.screens

import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.models.Theme
import dev.sebaubuntu.athena.ui.LocalPermissionsManager
import dev.sebaubuntu.athena.ui.LocalSnackbarHostState
import dev.sebaubuntu.athena.ui.composables.EnumPreferenceListItem
import dev.sebaubuntu.athena.ui.composables.PreferenceCategoryCard
import dev.sebaubuntu.athena.ui.composables.PreferenceListItem
import dev.sebaubuntu.athena.ui.composables.SwitchPreferenceListItem
import dev.sebaubuntu.athena.viewmodels.SettingsViewModel

/**
 * App settings screen.
 */
@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
) {
    val permissionsManager = LocalPermissionsManager.current

    val settingsViewModel = viewModel {
        SettingsViewModel(
            application = get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY)!!,
            permissionsManager = permissionsManager,
        )
    }

    val supportsDynamicColors = remember {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues,
    ) {
        // General
        item {
            PreferenceCategoryCard(
                titleStringResId = R.string.settings_general,
            ) {
                EnumPreferenceListItem(
                    preferenceHolder = settingsViewModel.theme,
                    onPreferenceChange = settingsViewModel::setPreferenceValue,
                    titleStringResId = R.string.theme,
                    valueToDescriptionStringResId = {
                        when (it) {
                            Theme.LIGHT -> R.string.theme_light
                            Theme.DARK -> R.string.theme_dark
                            Theme.SYSTEM -> R.string.theme_system
                        }
                    }
                )

                if (supportsDynamicColors) {
                    SwitchPreferenceListItem(
                        preferenceHolder = settingsViewModel.dynamicColors,
                        onPreferenceChange = settingsViewModel::setPreferenceValue,
                        titleStringResId = R.string.dynamic_colors,
                        descriptionStringResId = R.string.dynamic_colors_description,
                    )
                }
            }
        }

        // Export data
        item {
            ExportDataCard(
                settingsViewModel = settingsViewModel,
            )
        }

        // About
        item {
            AboutCard()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportDataCard(
    settingsViewModel: SettingsViewModel,
) {
    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHostState.current

    val exportDataStatus by settingsViewModel.exportDataStatus.collectAsStateWithLifecycle(null)

    val createJsonDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let(settingsViewModel::exportData) }

    PreferenceCategoryCard(
        titleStringResId = R.string.export_data,
    ) {
        PreferenceListItem(
            titleStringResId = R.string.export_data,
            descriptionStringResId = R.string.export_data_description,
        ) {
            createJsonDocumentLauncher.launch("data.json")
        }
    }

    if (exportDataStatus == SettingsViewModel.ExportDataStatus.Processing) {
        BasicAlertDialog(
            onDismissRequest = {},
        ) {
            Surface(
                modifier = Modifier.wrapContentSize(),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                Row(
                    modifier = Modifier.padding(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 16.dp),
                    )

                    Text(
                        text = stringResource(R.string.export_data_in_progress),
                    )
                }
            }
        }
    }

    LaunchedEffect(exportDataStatus) {
        when (val exportDataStatus = exportDataStatus) {
            is SettingsViewModel.ExportDataStatus.Processing -> {
                // Do nothing
            }

            is SettingsViewModel.ExportDataStatus.PermissionsNotGranted -> {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.export_data_missing_permissions),
                    withDismissAction = true,
                )
            }

            is SettingsViewModel.ExportDataStatus.Done -> when (exportDataStatus.result) {
                is Result.Success -> {
                    when (
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.export_data_success),
                            actionLabel = context.getString(R.string.export_data_open_file),
                            withDismissAction = true,
                        )
                    ) {
                        SnackbarResult.ActionPerformed -> {
                            context.startActivity(
                                Intent.createChooser(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        exportDataStatus.result.data,
                                    ),
                                    context.getString(
                                        R.string.export_data_open_file,
                                    ),
                                )
                            )
                        }

                        SnackbarResult.Dismissed -> Unit
                    }
                }

                is Result.Error -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(
                            R.string.export_data_error,
                            exportDataStatus.result.error.name,
                        ),
                        withDismissAction = true,
                    )
                }
            }

            null -> Unit
        }
    }
}

@Composable
private fun AboutCard() {
    PreferenceCategoryCard(
        titleStringResId = R.string.about,
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.about_developer),
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(R.string.developer_name),
                )
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AboutLinkIconButton(
                        nameStringResId = R.string.github,
                        iconDrawableResId = R.drawable.ic_github,
                        linkStringResId = R.string.about_developer_github_link,
                    )
                    AboutLinkIconButton(
                        nameStringResId = R.string.twitter,
                        iconDrawableResId = R.drawable.ic_twitter,
                        linkStringResId = R.string.about_developer_twitter_link,
                    )
                    AboutLinkIconButton(
                        nameStringResId = R.string.mastodon,
                        iconDrawableResId = R.drawable.ic_mastodon,
                        linkStringResId = R.string.about_developer_mastodon_link,
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
        )

        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.about_application),
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(R.string.app_name),
                )
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AboutLinkIconButton(
                        nameStringResId = R.string.about_application_website,
                        iconDrawableResId = R.drawable.ic_globe,
                        linkStringResId = R.string.about_application_website_link,
                    )
                    AboutLinkIconButton(
                        nameStringResId = R.string.about_application_repository,
                        iconDrawableResId = R.drawable.ic_github,
                        linkStringResId = R.string.about_application_repository_link,
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun AboutLinkIconButton(
    @StringRes nameStringResId: Int,
    @DrawableRes iconDrawableResId: Int,
    @StringRes linkStringResId: Int,
) {
    val context = LocalContext.current

    fun openLink(@StringRes link: Int) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                context.getString(link).toUri(),
            )
        )
    }

    IconButton(
        onClick = { openLink(linkStringResId) },
    ) {
        Icon(
            painter = painterResource(iconDrawableResId),
            contentDescription = stringResource(nameStringResId),
        )
    }
}
