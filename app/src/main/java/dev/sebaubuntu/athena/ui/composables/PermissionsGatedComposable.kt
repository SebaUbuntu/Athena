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
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.models.Permission
import dev.sebaubuntu.athena.ext.getStringResId
import dev.sebaubuntu.athena.models.PermissionState
import dev.sebaubuntu.athena.ui.LocalPermissionsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun PermissionsGatedComposable(
    permissions: Set<Permission>,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    val permissionsManager = LocalPermissionsManager.current

    val permissionsWithState by when (permissions.isEmpty()) {
        true -> flowOf(mapOf())
        false -> combine(
            permissions.map { permission ->
                permissionsManager.permissionStateFlow(
                    permission
                ).mapLatest { permissionState -> permission to permissionState }
            }
        ) { it.toMap() }
    }.collectAsStateWithLifecycle(null)

    PermissionsGatedComposable(
        permissionsWithState = permissionsWithState,
        paddingValues = paddingValues,
        onRequestPermissions = { permissions ->
            coroutineScope.launch(Dispatchers.IO) {
                permissions.forEach { permission ->
                    permissionsManager.requestPermission(permission)
                }
            }
        },
        content = content,
    )
}

@Composable
private fun PermissionsGatedComposable(
    permissionsWithState: Map<Permission, PermissionState>?,
    paddingValues: PaddingValues = PaddingValues(),
    onRequestPermissions: (Set<Permission>) -> Unit,
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    permissionsWithState.filter {
                        it.value == PermissionState.DENIED
                    }.takeIf { it.isNotEmpty() }?.also {
                        Text(
                            text = stringResource(
                                R.string.permissions_denied,
                                it.map { (permission, _) ->
                                    permission.getStringResId()
                                }.joinToString(),
                            ),
                        )
                    }

                    permissionsWithState.filter {
                        it.value == PermissionState.NOT_GRANTED
                    }.takeIf { it.isNotEmpty() }?.also {
                        Text(
                            text = stringResource(
                                R.string.permissions_not_granted,
                                it.map { (permission, _) ->
                                    permission.getStringResId()
                                }.joinToString(),
                            ),
                        )

                        Button(
                            onClick = {
                                onRequestPermissions(it.keys)
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
