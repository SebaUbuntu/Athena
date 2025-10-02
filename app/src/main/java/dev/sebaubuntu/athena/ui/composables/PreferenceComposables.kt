/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.athena.repositories.PreferencesRepository

/**
 * Preferences category header.
 */
@Composable
fun PreferencesCategoryText(
    @StringRes titleStringResId: Int,
) {
    Text(
        text = stringResource(titleStringResId),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun PreferenceCategoryCard(
    @StringRes titleStringResId: Int,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit),
) {
    Card(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Text(
            text = stringResource(titleStringResId),
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )

        Column(
            content = content,
        )
    }
}

@Composable
fun PreferenceListItem(
    @StringRes titleStringResId: Int,
    @StringRes descriptionStringResId: Int? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    BasePreferenceListItem(
        modifier = Modifier.clickable(
            enabled = enabled,
            role = Role.Button,
            onClick = onClick,
        ),
        enabled = enabled,
        headlineContent = {
            Text(
                text = stringResource(titleStringResId),
            )
        },
        supportingContent = descriptionStringResId?.let { descriptionStringResId ->
            {
                Text(
                    text = stringResource(descriptionStringResId),
                )
            }
        },
    )
}

@Composable
fun PreferenceListItem(
    @StringRes titleStringResId: Int,
    @StringRes descriptionStringResId: Int? = null,
) = PreferenceListItem(
    titleStringResId = titleStringResId,
    descriptionStringResId = descriptionStringResId,
    enabled = false,
    onClick = {},
)

@Composable
fun SwitchPreferenceListItem(
    preferenceHolder: PreferencesRepository.PreferenceHolder<Boolean>,
    onPreferenceChange: (PreferencesRepository.PreferenceHolder<Boolean>, Boolean) -> Unit,
    enabled: Boolean = true,
    @StringRes titleStringResId: Int,
    @StringRes descriptionStringResId: Int,
) {
    PreferenceHolderComposable(
        preferenceHolder = preferenceHolder,
        initialValue = false,
        onValueChange = onPreferenceChange,
    ) { value, onValueChange ->
        BasePreferenceListItem(
            modifier = Modifier.toggleable(
                value = value,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onValueChange,
            ),
            enabled = enabled,
            headlineContent = {
                Text(
                    text = stringResource(titleStringResId),
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(descriptionStringResId),
                )
            },
            trailingContent = {
                Switch(
                    checked = value,
                    onCheckedChange = null,
                    enabled = enabled,
                )
            }
        )
    }
}

/**
 * Enum preference list item.
 */
@Composable
inline fun <reified T : Enum<T>> EnumPreferenceListItem(
    preferenceHolder: PreferencesRepository.PreferenceHolder<T>,
    noinline onPreferenceChange: (PreferencesRepository.PreferenceHolder<T>, T) -> Unit,
    @StringRes titleStringResId: Int,
    noinline valueToDescriptionStringResId: (T) -> Int,
) = EnumPreferenceListItem(
    preferenceHolder = preferenceHolder,
    enumValues = enumValues<T>(),
    onPreferenceChange = onPreferenceChange,
    titleStringResId = titleStringResId,
    valueToDescriptionStringResId = valueToDescriptionStringResId,
)

@Composable
fun <T> EnumPreferenceListItem(
    preferenceHolder: PreferencesRepository.PreferenceHolder<T>,
    onPreferenceChange: (PreferencesRepository.PreferenceHolder<T>, T) -> Unit,
    enabled: Boolean = true,
    enumValues: Array<T>,
    @StringRes titleStringResId: Int,
    valueToDescriptionStringResId: (T) -> Int,
) {
    val value by preferenceHolder.collectAsStateWithLifecycle(enumValues.first())

    var dialogOpened by remember { mutableStateOf(false) }

    if (dialogOpened) {
        EnumPreferenceAlertDialog(
            value = value,
            enumValues = enumValues,
            onResult = {
                it?.let {
                    onPreferenceChange(preferenceHolder, it)
                }

                dialogOpened = false
            },
            titleStringResId = titleStringResId,
            valueToDescriptionStringResId = valueToDescriptionStringResId,
        )
    }

    BasePreferenceListItem(
        modifier = Modifier
            .clickable {
                dialogOpened = true
            },
        enabled = enabled,
        headlineContent = {
            Text(
                text = stringResource(titleStringResId),
            )
        },
        supportingContent = {
            Text(
                text = stringResource(valueToDescriptionStringResId(value)),
            )
        },
    )
}

@Composable
private fun <T> EnumPreferenceAlertDialog(
    value: T,
    enumValues: Array<T>,
    onResult: (T?) -> Unit,
    @StringRes titleStringResId: Int,
    valueToDescriptionStringResId: (T) -> Int,
) {
    var selectedValue by remember { mutableStateOf(value) }

    AlertDialog(
        onDismissRequest = {
            onResult(null)
        },
        confirmButton = {
            Button(
                onClick = {
                    onResult(selectedValue)
                }
            ) {
                Text(
                    text = stringResource(android.R.string.ok),
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onResult(null)
                }
            ) {
                Text(
                    text = stringResource(android.R.string.cancel),
                )
            }
        },
        title = {
            Text(
                text = stringResource(titleStringResId),
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
            ) {
                enumValues.forEach { enumValue ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .selectable(
                                selected = (enumValue == selectedValue),
                                onClick = { selectedValue = enumValue },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = enumValue == selectedValue,
                            onClick = null,
                        )
                        Text(
                            text = stringResource(valueToDescriptionStringResId(enumValue)),
                            modifier = Modifier.padding(start = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        },
    )
}

@Composable
fun InformationListItem(
    @StringRes titleStringResId: Int,
    description: String,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(titleStringResId),
            )
        },
        modifier = modifier,
        supportingContent = {
            Text(
                text = description,
            )
        }
    )
}

/**
 * Base preference list item.
 *
 * @param enabled Whether the preference is enabled
 * @param modifier Modifier to apply
 * @param headlineContent Headline content
 * @param supportingContent Supporting content
 * @param leadingContent Leading content
 * @param trailingContent Trailing content
 * @param colors Colors to use
 *
 * @see ListItem
 */
@Composable
private fun BasePreferenceListItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    headlineContent: @Composable () -> Unit = {},
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(
        containerColor = Color.Transparent,
    ),
) {
    ListItem(
        headlineContent = headlineContent,
        modifier = modifier,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            containerColor = colors.containerColor,
            headlineColor = when (enabled) {
                true -> colors.headlineColor
                false -> colors.disabledHeadlineColor
            },
            leadingIconColor = when (enabled) {
                true -> colors.leadingIconColor
                false -> colors.disabledLeadingIconColor
            },
            overlineColor = when (enabled) {
                true -> colors.overlineColor
                false -> colors.disabledHeadlineColor
            },
            supportingColor = when (enabled) {
                true -> colors.supportingTextColor
                false -> colors.disabledHeadlineColor
            },
            trailingIconColor = when (enabled) {
                true -> colors.trailingIconColor
                false -> colors.disabledTrailingIconColor
            },
            disabledHeadlineColor = colors.disabledHeadlineColor,
            disabledLeadingIconColor = colors.disabledLeadingIconColor,
            disabledTrailingIconColor = colors.disabledTrailingIconColor,
        )
    )
}

/**
 * Preference holder composable.
 *
 * @param preferenceHolder Preference holder
 * @param initialValue Initial value
 * @param onValueChange Callback to invoke when the value changes in UI
 * @param content The content to display
 */
@Composable
private fun <T> PreferenceHolderComposable(
    preferenceHolder: PreferencesRepository.PreferenceHolder<T>,
    initialValue: T,
    onValueChange: (PreferencesRepository.PreferenceHolder<T>, T) -> Unit,
    content: @Composable (value: T, onValueChange: (T) -> Unit) -> Unit,
) {
    val value by preferenceHolder.collectAsStateWithLifecycle(initialValue)

    content(value) {
        onValueChange(preferenceHolder, it)
    }
}
