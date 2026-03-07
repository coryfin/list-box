package com.coreo.listbox.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coreo.listbox.screens.HomeScreen
import com.coreo.listbox.screens.ItemDetailScreen
import com.coreo.listbox.screens.ListDetailScreen

sealed class NavigationState {
    object Home : NavigationState()
    data class ListDetail(val listId: String) : NavigationState()
    data class ItemDetail(val itemId: String) : NavigationState()
}

@Composable
actual fun ListBoxNavHost() {
    var currentState by remember { mutableStateOf<NavigationState>(NavigationState.Home) }
    
    when (currentState) {
        NavigationState.Home -> {
            HomeScreen(
                onListSelect = { listId ->
                    currentState = NavigationState.ListDetail(listId)
                }
            )
        }
        
        is NavigationState.ListDetail -> {
            val listId = (currentState as NavigationState.ListDetail).listId
            ListDetailScreen(
                listId = listId,
                onItemSelect = { itemId ->
                    currentState = NavigationState.ItemDetail(itemId)
                },
                onBackClick = {
                    currentState = NavigationState.Home
                }
            )
        }
        
        is NavigationState.ItemDetail -> {
            val itemId = (currentState as NavigationState.ItemDetail).itemId
            ItemDetailScreen(
                itemId = itemId,
                onBackClick = {
                    currentState = NavigationState.Home
                }
            )
        }
    }
}
