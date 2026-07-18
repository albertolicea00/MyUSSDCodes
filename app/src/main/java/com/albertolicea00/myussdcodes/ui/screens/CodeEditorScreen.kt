package com.albertolicea00.myussdcodes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.albertolicea00.myussdcodes.AppViewModel
import com.albertolicea00.myussdcodes.R
import com.albertolicea00.myussdcodes.data.model.CodeGroup
import com.albertolicea00.myussdcodes.data.model.CodeVariable
import com.albertolicea00.myussdcodes.data.model.UssdCode
import com.albertolicea00.myussdcodes.data.model.VariableType

private val PLACEHOLDER_REGEX = Regex("\\{([a-zA-Z][a-zA-Z0-9]*)\\}")
private val DIAL_STRING_REGEX = Regex("^([*#+0-9]|\\{[a-zA-Z][a-zA-Z0-9]*\\})+$")

/** Create or edit a custom code: dial string with {placeholders}, variables and group. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeEditorScreen(
    viewModel: AppViewModel,
    codeId: String?,
    onDone: () -> Unit
) {
    val data by viewModel.data.collectAsState()
    val existing = remember(codeId, data.codes) { data.codes.find { it.id == codeId } }

    var name by remember(existing) { mutableStateOf(existing?.name.orEmpty()) }
    var dialCode by remember(existing) { mutableStateOf(existing?.code.orEmpty()) }
    var description by remember(existing) { mutableStateOf(existing?.description.orEmpty()) }
    var category by remember(existing) { mutableStateOf(existing?.category ?: "Custom") }
    var dangerous by remember(existing) { mutableStateOf(existing?.dangerous ?: false) }
    var groupId by remember(existing) { mutableStateOf(existing?.groupId) }
    var newGroupName by remember { mutableStateOf("") }
    val variables = remember(existing) {
        (existing?.variables ?: emptyList()).toMutableStateList()
    }
    var error by remember { mutableStateOf<String?>(null) }

    fun syncVariablesWithCode() {
        val used = PLACEHOLDER_REGEX.findAll(dialCode).map { it.groupValues[1] }.toList()
        variables.removeAll { it.key !in used }
        used.forEach { key ->
            if (variables.none { it.key == key }) {
                variables.add(CodeVariable(key = key, label = key, type = VariableType.TEXT))
            }
        }
    }

    fun save() {
        syncVariablesWithCode()
        error = when {
            name.isBlank() -> "Name is required."
            dialCode.isBlank() -> "Code is required."
            !DIAL_STRING_REGEX.matches(dialCode) ->
                "Code may only contain *, #, +, digits and {placeholders}."
            variables.any { it.label.isBlank() } -> "Every variable needs a label."
            else -> null
        }
        if (error != null) return

        val resolvedGroupId = if (newGroupName.isNotBlank()) {
            val group = CodeGroup(
                id = "group-${System.currentTimeMillis()}",
                name = newGroupName.trim()
            )
            viewModel.upsertGroup(group)
            group.id
        } else {
            groupId
        }

        viewModel.upsertCode(
            UssdCode(
                id = existing?.id ?: "custom-${System.currentTimeMillis()}",
                name = name.trim(),
                description = description.trim(),
                code = dialCode.trim(),
                category = category.trim().ifBlank { "Custom" },
                variables = variables.toList(),
                dangerous = dangerous,
                custom = true,
                groupId = resolvedGroupId
            )
        )
        onDone()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onDone) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (existing == null) {
                    stringResource(R.string.new_code)
                } else {
                    stringResource(R.string.edit_code)
                },
                style = MaterialTheme.typography.titleLarge
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dialCode,
                onValueChange = { dialCode = it },
                label = { Text("Code") },
                placeholder = { Text("*123*{number}#") },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                supportingText = { Text("Use {placeholders} for values asked before dialing.") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Dangerous code", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "Warn before running (SIM locks, charges…)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = dangerous, onCheckedChange = { dangerous = it })
            }

            HorizontalDivider()

            // --- Group ---
            Text(
                text = "Group",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            var groupMenuExpanded by remember { mutableStateOf(false) }
            val selectedGroup = data.groups.find { it.id == groupId }
            ExposedDropdownMenuBox(
                expanded = groupMenuExpanded,
                onExpandedChange = { groupMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedGroup?.name ?: "No group",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Existing group") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupMenuExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = groupMenuExpanded,
                    onDismissRequest = { groupMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("No group") },
                        onClick = {
                            groupId = null
                            groupMenuExpanded = false
                        }
                    )
                    data.groups.sortedBy { it.name }.forEach { group ->
                        DropdownMenuItem(
                            text = { Text("${group.icon} ${group.name}") },
                            onClick = {
                                groupId = group.id
                                groupMenuExpanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = newGroupName,
                onValueChange = { newGroupName = it },
                label = { Text("…or create a new group") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // --- Variables ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Variables",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedButton(onClick = { syncVariablesWithCode() }) {
                    Text("Detect from code")
                }
            }
            variables.forEachIndexed { index, variable ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "{${variable.key}}",
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { variables.removeAt(index) }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = variable.label,
                        onValueChange = { variables[index] = variable.copy(label = it) },
                        label = { Text("Label") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VariableType.entries.forEach { type ->
                            FilterChip(
                                selected = variable.type == type,
                                onClick = { variables[index] = variable.copy(type = type) },
                                label = { Text(type.name.lowercase()) }
                            )
                        }
                    }
                }
            }
            if (variables.isEmpty()) {
                Text(
                    text = "No variables. Add {placeholders} to the code and tap \"Detect from code\".",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(onClick = { save() }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save))
            }
            if (existing?.custom == true) {
                OutlinedButton(
                    onClick = {
                        viewModel.deleteCode(existing.id)
                        onDone()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
