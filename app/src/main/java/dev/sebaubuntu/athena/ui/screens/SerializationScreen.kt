/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.core.models.Result
import dev.sebaubuntu.athena.ui.LocalPermissionsManager
import dev.sebaubuntu.athena.ui.LocalSnackbarHostState
import dev.sebaubuntu.athena.viewmodels.SerializationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SerializationScreen(
    paddingValues: PaddingValues,
) {
    val coroutineScope = rememberCoroutineScope()

    val permissionsManager = LocalPermissionsManager.current

    val serializationViewModel = viewModel<SerializationViewModel> {
        SerializationViewModel(
            application = get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY)!!,
            permissionsManager = permissionsManager,
        )
    }

    val createJsonDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let(serializationViewModel::serialize) }

    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(snackbarHostState) {
        coroutineScope.launch {
            serializationViewModel.serializationStatus.collectLatest { serializationStatus ->
                when (serializationStatus) {
                    is Result.Success -> {
                        snackbarHostState.showSnackbar(
                            message = "Success",
                            withDismissAction = true,
                        )

                        serializationViewModel.acknowledgeSerializationStatus()
                    }

                    is Result.Error -> {
                        snackbarHostState.showSnackbar(
                            message = "Error",
                            withDismissAction = true,
                        )

                        serializationViewModel.acknowledgeSerializationStatus()
                    }

                    null -> Unit
                }
            }
        }
    }

    Column(
        modifier = Modifier.padding(paddingValues),
    ) {
        Button(
            onClick = {
                createJsonDocumentLauncher.launch("data.json")
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_data_object),
                    contentDescription = stringResource(R.string.export_data),
                )
                Text(stringResource(R.string.export_data))
            }
        }
    }
}
