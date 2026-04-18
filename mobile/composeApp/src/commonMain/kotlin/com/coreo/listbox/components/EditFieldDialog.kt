package com.coreo.listbox.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

private val EDIT_DATA_TYPES = listOf("Text", "Dropdown")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFieldBottomSheet(
    initialName: String,
    initialDataType: String,
    initialOptions: List<String>,
    onDismiss: () -> Unit,
    onSave: (name: String, dataType: String, options: List<String>) -> Unit
) {
    var fieldLabel by remember { mutableStateOf(initialName) }
    val initialDisplayType = if (initialDataType == "DROPDOWN") "Dropdown" else "Text"
    var selectedDataType by remember { mutableStateOf(initialDisplayType) }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    val options = remember {
        mutableStateListOf<String>().also { list ->
            if (initialOptions.isEmpty() && initialDisplayType == "Dropdown") {
                list.add("")
            } else {
                list.addAll(initialOptions)
            }
        }
    }
    val focusRequesters = remember {
        mutableStateListOf<FocusRequester>().also { list ->
            repeat(options.size.coerceAtLeast(1)) { list.add(FocusRequester()) }
        }
    }

    val isDropdown = selectedDataType == "Dropdown"
    val isSaveEnabled = fieldLabel.trim().isNotEmpty() &&
        (!isDropdown || options.any { it.trim().isNotEmpty() })

    LaunchedEffect(options.size) {
        if (isDropdown && options.size > 0) {
            focusRequesters.lastOrNull()?.requestFocus()
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .imePadding()
                .navigationBarsPadding()
        ) {
            Text(
                text = "Edit field",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))
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
                    EDIT_DATA_TYPES.forEach { type ->
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = option,
                            onValueChange = { options[index] = it },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequesters[index]),
                            singleLine = true
                        )
                        IconButton(onClick = {
                            options.removeAt(index)
                            focusRequesters.removeAt(index)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove option"
                            )
                        }
                    }
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
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
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
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

