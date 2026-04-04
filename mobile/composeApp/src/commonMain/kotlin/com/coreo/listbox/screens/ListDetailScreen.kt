package com.coreo.listbox.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.coreo.listbox.components.AddItemBottomSheet
import com.coreo.listbox.components.DeleteItemsDialog
import com.coreo.listbox.components.DeleteListDialog
import com.coreo.listbox.components.RenameListDialog
import com.coreo.listbox.database.ItemEntity
import com.coreo.listbox.di.ServiceLocator
import com.coreo.listbox.viewmodel.ListDetailViewModel
import com.coreo.listbox.viewmodel.ListInteractionState
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

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

    val listInteractionState by viewModel.listInteractionState.collectAsState()
    val selectedItemIds by viewModel.selectedItemIds.collectAsState()
    val orderedItems by viewModel.orderedItems.collectAsState()

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        viewModel.onItemMoved(from.index, to.index)
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
            itemCount = selectedItemIds.size,
            onConfirm = {
                viewModel.deleteSelectedItems()
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
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add item",
                )
            }
        },
        topBar = {
            AnimatedContent(
                targetState = selectedItemIds.isNotEmpty(),
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f)) togetherWith
                            (fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.95f))
                },
                label = "TopAppBarTransition"
            ) { hasSelection ->
                if (hasSelection) {
                    TopAppBar(
                        title = {
                            val count = selectedItemIds.size
                            Text(
                                text = if (count == 1) "1 item selected" else "$count items selected",
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { viewModel.exitMultiSelect() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear selection"
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
            EmptyListState(
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
                            isSelected = selectedItemIds.contains(item.id),
                            isDragging = isDragging,
                            canDrag = listInteractionState != ListInteractionState.MultiSelect,
                            onTap = {
                                if (listInteractionState == ListInteractionState.Default) {
                                    onItemNavigate(item.id)
                                } else if (listInteractionState == ListInteractionState.MultiSelect) {
                                    viewModel.toggleItemSelection(item.id)
                                }
                            },
                            onDragStart = { viewModel.onDragStarted(item.id) },
                            onDragEnd = { viewModel.onDragEnded() },
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
fun EmptyListState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your list is empty.",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Add your first item to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
