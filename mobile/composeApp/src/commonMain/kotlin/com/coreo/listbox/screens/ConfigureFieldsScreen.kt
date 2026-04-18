package com.coreo.listbox.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coreo.listbox.components.AddFieldDialog
import com.coreo.listbox.components.EditFieldDialog
import com.coreo.listbox.database.FieldDefinitionEntity
import com.coreo.listbox.di.ServiceLocator
import com.coreo.listbox.viewmodel.ConfigureFieldsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureFieldsScreen(
    listId: String,
    onBackClick: () -> Unit
) {
    val repository = remember { ServiceLocator.getRepository() }
    val viewModel = remember { ConfigureFieldsViewModel(repository, listId) }
    val fieldDefinitions by viewModel.fieldDefinitions.collectAsState()
    val fieldOptions by viewModel.fieldOptions.collectAsState()
    val list by viewModel.list.collectAsState()
    val listTitle = list?.title ?: ""

    var showAddFieldDialog by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf<FieldDefinitionEntity?>(null) }
    var deletingField by remember { mutableStateOf<FieldDefinitionEntity?>(null) }

    if (showAddFieldDialog) {
        AddFieldDialog(
            onDismiss = { showAddFieldDialog = false },
            onSave = { name, dataType, options ->
                viewModel.addField(name, dataType, options)
            }
        )
    }

    editingField?.let { field ->
        val existingOptions = if (field.dataType == "DROPDOWN") {
            (fieldOptions[field.id] ?: emptyList()).map { it.label }
        } else {
            emptyList()
        }
        EditFieldDialog(
            initialName = field.name,
            initialDataType = field.dataType,
            initialOptions = existingOptions,
            onDismiss = { editingField = null },
            onSave = { name, dataType, options ->
                viewModel.updateField(field.id, name, dataType, options)
            }
        )
    }

    deletingField?.let { field ->
        AlertDialog(
            onDismissRequest = { deletingField = null },
            title = { Text("Delete field?") },
            text = { Text("\"${field.name}\" and all its values will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteField(field.id)
                    deletingField = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingField = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Configure Fields",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = listTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddFieldDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add field"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(fieldDefinitions, key = { it.id }) { field ->
                FieldRow(
                    field = field,
                    onTap = { editingField = field },
                    onVisibilityToggle = { viewModel.toggleVisibility(field.id, field.visible == 1L) },
                    onDeleteClick = { deletingField = field }
                )
            }
        }
    }
}

@Composable
private fun FieldRow(
    field: FieldDefinitionEntity,
    onTap: () -> Unit,
    onVisibilityToggle: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onVisibilityToggle) {
            Icon(
                imageVector = if (field.visible == 1L) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = if (field.visible == 1L) "Hide field" else "Show field",
                tint = if (field.visible == 1L)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        TextButton(
            onClick = onTap,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = field.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = field.dataType.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete field",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
