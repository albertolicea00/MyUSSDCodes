package com.albertolicea00.myussdcodes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.albertolicea00.myussdcodes.R
import com.albertolicea00.myussdcodes.data.model.UssdCode
import com.albertolicea00.myussdcodes.data.model.VariableType
import com.albertolicea00.myussdcodes.util.UssdDialer

@Composable
fun CodeCard(
    code: UssdCode,
    onRun: () -> Unit,
    onEdit: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ElevatedCard(onClick = onRun, modifier = modifier.fillMaxWidth()) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = { Text(code.name) },
            supportingContent = {
                Column {
                    Text(
                        text = code.code,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (code.description.isNotBlank()) {
                        Text(
                            text = code.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            leadingContent = if (code.dangerous) {
                {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else null,
            trailingContent = if (code.custom && onEdit != null) {
                {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(R.string.edit_code)
                        )
                    }
                }
            } else null
        )
    }
}

/** Scrollable list of codes; tapping a code opens the pre-dial dialog. */
@Composable
fun CodeList(
    codes: List<UssdCode>,
    onEdit: (UssdCode) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    var running by remember { mutableStateOf<UssdCode?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(codes, key = { it.id }) { code ->
            CodeCard(
                code = code,
                onRun = { running = code },
                onEdit = if (code.custom) {
                    { onEdit(code) }
                } else null
            )
        }
    }

    running?.let { code ->
        RunCodeDialog(code = code, onDismiss = { running = null })
    }
}

/** Asks for the code's variables (if any), warns on dangerous codes, then opens the dialer. */
@Composable
fun RunCodeDialog(code: UssdCode, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val values = remember(code.id) { mutableStateMapOf<String, String>() }
    val ready = code.variables.all { !values[it.key].isNullOrBlank() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(code.name) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = code.code,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )
                if (code.description.isNotBlank()) {
                    Text(code.description, style = MaterialTheme.typography.bodyMedium)
                }
                code.variables.forEach { variable ->
                    OutlinedTextField(
                        value = values[variable.key].orEmpty(),
                        onValueChange = { values[variable.key] = it },
                        label = { Text(variable.label) },
                        placeholder = {
                            if (variable.hint.isNotBlank()) Text(variable.hint)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = when (variable.type) {
                                VariableType.NUMBER -> KeyboardType.Number
                                VariableType.PHONE -> KeyboardType.Phone
                                VariableType.TEXT -> KeyboardType.Text
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (code.dangerous) {
                    Text(
                        text = stringResource(R.string.dangerous_warning),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (code.notes.isNotBlank()) {
                        Text(
                            text = code.notes,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = ready,
                onClick = {
                    UssdDialer.dial(context, UssdDialer.buildDialString(code, values))
                    onDismiss()
                }
            ) { Text(stringResource(R.string.dial)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
