package com.coreo.listbox.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coreo.listbox.screens.HomeScreen
import com.coreo.listbox.screens.ItemDetailScreen
import com.coreo.listbox.screens.ListDetailScreen
import com.coreo.listbox.viewmodel.HomeViewModel
import com.coreo.listbox.viewmodel.ItemDetailViewModel
import com.coreo.listbox.viewmodel.ListDetailViewModel
import com.coreo.listbox.di.ServiceLocator

sealed class NavigationState {
    object Home : NavigationState()
    data class ListDetail(val listId: String) : NavigationState()
    data class ItemDetail(val itemId: String, val listId: String) : NavigationState()
}

@Composable
actual fun ListBoxNavHost() {
    var currentState by remember { mutableStateOf<NavigationState>(NavigationState.Home) }
    val repository = remember { ServiceLocator.getRepository() }
    
    when (currentState) {
        NavigationState.Home -> {
            val homeViewModel = remember { HomeViewModel(repository) }
            val lists = homeViewModel.lists.collectAsState().value
            
            HomeScreen(
                onListSelect = { listId ->
                    currentState = NavigationState.ListDetail(listId)
                },
                onCreateBlankList = { title ->
                    val newList = homeViewModel.createListAndGetId(title)
                    if (newList != null) {
                        currentState = NavigationState.ListDetail(newList.id)
                    }
                },
                onCreateFromTemplate = { templateType ->
                    val newList = homeViewModel.createListFromTemplateAndGetId(templateType)
                    if (newList != null) {
                        currentState = NavigationState.ListDetail(newList.id)
                    }
                },
                listEntities = lists
            )
        }
        
        is NavigationState.ListDetail -> {
            val listId = (currentState as NavigationState.ListDetail).listId
            val listDetailViewModel = remember { ListDetailViewModel(repository, listId) }
            val items = listDetailViewModel.items.collectAsState().value
            val list = listDetailViewModel.list.collectAsState().value

            ListDetailScreen(
                listId = listId,
                onItemSelect = { itemId ->
                    currentState = NavigationState.ItemDetail(itemId = itemId, listId = listId)
                },
                onBackClick = {
                    currentState = NavigationState.Home
                },
                onDeleteList = {
                    listDetailViewModel.deleteList()
                },
                onRenameList = { newTitle ->
                    listDetailViewModel.updateListTitle(newTitle)
                },
                onSaveItem = { title, description ->
                    listDetailViewModel.createItem(title, description)
                },
                items = items,
                listTitle = list?.title ?: "List"
            )
        }
        
        is NavigationState.ItemDetail -> {
            val itemId = (currentState as NavigationState.ItemDetail).itemId
            val itemDetailViewModel = remember { ItemDetailViewModel(repository, itemId) }
            val item = itemDetailViewModel.item.collectAsState().value
            val isEditMode = itemDetailViewModel.isEditMode.collectAsState().value

            val listId = (currentState as NavigationState.ItemDetail).listId
            ItemDetailScreen(
                itemId = itemId,
                onBackClick = { currentState = NavigationState.ListDetail(listId) },
                onDeleteItem = { itemDetailViewModel.deleteItem() },
                onEnterEditMode = { itemDetailViewModel.enterEditMode() },
                onExitEditMode = { itemDetailViewModel.exitEditMode() },
                onSaveItem = { title, description -> itemDetailViewModel.saveItem(title, description) },
                item = item,
                isEditMode = isEditMode
            )
        }
    }
}
