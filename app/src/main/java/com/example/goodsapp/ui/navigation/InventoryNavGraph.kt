package com.example.goodsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.goodsapp.security.SecurityViewModel
import com.example.goodsapp.security.SettingsDestination
import com.example.goodsapp.security.SettingsScreen
import com.example.goodsapp.ui.home.HomeDestination
import com.example.goodsapp.ui.home.HomeScreen
import com.example.goodsapp.ui.item.ItemDetailsDestination
import com.example.goodsapp.ui.item.ItemDetailsScreen
import com.example.goodsapp.ui.item.ItemEditDestination
import com.example.goodsapp.ui.item.ItemEditScreen
import com.example.goodsapp.ui.item.ItemEntryDestination
import com.example.goodsapp.ui.item.ItemEntryScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    securityManager: SecurityViewModel
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToItemEntry = { navController.navigate(ItemEntryDestination.route) },
                navigateToItemUpdate = {
                    navController.navigate("${ItemDetailsDestination.route}/${it}")
                },
                navigateToSettings = { navController.navigate(SettingsDestination.route) },
                settingsViewModel = securityManager
            )
        }
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                onNavigateUp = { navController.navigateUp() },
                viewModel = securityManager,
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(route = ItemEntryDestination.route) {
            ItemEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                settingsViewModel = securityManager
            )
        }
        composable(
            route = ItemDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ItemDetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            ItemDetailsScreen(
                navigateToEditItem = { navController.navigate("${ItemEditDestination.route}/$it") },
                navigateBack = { navController.navigateUp() },
                settingsViewModel = securityManager
            )
        }
        composable(
            route = ItemEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ItemEditDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            ItemEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                settingsViewModel = securityManager
            )
        }
    }
}
