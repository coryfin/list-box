package com.coreo.listbox.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coreo.listbox.database.FieldDefinitionEntity
import com.coreo.listbox.database.ListEntity

/**
 * Dialog for moving selected items to a different list.
 *
 * The caller is responsible for supplying [destinationFieldDefinitions] whenever
 * [selectedDestinationListId] changes (via the [onDestinationSelected] callback).
 * This allows auto-match detection (fields whose label already exists in the destination).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveItemsDialog(
    itemCount: Int,
    availableLists: List<ListEntity>,
    sourceFieldDefinitions: List<FieldDefinitionEntity>,
    selectedDestinationListId: String?,
    destinationFieldDefinitions: List<FieldDefinitionEntity>,
    onDestinationSelected: (String?) -> Unit,
    onDismiss: () -> Unit,
    onMove: (destinationListId: String, sourceFieldDefIdsToCreate: List<String>) -> Unit
) {
    val selectedList = availableLists.find { it.id == selectedDestinationListId }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedFieldIds by remember(selectedDestinationListId) { mutableStateOf<Set<String>>(emptySet()) }

    val destFieldLabels = remember(destinationFieldDefinitions) {
        destinationFieldDefinitions.map { it.name }.toSet()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (itemCount == 1) "Move 1 item" else "Move $itemCount items")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Destination list",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                if (availableLists.isEmpty()) {
                    Text(
                        text = "No other lists available.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = selectedList?.title ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Select a list") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            availableLists.forEach { list ->
                                DropdownMenuItem(
                                    text = { Text(list.title) },
                                    onClick = {
                                        onDestinationSelected(list.id)
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (selectedDestinationListId != null && sourceFieldDefinitions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Custom fields",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    sourceFieldDefinitions.forEach { fieldDef ->
                        val isAutoMatched = fieldDef.name in destFieldLabels
                        val isChecked = isAutoMatched || fieldDef.id in selectedFieldIds

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    if (!isAutoMatched) {
                                        selectedFieldIds = if (checked) {
                                            selectedFieldIds + fieldDef.id
                                        } else {
                                            selectedFieldIds - fieldDef.id
                                        }
                                    }
                                },
                                enabled = !isAutoMatched
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fieldDef.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isAutoMatched)
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                if (isAutoMatched) {
                                    Text(
                                        text = "Already in destination",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val destId = selectedDestinationListId ?: return@TextButton
                    onMove(destId, selectedFieldIds.toList())
                    onDismiss()
                },
                enabled = selectedDestinationListId != null
            ) {
                Text("Move")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
