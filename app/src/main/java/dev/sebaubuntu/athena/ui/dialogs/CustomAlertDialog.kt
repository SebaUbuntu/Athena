/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.dialogs

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import dev.sebaubuntu.athena.R

abstract class CustomAlertDialog(
    context: Context,
    @LayoutRes private val contentLayoutId: Int,
) : AlertDialog(context, R.style.Theme_Athena_CustomDialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(contentLayoutId)
    }
}
