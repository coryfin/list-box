package com.coreo.listbox.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.coreo.listbox.components.CreateListDialog
import com.coreo.listbox.database.ListEntity
import com.coreo.listbox.di.ServiceLocator
import com.coreo.listbox.util.formatDateForListCard
import com.coreo.listbox.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import listbox.composeapp.generated.resources.Res
import listbox.composeapp.generated.resources.empty_state
import listbox.composeapp.generated.resources.listbox_banner_logo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onListSelect: (String) -> Unit,
    onListCreated: (String) -> Unit
) {
    val repository = remember { ServiceLocator.getRepository() }
    val viewModel = remember { HomeViewModel(repository) }
    val listEntities by viewModel.lists.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showCreateDialog by remember { mutableStateOf(false) }
    val isPopulated = listEntities.isNotEmpty()
    
    if (showCreateDialog) {
        CreateListDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title ->
                coroutineScope.launch {
                    val newList = viewModel.createListAndGetId(title)
                    if (newList != null) onListCreated(newList.id)
                }
                showCreateDialog = false
            }
        )
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(Res.drawable.listbox_banner_logo),
                        contentDescription = "ListBox Banner",
                        modifier = Modifier
                            .height(40.dp),
                        contentScale = ContentScale.Inside
                    )
                }
            )
        },
        floatingActionButton = {
            if (isPopulated) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    ) { paddingValues ->
        if (listEntities.isEmpty()) {
            EmptyListState(
                onCreateBlankList = { title ->
                    val newList = viewModel.createListAndGetId(title)
                    if (newList != null) onListCreated(newList.id)
                },
                onCreateFromTemplate = { templateType ->
                    val newList = viewModel.createListFromTemplateAndGetId(templateType)
                    if (newList != null) onListCreated(newList.id)
                },
                onShowCreateDialog = { showCreateDialog = true },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listEntities) { list ->
                    ListCard(
                        list = list,
                        onClick = { onListSelect(list.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyListState(
    onCreateBlankList: suspend (String) -> Unit,
    onCreateFromTemplate: suspend (String) -> Unit,
    onShowCreateDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_state),
            contentDescription = null,
            modifier = Modifier
                .width(220.dp)
                .height(230.dp)
                .padding(bottom = 24.dp)
        )
        Text(
            text = "Your lists are empty.",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Create your first list to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp)
        )
        
        // Blank List Button
        Button(
            onClick = onShowCreateDialog,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Blank List")
        }
        
        // Template Buttons
        Text(
            text = "Or choose a template:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.Start)
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TemplateButton(
                title = "Gift Ideas",
                onClick = {
                    coroutineScope.launch {
                        onCreateFromTemplate("gift-ideas")
                    }
                }
            )
            TemplateButton(
                title = "Recipe Box",
                onClick = {
                    coroutineScope.launch {
                        onCreateFromTemplate("recipe-box")
                    }
                }
            )
            TemplateButton(
                title = "Goal Tracker",
                onClick = {
                    coroutineScope.launch {
                        onCreateFromTemplate("goal-tracker")
                    }
                }
            )
        }
    }
}

@Composable
private fun TemplateButton(
    title: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(title)
    }
}

@Composable
private fun ListCard(
    list: ListEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = list.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatDateForListCard(list.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
