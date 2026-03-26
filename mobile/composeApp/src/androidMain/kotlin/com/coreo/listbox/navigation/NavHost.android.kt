package com.coreo.listbox.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coreo.listbox.screens.HomeScreen
import com.coreo.listbox.screens.ItemDetailScreen
import com.coreo.listbox.screens.ListDetailScreen
import com.coreo.listbox.viewmodel.HomeViewModel
import com.coreo.listbox.viewmodel.ItemDetailViewModel
import com.coreo.listbox.viewmodel.ListDetailViewModel
import com.coreo.listbox.di.ServiceLocator

@Composable
actual fun ListBoxNavHost() {
    val navController: NavHostController = rememberNavController()
    val repository = remember { ServiceLocator.getRepository() }
    
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val homeViewModel = remember { HomeViewModel(repository) }
            val lists = homeViewModel.lists.collectAsState().value
            
            HomeScreen(
                onListSelect = { listId ->
                    navController.navigate(Routes.listDetail(listId))
                },
                onCreateBlankList = { title ->
                    val newList = homeViewModel.createListAndGetId(title)
                    if (newList != null) {
                        navController.navigate(Routes.listDetail(newList.id))
                    }
                },
                onCreateFromTemplate = { templateType ->
                    val newList = homeViewModel.createListFromTemplateAndGetId(templateType)
                    if (newList != null) {
                        navController.navigate(Routes.listDetail(newList.id))
                    }
                },
                listEntities = lists
            )
        }
        
        composable(Routes.LIST_DETAIL) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            val listDetailViewModel = remember { ListDetailViewModel(repository, listId) }
            val items = listDetailViewModel.items.collectAsState().value
            val list = listDetailViewModel.list.collectAsState().value
            val isMultiSelectMode = listDetailViewModel.isMultiSelectMode.collectAsState().value
            val selectedItems = listDetailViewModel.selectedItems.collectAsState().value

            ListDetailScreen(
                listId = listId,
                onItemSelect = { itemId ->
                    if (isMultiSelectMode) {
                        listDetailViewModel.toggleItemSelection(itemId)
                    } else {
                        navController.navigate(Routes.itemDetail(itemId))
                    }
                },
                onBackClick = {
                    navController.navigateUp()
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
                onItemLongClick = { itemId ->
                    if (isMultiSelectMode) {
                        listDetailViewModel.toggleItemSelection(itemId)
                    } else {
                        listDetailViewModel.enterMultiSelect(itemId)
                    }
                },
                items = items,
                listTitle = list?.title ?: "List",
                isMultiSelectMode = isMultiSelectMode,
                selectedItems = selectedItems
            )
        }
        
        composable(Routes.ITEM_DETAIL) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            val itemDetailViewModel = remember { ItemDetailViewModel(repository, itemId) }
            val item = itemDetailViewModel.item.collectAsState().value
            val isEditMode = itemDetailViewModel.isEditMode.collectAsState().value

            ItemDetailScreen(
                itemId = itemId,
                onBackClick = { navController.navigateUp() },
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
