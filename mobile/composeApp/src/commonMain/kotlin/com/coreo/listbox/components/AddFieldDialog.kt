package com.coreo.listbox.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

private val DATA_TYPES = listOf("Text", "Dropdown")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFieldDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, dataType: String, options: List<String>) -> Unit
) {
    var fieldLabel by remember { mutableStateOf("") }
    var selectedDataType by remember { mutableStateOf(DATA_TYPES[0]) }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    val options = remember { mutableStateListOf("") }
    val focusRequesters = remember { mutableStateListOf(FocusRequester()) }

    val isDropdown = selectedDataType == "Dropdown"
    val isSaveEnabled = fieldLabel.trim().isNotEmpty() &&
        (!isDropdown || options.any { it.trim().isNotEmpty() })

    // Request focus on the last option field whenever a new one is added
    LaunchedEffect(options.size) {
        if (isDropdown && options.size > 0) {
            focusRequesters.lastOrNull()?.requestFocus()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New field") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fieldLabel,
                    onValueChange = { fieldLabel = it },
                    label = { Text("Field name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = typeMenuExpanded,
                    onExpandedChange = { typeMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedDataType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Data type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = typeMenuExpanded,
                        onDismissRequest = { typeMenuExpanded = false }
                    ) {
                        DATA_TYPES.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedDataType = type
                                    typeMenuExpanded = false
                                    if (type == "Dropdown" && options.isEmpty()) {
                                        options.add("")
                                        focusRequesters.add(FocusRequester())
                                    }
                                }
                            )
                        }
                    }
                }
                if (isDropdown) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Options",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    options.forEachIndexed { index, option ->
                        OutlinedTextField(
                            value = option,
                            onValueChange = { options[index] = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequesters[index]),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    TextButton(
                        onClick = {
                            options.add("")
                            focusRequesters.add(FocusRequester())
                        },
                        modifier = Modifier.padding(start = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(Modifier.padding(horizontal = 4.dp))
                        Text("Add Option")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        fieldLabel.trim(),
                        selectedDataType.uppercase(),
                        if (isDropdown) options.filter { it.trim().isNotEmpty() } else emptyList()
                    )
                    onDismiss()
                },
                enabled = isSaveEnabled
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
