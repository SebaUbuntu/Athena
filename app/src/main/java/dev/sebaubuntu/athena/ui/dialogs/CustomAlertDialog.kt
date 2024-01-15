/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.dialogs

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ui.views.ListItem

abstract class CustomAlertDialog(
    context: Context,
    @LayoutRes private val contentLayoutId: Int,
) : AlertDialog(context, R.style.Theme_Athena_CustomDialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(contentLayoutId)
    }

    protected fun ListItem.setSupportingTextOrHide(data: String?) {
        data?.takeIf { it.isNotEmpty() }?.also {
            supportingText = it
        }.also {
            isVisible = it != null
        }
    }

    protected fun ListItem.setSupportingTextOrHide(data: List<String>?) = setSupportingTextOrHide(
        data?.takeIf { it.isNotEmpty() }?.joinToString()
    )

    protected fun ListItem.setSupportingTextOrHide(data: IntArray?) = setSupportingTextOrHide(
        data?.map { it.toString() }
    )
}
