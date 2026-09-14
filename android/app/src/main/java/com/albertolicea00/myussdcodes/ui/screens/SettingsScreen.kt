package com.albertolicea00.myussdcodes.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.albertolicea00.myussdcodes.AppViewModel
import com.albertolicea00.myussdcodes.R

private const val CATALOG_URL =
    "https://raw.githubusercontent.com/albertolicea00/MyUSSDCodes/main/data/codes/gsm-standard.json"
private const val REPO_CATALOG = "https://github.com/albertolicea00/MyUSSDCodes/tree/main/data"
private const val REPO_ANDROID = "https://github.com/albertolicea00/MyUSSDCodes/tree/main/android"
private const val REPO_IOS = "https://github.com/albertolicea00/MyUSSDCodes/tree/main/ios"

/** Third tab: import, data management and app info. */
@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val data by viewModel.data.collectAsState()

    var importUrl by rememberSaveable { mutableStateOf(CATALOG_URL) }
    var showPasteDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    fun open(url: String) =
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.tab_settings),
            style = MaterialTheme.typography.headlineMedium
        )

        // --- Import ---
        Text(
            text = "Import collections",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        OutlinedTextField(
            value = importUrl,
            onValueChange = { importUrl = it },
            label = { Text("Collection URL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                viewModel.importFromUrl(importUrl.trim()) { result ->
                    result
                        .onSuccess { toast("Imported $it codes") }
                        .onFailure { toast("Import failed: ${it.message}") }
                }
            }) { Text("Import from URL") }
            OutlinedButton(onClick = { showPasteDialog = true }) { Text("Paste JSON") }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // --- Data ---
        Text(
            text = "Data",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "${data.codes.size} codes · ${data.groups.size} groups · " +
                "${data.importedCollections.size} imported collection(s)",
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedButton(onClick = { showResetDialog = true }) {
            Text("Reset to bundled catalog")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // --- About ---
        Text(
            text = "About",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        ElevatedCard {
            Column {
                AboutRow("Code catalog", REPO_CATALOG) { open(REPO_CATALOG) }
                AboutRow("Android app source", REPO_ANDROID) { open(REPO_ANDROID) }
                AboutRow("iOS app source", REPO_IOS) { open(REPO_IOS) }
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("License") },
                    supportingContent = { Text("MIT © 2026 Alberto Licea") }
                )
            }
        }
        Text(
            text = "USSD codes are executed by your carrier. Codes vary by country, carrier " +
                "and plan; some may be paid services. Double-check a code before running it.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showPasteDialog) {
        var pasted by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPasteDialog = false },
            title = { Text("Paste collection JSON") },
            text = {
                OutlinedTextField(
                    value = pasted,
                    onValueChange = { pasted = it },
                    placeholder = { Text("{ \"id\": \"...\", \"codes\": [...] }") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.importCollection(pasted)
                        .onSuccess { toast("Imported $it codes") }
                        .onFailure { toast("Import failed: ${it.message}") }
                    showPasteDialog = false
                }) { Text("Import") }
            },
            dismissButton = {
                TextButton(onClick = { showPasteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset catalog?") },
            text = {
                Text(
                    "All custom codes, groups and imported collections will be deleted " +
                        "and the bundled GSM catalog restored."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetToSeed()
                    showResetDialog = false
                }) { Text("Reset") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun AboutRow(title: String, url: String, onClick: () -> Unit) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = { Text(title) },
        supportingContent = { Text(url, style = MaterialTheme.typography.bodySmall) },
        trailingContent = {
            Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null)
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}
