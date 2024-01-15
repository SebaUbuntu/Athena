/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.dialogs

import android.content.Context
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.stringRes
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.vintf.HIDLInterface

class TrebleInterfaceInfoAlertDialog(
    private val context: Context,
    private val trebleInterface: HIDLInterface,
) : CustomAlertDialog(context) {
    private val addressListItem by lazy { findViewById<ListItem>(R.id.addressListItem)!! }
    private val archListItem by lazy { findViewById<ListItem>(R.id.archListItem)!! }
    private val clientsProcessIdsListItem by lazy { findViewById<ListItem>(R.id.clientsProcessIdsListItem)!! }
    private val doneButton by lazy { findViewById<MaterialButton>(R.id.doneButton)!! }
    private val inDeviceCompatibilityMatrixListItem by lazy { findViewById<ListItem>(R.id.inDeviceCompatibilityMatrixListItem)!! }
    private val inDeviceManifestListItem by lazy { findViewById<ListItem>(R.id.inDeviceManifestListItem)!! }
    private val inFrameworkCompatibilityMatrixListItem by lazy { findViewById<ListItem>(R.id.inFrameworkCompatibilityMatrixListItem)!! }
    private val inFrameworkManifestListItem by lazy { findViewById<ListItem>(R.id.inFrameworkManifestListItem)!! }
    private val nameListItem by lazy { findViewById<ListItem>(R.id.nameListItem)!! }
    private val releasedListItem by lazy { findViewById<ListItem>(R.id.releasedListItem)!! }
    private val serverProcessIdListItem by lazy { findViewById<ListItem>(R.id.serverProcessIdListItem)!! }
    private val threadsListItem by lazy { findViewById<ListItem>(R.id.threadsListItem)!! }
    private val transportListItem by lazy { findViewById<ListItem>(R.id.transportListItem)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_treble_interface_info)

        nameListItem.supportingText = trebleInterface.name
        transportListItem.supportingText = trebleInterface.transport.name
        serverProcessIdListItem.supportingText =
            trebleInterface.serverProcessId?.toString() ?: context.getString(R.string.unknown)
        addressListItem.supportingText = trebleInterface.address
        archListItem.supportingText = trebleInterface.arch
        threadsListItem.supportingText =
            "${
                trebleInterface.currentThreads ?: context.getString(R.string.unknown)
            }/${
                trebleInterface.maxThreads ?: context.getString(R.string.unknown)
            }"
        releasedListItem.supportingText = context.getString(trebleInterface.released.stringRes)
        inDeviceManifestListItem.supportingText =
            context.getString(trebleInterface.inDeviceManifest.stringRes)
        inDeviceCompatibilityMatrixListItem.supportingText =
            context.getString(trebleInterface.inDeviceCompatibilityMatrix.stringRes)
        inFrameworkManifestListItem.supportingText =
            context.getString(trebleInterface.inFrameworkManifest.stringRes)
        inFrameworkCompatibilityMatrixListItem.supportingText =
            context.getString(trebleInterface.inFrameworkCompatibilityMatrix.stringRes)
        clientsProcessIdsListItem.supportingText =
            trebleInterface.clientsProcessIds?.joinToString() ?: context.getString(R.string.unknown)

        doneButton.setOnClickListener {
            dismiss()
        }
    }
}
