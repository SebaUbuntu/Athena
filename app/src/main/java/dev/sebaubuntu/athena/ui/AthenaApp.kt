/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.models.Resource
import dev.sebaubuntu.athena.ui.navigation.AthenaNavDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthenaApp() {
    val backStack = remember { mutableStateListOf(Resource.Identifier.ROOT) }

    AthenaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = backStack.lastOrNull()?.toUri()?.toString() ?: stringResource(
                                R.string.app_name
                            )
                        )
                    },
                    navigationIcon = {
                        if (backStack.size > 1) {
                            IconButton(
                                onClick = backStack::removeLastOrNull,
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
        ) { paddingValues ->
            AthenaNavDisplay(
                backStack = backStack,
                paddingValues = paddingValues,
            )
        }
    }
}
