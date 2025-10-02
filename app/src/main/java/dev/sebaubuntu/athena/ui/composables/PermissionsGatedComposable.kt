/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.PermissionState
import dev.sebaubuntu.athena.ui.LocalPermissionsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun PermissionsGatedComposable(
    permissions: Array<String>,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    val permissionsManager = LocalPermissionsManager.current

    val permissionsWithState by permissionsManager.permissionsStateFlow(
        permissions
    ).collectAsStateWithLifecycle(null)

    PermissionsGatedComposable(
        permissionsWithState = permissionsWithState,
        paddingValues = paddingValues,
        onRequestPermissions = { permissions ->
            coroutineScope.launch(Dispatchers.IO) {
                permissionsManager.requestPermissions(permissions)
            }
        },
        content = content,
    )
}

@Composable
private fun PermissionsGatedComposable(
    permissionsWithState: Map<String, PermissionState>?,
    paddingValues: PaddingValues = PaddingValues(),
    onRequestPermissions: (Array<String>) -> Unit,
    content: @Composable () -> Unit,
) {
    permissionsWithState?.also { permissionsWithState ->
        when (permissionsWithState.all { it.value == PermissionState.GRANTED }) {
            true -> content()

            false -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    permissionsWithState.filter {
                        it.value == PermissionState.DENIED
                    }.takeIf { it.isNotEmpty() }?.also {
                        Text(
                            text = stringResource(
                                R.string.permissions_denied,
                                it.keys.joinToString(),
                            ),
                            textAlign = TextAlign.Center,
                        )
                    }

                    permissionsWithState.filter {
                        it.value == PermissionState.NOT_GRANTED
                    }.takeIf { it.isNotEmpty() }?.also {
                        Text(
                            text = stringResource(
                                R.string.permissions_not_granted,
                                it.keys.joinToString(),
                            ),
                            textAlign = TextAlign.Center,
                        )

                        Button(
                            onClick = {
                                onRequestPermissions(it.keys.toTypedArray())
                            },
                            modifier = Modifier.padding(top = 8.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.request_permissions),
                            )
                        }
                    }
                }
            }
        }
    } ?: LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues),
    )
}
