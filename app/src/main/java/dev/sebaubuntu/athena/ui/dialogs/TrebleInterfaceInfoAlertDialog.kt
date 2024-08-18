/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.dialogs

import android.content.Context
import android.os.Bundle
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.stringRes
import dev.sebaubuntu.athena.models.vintf.HidlInterface
import dev.sebaubuntu.athena.models.vintf.TrebleInterface
import dev.sebaubuntu.athena.ui.views.ListItem
import kotlin.reflect.safeCast

class TrebleInterfaceInfoAlertDialog(
    context: Context,
    private val trebleInterface: TrebleInterface,
) : CustomAlertDialog(context, R.layout.dialog_treble_interface_info) {
    private val addressListItem by lazy { findViewById<ListItem>(R.id.addressListItem)!! }
    private val archListItem by lazy { findViewById<ListItem>(R.id.archListItem)!! }
    private val clientsProcessIdsListItem by lazy { findViewById<ListItem>(R.id.clientsProcessIdsListItem)!! }
    private val doneButton by lazy { findViewById<MaterialButton>(R.id.doneButton)!! }
    private val inDeviceCompatibilityMatrixListItem by lazy { findViewById<ListItem>(R.id.inDeviceCompatibilityMatrixListItem)!! }
    private val inDeviceManifestListItem by lazy { findViewById<ListItem>(R.id.inDeviceManifestListItem)!! }
    private val inFrameworkCompatibilityMatrixListItem by lazy { findViewById<ListItem>(R.id.inFrameworkCompatibilityMatrixListItem)!! }
    private val inFrameworkManifestListItem by lazy { findViewById<ListItem>(R.id.inFrameworkManifestListItem)!! }
    private val interfaceTypeListItem by lazy { findViewById<ListItem>(R.id.interfaceTypeListItem)!! }
    private val nameListItem by lazy { findViewById<ListItem>(R.id.nameListItem)!! }
    private val releasedListItem by lazy { findViewById<ListItem>(R.id.releasedListItem)!! }
    private val serverProcessIdListItem by lazy { findViewById<ListItem>(R.id.serverProcessIdListItem)!! }
    private val threadsListItem by lazy { findViewById<ListItem>(R.id.threadsListItem)!! }
    private val transportListItem by lazy { findViewById<ListItem>(R.id.transportListItem)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nameListItem.supportingText = trebleInterface.name
        interfaceTypeListItem.setSupportingText(trebleInterface.interfaceTypeStringResId)

        HidlInterface::class.safeCast(trebleInterface)?.let { hidlInterface ->
            transportListItem.supportingText = hidlInterface.transport.name
            transportListItem.isVisible = true

            serverProcessIdListItem.supportingText =
                hidlInterface.serverProcessId?.toString() ?: context.getString(R.string.unknown)
            serverProcessIdListItem.isVisible = true

            addressListItem.supportingText = hidlInterface.address
            addressListItem.isVisible = true

            archListItem.supportingText = hidlInterface.arch
            archListItem.isVisible = true

            threadsListItem.supportingText =
                "${
                    hidlInterface.currentThreads ?: context.getString(R.string.unknown)
                }/${
                    hidlInterface.maxThreads ?: context.getString(R.string.unknown)
                }"
            threadsListItem.isVisible = true

            releasedListItem.supportingText = context.getString(hidlInterface.released.stringRes)
            releasedListItem.isVisible = true

            inDeviceManifestListItem.supportingText =
                context.getString(hidlInterface.inDeviceManifest.stringRes)
            inDeviceManifestListItem.isVisible = true

            inDeviceCompatibilityMatrixListItem.supportingText =
                context.getString(hidlInterface.inDeviceCompatibilityMatrix.stringRes)
            inDeviceCompatibilityMatrixListItem.isVisible = true

            inFrameworkManifestListItem.supportingText =
                context.getString(hidlInterface.inFrameworkManifest.stringRes)
            inFrameworkManifestListItem.isVisible = true

            inFrameworkCompatibilityMatrixListItem.supportingText =
                context.getString(hidlInterface.inFrameworkCompatibilityMatrix.stringRes)
            inFrameworkCompatibilityMatrixListItem.isVisible = true

            clientsProcessIdsListItem.supportingText =
                hidlInterface.clientsProcessIds?.joinToString()
                    ?: context.getString(R.string.unknown)
            clientsProcessIdsListItem.isVisible = true
        }

        doneButton.setOnClickListener {
            dismiss()
        }
    }
}
