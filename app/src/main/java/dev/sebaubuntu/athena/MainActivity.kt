/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import dev.sebaubuntu.athena.models.data.Section
import dev.sebaubuntu.athena.models.data.Section.Companion.listSerializer
import dev.sebaubuntu.athena.models.data.Section.Companion.toSerializable
import dev.sebaubuntu.athena.models.data.Subsection
import dev.sebaubuntu.athena.sections.SectionEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.FileWriter

class MainActivity : AppCompatActivity() {
    // Views
    private val linearProgressIndicator by lazy { findViewById<LinearProgressIndicator>(R.id.linearProgressIndicator) }
    private val toolbar by lazy { findViewById<MaterialToolbar>(R.id.toolbar) }

    // Fragments
    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment }

    private val navController by lazy { navHostFragment.navController }

    // JSON export
    private var toExport: String? = null
    private val createDocumentContract = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) {
        it?.let { uri ->
            toExport?.let { toExport ->
                contentResolver.openFileDescriptor(uri, "wt")?.use { parcelFileDescriptor ->
                    FileWriter(parcelFileDescriptor.fileDescriptor).use { fileWriter ->
                        fileWriter.write(toExport)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Setup edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setSupportActionBar(toolbar)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.exportData -> {
            exportData()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun exportData() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.export_data)
            .setMessage(R.string.export_data_description)
            .setPositiveButton(R.string.yes) { _, _ ->
                lifecycleScope.launch {
                    linearProgressIndicator.progress = 0
                    linearProgressIndicator.isInvisible = false

                    withContext(Dispatchers.IO) {
                        val sections = SectionEnum.values().map { it.clazz }

                        withContext(Dispatchers.Main) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                linearProgressIndicator.min = 0
                            }
                            linearProgressIndicator.max = sections.size
                        }

                        val sectionToData = mutableMapOf<Section, List<Subsection>>()

                        val updateData: suspend (
                            Section, List<Subsection>?
                        ) -> Unit = { section, data ->
                            data?.let {
                                sectionToData[section] = it
                            }

                            withContext(Dispatchers.Main) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    linearProgressIndicator.setProgress(
                                        sectionToData.size, true
                                    )
                                } else {
                                    linearProgressIndicator.progress = sectionToData.size
                                }
                            }
                        }

                        sections.map {
                            async {
                                updateData(
                                    it,
                                    runCatching {
                                        it.dataFlow(this@MainActivity)
                                    }.getOrNull()?.take(1)?.single()
                                )
                            }
                        }.awaitAll()

                        withContext(Dispatchers.Main) {
                            linearProgressIndicator.progress = linearProgressIndicator.max
                            linearProgressIndicator.isInvisible = true
                        }

                        val jsonData = Json.encodeToString(
                            listSerializer<Map<Section, List<Subsection>>>(),
                            sectionToData.toSerializable()
                        )

                        toExport = jsonData
                        createDocumentContract.launch("data.json")
                    }
                }
            }
            .setNegativeButton(R.string.no) { _, _ ->
                // Do nothing
            }
            .show()
    }
}
