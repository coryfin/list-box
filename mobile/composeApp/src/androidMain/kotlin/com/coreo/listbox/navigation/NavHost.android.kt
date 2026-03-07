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
            ListDetailScreen(
                listId = listId,
                onItemSelect = { itemId ->
                    navController.navigate(Routes.itemDetail(itemId))
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Routes.ITEM_DETAIL) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            ItemDetailScreen(
                itemId = itemId,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}
