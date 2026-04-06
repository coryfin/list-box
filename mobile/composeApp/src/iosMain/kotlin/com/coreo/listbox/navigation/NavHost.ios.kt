package com.coreo.listbox.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
    data class ItemDetail(val itemId: String, val listId: String) : NavigationState()
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
actual fun ListBoxNavHost() {
    var currentState by remember { mutableStateOf<NavigationState>(NavigationState.Home) }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = currentState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "NavigationTransition"
        ) { state ->
            when (state) {
                NavigationState.Home -> {
                    HomeScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this,
                        onListSelect = { listId -> currentState = NavigationState.ListDetail(listId) },
                        onListCreated = { listId -> currentState = NavigationState.ListDetail(listId) }
                    )
                }

                is NavigationState.ListDetail -> {
                    ListDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this,
                        listId = state.listId,
                        onItemNavigate = { itemId ->
                            currentState = NavigationState.ItemDetail(itemId = itemId, listId = state.listId)
                        },
                        onBackClick = { currentState = NavigationState.Home }
                    )
                }

                is NavigationState.ItemDetail -> {
                    ItemDetailScreen(
                        itemId = state.itemId,
                        onBackClick = { currentState = NavigationState.ListDetail(state.listId) }
                    )
                }
            }
        }
    }
}
