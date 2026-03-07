package com.coreo.listbox.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coreo.listbox.screens.HomeScreen
import com.coreo.listbox.screens.ItemDetailScreen
import com.coreo.listbox.screens.ListDetailScreen

@Composable
actual fun ListBoxNavHost() {
    val navController: NavHostController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onListSelect = { listId ->
                    navController.navigate(Routes.listDetail(listId))
                }
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
