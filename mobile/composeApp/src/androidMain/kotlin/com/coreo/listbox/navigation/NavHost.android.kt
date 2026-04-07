package com.coreo.listbox.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coreo.listbox.screens.HomeScreen
import com.coreo.listbox.screens.ItemDetailScreen
import com.coreo.listbox.screens.ListDetailScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
actual fun ListBoxNavHost() {
    val navController: NavHostController = rememberNavController()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this,
                    onListSelect = { listId -> navController.navigate(Routes.listDetail(listId)) },
                    onListCreated = { listId -> navController.navigate(Routes.listDetail(listId)) }
                )
            }

            composable(
                Routes.LIST_DETAIL,
                exitTransition = {
                    if (targetState.destination.route == Routes.ITEM_DETAIL) {
                        fadeOut(animationSpec = tween(200)) +
                                slideOutHorizontally(targetOffsetX = { -(it * 0.1f).toInt() }, animationSpec = tween(300))
                    } else {
                        fadeOut(animationSpec = tween(700))
                    }
                },
                popEnterTransition = {
                    if (initialState.destination.route == Routes.ITEM_DETAIL) {
                        fadeIn(animationSpec = tween(200)) +
                                slideInHorizontally(initialOffsetX = { -(it * 0.1f).toInt() }, animationSpec = tween(300))
                    } else {
                        fadeIn(animationSpec = tween(700))
                    }
                }
            ) { backStackEntry ->
                val listId = backStackEntry.arguments?.getString("listId") ?: ""
                ListDetailScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this,
                    listId = listId,
                    onItemNavigate = { itemId -> navController.navigate(Routes.itemDetail(itemId)) },
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable(
                Routes.ITEM_DETAIL,
                enterTransition = {
                    fadeIn(animationSpec = tween(200)) +
                            slideInHorizontally(initialOffsetX = { (it * 0.1f).toInt() }, animationSpec = tween(300))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(200)) +
                            slideOutHorizontally(targetOffsetX = { (it * 0.1f).toInt() }, animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                ItemDetailScreen(
                    itemId = itemId,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}
