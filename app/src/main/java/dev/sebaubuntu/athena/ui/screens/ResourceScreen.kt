/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.models.Element
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.core.models.Screen
import dev.sebaubuntu.athena.ext.getDisplayValue
import dev.sebaubuntu.athena.ext.getString
import dev.sebaubuntu.athena.ui.composables.FlowResultComposable
import dev.sebaubuntu.athena.ui.composables.PermissionsGatedComposable
import dev.sebaubuntu.athena.viewmodels.ResourceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceScreen(
    resourceIdentifier: Resource.Identifier,
    paddingValues: PaddingValues,
    onNavigateTo: (Resource.Identifier) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = viewModel<ResourceViewModel>(
        key = resourceIdentifier.toUri().toString(),
    ) {
        ResourceViewModel(
            application = get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY)!!,
            resourceIdentifier = resourceIdentifier,
        )
    }

    val requiredPermissions by viewModel.requiredPermissions.collectAsStateWithLifecycle()

    FlowResultComposable(
        flowResult = requiredPermissions,
        paddingValues = paddingValues,
    ) { requiredPermissions ->
        PermissionsGatedComposable(
            permissions = requiredPermissions,
            paddingValues = paddingValues,
        ) {
            val resource by viewModel.resource.collectAsStateWithLifecycle()

            FlowResultComposable(
                flowResult = resource,
                paddingValues = paddingValues,
            ) { resource ->
                when (resource) {
                    is Screen -> when (resource) {
                        is Screen.CardListScreen -> CardListScreenLazyColumn(
                            screen = resource,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = paddingValues,
                            onNavigateTo = onNavigateTo,
                        )

                        is Screen.DialogScreen -> DialogScreenBasicAlertDialog(
                            screen = resource,
                            modifier = Modifier.padding(vertical = 32.dp),
                            onBack = onBack,
                            onNavigateTo = onNavigateTo,
                        )

                        is Screen.ItemListScreen -> ItemListScreenLazyColumn(
                            screen = resource,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = paddingValues,
                            onNavigateTo = onNavigateTo,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardElementCard(
    element: Element.Card,
    modifier: Modifier = Modifier,
    onNavigateTo: (Resource.Identifier) -> Unit,
) {
    Card(
        modifier = modifier,
    ) {
        Text(
            text = element.title.getString(),
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )

        element.elements.forEach { element ->
            ItemElementListItem(
                element = element,
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                ),
                onNavigateTo = onNavigateTo,
            )
        }
    }
}

@Composable
private fun ItemElementListItem(
    element: Element.Item,
    modifier: Modifier = Modifier,
    colors: ListItemColors = ListItemDefaults.colors(),
    onNavigateTo: (Resource.Identifier) -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                text = element.title.getString(),
            )
        },
        modifier = modifier.clickable(
            enabled = element.navigateTo != null,
            role = Role.Button,
            onClick = {
                element.navigateTo?.let(onNavigateTo)
            }
        ),
        supportingContent = element.value?.let { value ->
            {
                Text(
                    text = value.getDisplayValue(),
                )
            }
        },
        leadingContent = element.drawableResId?.let { drawableResId ->
            {
                Icon(
                    painter = painterResource(drawableResId),
                    contentDescription = element.title.getString(),
                )
            }
        },
        trailingContent = element.navigateTo?.let {
            {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = element.title.getString(),
                )
            }
        },
        colors = colors,
    )
}

@Composable
private fun CardListScreenLazyColumn(
    screen: Screen.CardListScreen,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onNavigateTo: (Resource.Identifier) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        items(screen.elements) { item ->
            CardElementCard(
                element = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                onNavigateTo = onNavigateTo,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogScreenBasicAlertDialog(
    screen: Screen.DialogScreen,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onNavigateTo: (Resource.Identifier) -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onBack,
        modifier = modifier,
        properties = DialogProperties(
            windowTitle = screen.title.getString(),
        ),
    ) {
        Surface(
            modifier = Modifier,//.wrapContentSize(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column {
                Text(
                    text = screen.title.getString(),
                    modifier = Modifier.padding(
                        16.dp,
                        24.dp,
                    ),
                    style = MaterialTheme.typography.titleLarge,
                )

                LazyColumn {
                    items(screen.elements) { element ->
                        ItemElementListItem(
                            element = element,
                            onNavigateTo = onNavigateTo,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemListScreenLazyColumn(
    screen: Screen.ItemListScreen,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onNavigateTo: (Resource.Identifier) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        items(screen.elements) { element ->
            ItemElementListItem(
                element = element,
                onNavigateTo = onNavigateTo,
            )
        }
    }
}
