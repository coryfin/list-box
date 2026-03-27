package com.coreo.listbox.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.coreo.listbox.components.RenameListDialog
import com.coreo.listbox.database.ItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listId: String,
    onItemSelect: (String) -> Unit,
    onBackClick: () -> Unit,
    onDeleteList: () -> Unit,
    onRenameList: (String) -> Unit = {},
    onSaveItem: (title: String, description: String) -> Unit = { _, _ -> },
    onItemLongClick: (String) -> Unit = {},
    onExitMultiSelect: () -> Unit = {},
    onDeleteSelectedItems: () -> Unit = {},
    items: List<ItemEntity> = emptyList(),
    listTitle: String = "List",
    isMultiSelectMode: Boolean = false,
    selectedItems: Set<String> = emptySet()
) {
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteSelectedDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showAddItemSheet by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        snapAnimationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    if (showRenameDialog) {
        RenameListDialog(
            currentTitle = listTitle,
            onDismiss = { showRenameDialog = false },
            onRename = onRenameList
        )
    }

    if (showDeleteSelectedDialog) {
        val count = selectedItems.size
        AlertDialog(
            onDismissRequest = { showDeleteSelectedDialog = false },
            title = { Text(if (count == 1) "Delete 1 item?" else "Delete $count items?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteSelectedDialog = false
                    onDeleteSelectedItems()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteSelectedDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete list?") },
            text = { Text("\"$listTitle\" and all its items will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteList()
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
            FloatingActionButton(
                onClick = { showAddItemSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add item",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        topBar = {
            AnimatedContent(
                targetState = isMultiSelectMode,
                transitionSpec = {
                    if (targetState) {
                        // entering multi-select: slide in from top, fade in
                        (slideInVertically { -it / 4 } + fadeIn()) togetherWith
                            (slideOutVertically { it / 4 } + fadeOut())
                    } else {
                        // exiting multi-select: slide in from below, fade in
                        (slideInVertically { it / 4 } + fadeIn()) togetherWith
                            (slideOutVertically { -it / 4 } + fadeOut())
                    }
                },
                label = "TopAppBarTransition"
            ) { multiSelectActive ->
            if (multiSelectActive) {
                TopAppBar(
                    title = {
                        val count = selectedItems.size
                        Text(
                            text = if (count == 1) "1 item selected" else "$count items selected",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onExitMultiSelect) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit multi-select"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDeleteSelectedDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete selected items"
                            )
                        }
                    }
                )
            } else {
                MediumTopAppBar(
                    title = {
                        Text(
                            text = listTitle,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
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
                                text = { Text("Rename") },
                                onClick = {
                                    showOverflowMenu = false
                                    showRenameDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showOverflowMenu = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                )
            }
            } // end AnimatedContent
        }
    ) { paddingValues ->
        if (showAddItemSheet) {
            AddItemBottomSheet(
                onDismiss = { showAddItemSheet = false },
                onSave = { title, description ->
                    onSaveItem(title, description)
                    showAddItemSheet = false
                }
            )
        }

        if (items.isEmpty()) {
            EmptyItemState(
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                items(items) { item ->
                    ItemCard(
                        item = item,
                        isSelected = selectedItems.contains(item.id),
                        onClick = { onItemSelect(item.id) },
                        onLongClick = { onItemLongClick(item.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ItemCard(
    item: ItemEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp)
        )
    }
}

