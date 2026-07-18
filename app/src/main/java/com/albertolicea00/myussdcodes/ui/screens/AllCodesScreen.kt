package com.albertolicea00.myussdcodes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.albertolicea00.myussdcodes.AppViewModel
import com.albertolicea00.myussdcodes.R
import com.albertolicea00.myussdcodes.data.model.UssdCode
import com.albertolicea00.myussdcodes.ui.components.CodeList

/** Second tab: every code on the device behind a search box. */
@Composable
fun AllCodesScreen(
    viewModel: AppViewModel,
    onEdit: (UssdCode) -> Unit
) {
    val data by viewModel.data.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }

    val filtered = data.codes
        .filter { it.matches(query) }
        .sortedBy { it.name }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.tab_all_codes),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Outlined.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        if (filtered.isEmpty()) {
            Text(
                text = "No codes match \"$query\".",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            CodeList(
                codes = filtered,
                onEdit = onEdit,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
        }
    }
}
