package com.coreo.listbox.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.coreo.listbox.components.AddFieldDialog
import com.coreo.listbox.di.ServiceLocator
import com.coreo.listbox.viewmodel.ItemDetailViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ItemDetailScreen(
    itemId: String,
    onBackClick: () -> Unit
) {
    val repository = remember { ServiceLocator.getRepository() }
    val viewModel = remember { ItemDetailViewModel(repository, itemId) }
    val item by viewModel.item.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val fieldDefinitions by viewModel.fieldDefinitions.collectAsState()
    val fieldOptions by viewModel.fieldOptions.collectAsState()
    val fieldValues by viewModel.fieldValues.collectAsState()
    val draftFieldValues by viewModel.draftFieldValues.collectAsState()
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showAddFieldDialog by remember { mutableStateOf(false) }
    // Drafts are keyed on item id so they reset on first load (null → actual id)
    var draftTitle by remember(item?.id) {
        val text = item?.title ?: ""
        mutableStateOf(TextFieldValue(text = text, selection = TextRange(text.length)))
    }
    var draftDescription by remember(item?.id) { mutableStateOf(TextFieldValue(item?.description ?: "")) }
    val titleFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            titleFocusRequester.requestFocus()
        }
    }
    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var prevScrollMax by remember { mutableStateOf(0) }

    LaunchedEffect(scrollState.maxValue) {
        val newMax = scrollState.maxValue
        if (isEditMode && newMax > prevScrollMax && scrollState.value >= prevScrollMax - 4) {
            scrollState.animateScrollTo(newMax)
        }
        prevScrollMax = newMax
    }

    LaunchedEffect(draftDescription.selection) {
        if (isEditMode && draftDescription.selection.end == draftDescription.text.length) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    val hasUnsavedChanges = isEditMode &&
        (draftTitle.text != (item?.title ?: "") ||
            draftDescription.text != (item?.description ?: "") ||
            viewModel.fieldValuesHaveChanged())

    BackHandler(enabled = isEditMode) {
        if (hasUnsavedChanges) {
            showDiscardDialog = true
        } else {
            draftTitle = TextFieldValue(item?.title ?: "")
            draftDescription = TextFieldValue(item?.description ?: "")
            viewModel.exitEditMode()
        }
    }

    if (showAddFieldDialog) {
        AddFieldDialog(
            onDismiss = { showAddFieldDialog = false },
            onSave = { name, dataType, options ->
                viewModel.addFieldDefinition(name, dataType, options)
            }
        )
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("Your edits will be lost.") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    draftTitle = TextFieldValue(item?.title ?: "")
                    draftDescription = TextFieldValue(item?.description ?: "")
                    viewModel.exitEditMode()
                }) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Keep Editing")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete item?") },
            text = { Text("\"${item?.title ?: ""}\" will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteItem()
                    onBackClick()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            if (!isEditMode) {
                FloatingActionButton(onClick = { viewModel.enterEditMode() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {},
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    if (isEditMode) {
                        IconButton(onClick = {
                            if (hasUnsavedChanges) {
                                showDiscardDialog = true
                            } else {
                                draftTitle = TextFieldValue(item?.title ?: "")
                                draftDescription = TextFieldValue(item?.description ?: "")
                                viewModel.exitEditMode()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    } else {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(
                            onClick = { viewModel.saveItem(draftTitle.text, draftDescription.text.ifBlank { null }) },
                            enabled = draftTitle.text.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save"
                            )
                        }
                    } else {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options"
                            )
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showOverflowMenu = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (isEditMode) {
                BasicTextField(
                    value = draftTitle,
                    onValueChange = { draftTitle = it },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().focusRequester(titleFocusRequester),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box {
                            if (draftTitle.text.isEmpty()) {
                                Text(
                                    text = "Title",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            } else {
                Text(
                    text = item?.title ?: "",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(Modifier.height(12.dp))
            if (isEditMode) {
                BasicTextField(
                    value = draftDescription,
                    onValueChange = { draftDescription = it },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box {
                            if (draftDescription.text.isEmpty()) {
                                Text(
                                    text = "Add a description\u2026",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            } else {
                val description = item?.description
                Text(
                    text = if (!description.isNullOrBlank()) description else "No description",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (!description.isNullOrBlank()) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Custom fields
            if (fieldDefinitions.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
            }
            fieldDefinitions.forEach { fieldDef ->
                if (isEditMode) {
                    val currentValue = draftFieldValues[fieldDef.id] ?: fieldValues[fieldDef.id] ?: ""
                    if (fieldDef.dataType == "DROPDOWN") {
                        val options = fieldOptions[fieldDef.id] ?: emptyList()
                        var dropdownExpanded by remember(fieldDef.id) { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = currentValue,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(fieldDef.name) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.label) },
                                        onClick = {
                                            viewModel.updateDraftFieldValue(fieldDef.id, option.label)
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = currentValue,
                            onValueChange = { viewModel.updateDraftFieldValue(fieldDef.id, it) },
                            label = { Text(fieldDef.name) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                } else {
                    val value = fieldValues[fieldDef.id]
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = fieldDef.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (!value.isNullOrBlank()) value else "None",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (!value.isNullOrBlank()) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (isEditMode) {
                OutlinedButton(
                    onClick = { showAddFieldDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text("Add field")
                }
            }
        }
    }
}
