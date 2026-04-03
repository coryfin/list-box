package com.coreo.listbox.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coreo.listbox.components.RenameListDialog
import com.coreo.listbox.database.ItemEntity
import com.coreo.listbox.di.ServiceLocator
import com.coreo.listbox.viewmodel.ListDetailViewModel
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

//sealed class ListScreenStateEnriched {
//    object Base : ListScreenStateEnriched()
//    data class SelectOrDrag(val selectedId: String) : ListScreenStateEnriched()
//    data class MultiSelect(val selectedIds: Set<String>) : ListScreenStateEnriched()
//    data class Dragging(val draggedId: String, val currentIndex: Int) : ListScreenStateEnriched()
//}

enum class ListScreenState {
    Base,
    SelectOrDrag,
    MultiSelect,
    Dragging
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listId: String,
    onItemNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { ServiceLocator.getRepository() }
    val viewModel = remember { ListDetailViewModel(repository, listId) }
    val list by viewModel.list.collectAsState()
    val listTitle = list?.title ?: "List"
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showDeleteListDialog by remember { mutableStateOf(false) }
    var showDeleteItemsDialog by remember { mutableStateOf(false) }
    var showRenameListDialog by remember { mutableStateOf(false) }
    var showAddItemSheet by remember { mutableStateOf(false) }

    var screenState by remember { mutableStateOf(ListScreenState.Base) }
    val selectedItems = remember { mutableStateSetOf<String>() }

    val dbItems by viewModel.items.collectAsState()
    var orderedItems by remember { mutableStateOf(dbItems) }

    LaunchedEffect(dbItems) {
        if (screenState == ListScreenState.Base) {
            orderedItems = dbItems
        }
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        if (screenState == ListScreenState.SelectOrDrag) {
            screenState = ListScreenState.Dragging
            selectedItems.clear()
        }
        // Update the list
        orderedItems = orderedItems.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    fun toggleItemSelection(item: ItemEntity) {
        if (selectedItems.contains(item.id)) {
            selectedItems.remove(item.id)
            if (selectedItems.isEmpty()) {
                screenState = ListScreenState.Base
            }
        } else {
            selectedItems.add(item.id)
        }
    }


    if (showRenameListDialog) {
        RenameListDialog(
            currentTitle = listTitle,
            onDismiss = { showRenameListDialog = false },
            onRename = { newTitle -> viewModel.updateListTitle(newTitle) }
        )
    }

    if (showDeleteItemsDialog) {
        DeleteItemsDialog(
            itemCount = selectedItems.size,
            onConfirm = {
                for (itemId in selectedItems) {
                    viewModel.toggleItemSelection(itemId)
                }
                viewModel.deleteSelectedItems()
                selectedItems.clear()
                screenState = ListScreenState.Base
                showDeleteItemsDialog = false
            },
            onDismiss = { showDeleteItemsDialog = false }
        )
    }

    if (showDeleteListDialog) {
        DeleteListDialog(
            listTitle = listTitle,
            onConfirm = {
                showDeleteListDialog = false
                viewModel.deleteList()
                onBackClick()
            },
            onDismiss = { showDeleteListDialog = false }
        )
    }

    Scaffold(
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
                targetState = selectedItems.isNotEmpty(),
                transitionSpec = {
                    // Pure fade with mini-scale: minimalist transition
                    (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f)) togetherWith
                            (fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.95f))
                },
                label = "TopAppBarTransition"
            ) { hasSelection ->
                if (hasSelection) {
                    TopAppBar(
                        title = {
                            val count = selectedItems.size
                            Text(
                                text = if (count == 1) "1 item selected" else "$count items selected",
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                screenState = ListScreenState.Base
                                selectedItems.clear()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Exit multi-select"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { showDeleteItemsDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete selected items"
                                )
                            }
                        }
                    )
                } else {
                    TopAppBar(
                        title = {
                            Text(
                                text = listTitle,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
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
                                        showRenameListDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        showOverflowMenu = false
                                        showDeleteListDialog = true
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
                    viewModel.createItem(title, description)
                    showAddItemSheet = false
                }
            )
        }

        if (orderedItems.isEmpty()) {
            EmptyItemState(
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                items(orderedItems, key = { it.id }) { item ->
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = item.id
                    ) { isDragging ->
                        ItemCard(
                            item = item,
                            isSelected = selectedItems.contains(item.id),
                            isDragging = isDragging,
                            canDrag = screenState != ListScreenState.MultiSelect,
                            onTap = {
                                if (screenState == ListScreenState.Base) {
                                    onItemNavigate(item.id)
                                } else if (screenState == ListScreenState.MultiSelect) {
                                    toggleItemSelection(item)
                                }
                            },
                            onDragStart = {
                                if (screenState == ListScreenState.Base) {
                                    screenState = ListScreenState.SelectOrDrag
                                    toggleItemSelection(item)
                                }
                            },
                            onDragEnd = {
                                if (screenState == ListScreenState.SelectOrDrag) {
                                    screenState = ListScreenState.MultiSelect
                                } else if (screenState == ListScreenState.Dragging) {
                                    screenState = ListScreenState.Base
                                    viewModel.saveOrderedItems(orderedItems)
                                }
                            },
                            scope = this
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ItemCard(
    item: ItemEntity,
    isSelected: Boolean,
    isDragging: Boolean,
    canDrag: Boolean,
    onTap: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    scope: ReorderableCollectionItemScope
) {
    Card(
        onClick = onTap,
        modifier = with(scope) {
            Modifier
                .longPressDraggableHandle(
                    enabled = canDrag,
                    onDragStarted = { onDragStart() },
                    onDragStopped = onDragEnd
                )
                .fillMaxSize()
                .padding(vertical = 8.dp)
        },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected || isDragging)
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

@Composable
private fun DeleteItemsDialog(
    itemCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (itemCount == 1) "Delete 1 item?" else "Delete $itemCount items?") },
        text = { Text("This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteListDialog(
    listTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete list?") },
        text = { Text("\"$listTitle\" and all its items will be permanently deleted.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

