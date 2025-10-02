/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.NavDestination
import dev.sebaubuntu.athena.ui.navigation.AthenaNavDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthenaApp() {
    val navigationBackStack = LocalNavigationBackStack.current

    AthenaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            IconButton(
                                onClick = {
                                    if (navigationBackStack.lastOrNull() != NavDestination.Settings) {
                                        navigationBackStack.add(NavDestination.Settings)
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 8.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_settings),
                                    contentDescription = stringResource(R.string.settings_general),
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        if (navigationBackStack.size > 1) {
                            IconButton(
                                onClick = navigationBackStack::removeLastOrNull,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_arrow_back),
                                    contentDescription = stringResource(R.string.go_back),
                                )
                            }
                        }
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = LocalSnackbarHostState.current)
            }
        ) { paddingValues ->
            AthenaNavDisplay(
                paddingValues = paddingValues,
            )
        }
    }
}
