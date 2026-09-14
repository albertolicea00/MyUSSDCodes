package com.albertolicea00.myussdcodes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.albertolicea00.myussdcodes.AppViewModel
import com.albertolicea00.myussdcodes.R
import com.albertolicea00.myussdcodes.data.model.CodeGroup
import com.albertolicea00.myussdcodes.ui.components.CodeList

/** First tab: user groups + categories, "sections" of the catalog. */
@Composable
fun SectionsScreen(
    viewModel: AppViewModel,
    onOpenSection: (type: String, key: String) -> Unit
) {
    val data by viewModel.data.collectAsState()
    val categories = data.codes.map { it.category }.distinct().sorted()
    var groupToDelete by remember { mutableStateOf<CodeGroup?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.tab_sections),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (data.groups.isNotEmpty()) {
            item {
                Text(
                    text = "My groups",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(data.groups.sortedBy { it.name }, key = { "group-${it.id}" }) { group ->
                SectionRow(
                    icon = group.icon,
                    name = group.name,
                    count = data.codes.count { it.groupId == group.id },
                    onClick = { onOpenSection(TYPE_GROUP, group.id) },
                    onDelete = { groupToDelete = group }
                )
            }
        }

        item {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(categories, key = { "category-$it" }) { category ->
            SectionRow(
                icon = "🏷️",
                name = category,
                count = data.codes.count { it.category == category },
                onClick = { onOpenSection(TYPE_CATEGORY, category) }
            )
        }
    }

    groupToDelete?.let { group ->
        AlertDialog(
            onDismissRequest = { groupToDelete = null },
            title = { Text("Delete group?") },
            text = { Text("\"${group.name}\" will be removed. Its codes are kept and just lose the group.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteGroup(group.id)
                    groupToDelete = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { groupToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SectionRow(
    icon: String,
    name: String,
    count: Int,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            leadingContent = { Text(icon, style = MaterialTheme.typography.titleLarge) },
            headlineContent = { Text(name) },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Badge { Text("$count") }
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                }
            }
        )
    }
}

/** Codes inside one group or category. */
@Composable
fun SectionDetailScreen(
    viewModel: AppViewModel,
    type: String,
    key: String,
    onBack: () -> Unit,
    onEdit: (com.albertolicea00.myussdcodes.data.model.UssdCode) -> Unit
) {
    val data by viewModel.data.collectAsState()

    val title: String
    val codes = when (type) {
        TYPE_GROUP -> {
            val group = data.groups.find { it.id == key }
            title = group?.let { "${it.icon} ${it.name}" } ?: key
            data.codes.filter { it.groupId == key }
        }
        else -> {
            title = key
            data.codes.filter { it.category == key }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        }
        CodeList(
            codes = codes.sortedBy { it.name },
            onEdit = onEdit,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }
}

const val TYPE_GROUP = "group"
const val TYPE_CATEGORY = "category"
