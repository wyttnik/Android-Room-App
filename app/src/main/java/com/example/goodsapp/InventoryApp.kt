@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.goodsapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.goodsapp.R.string
import com.example.goodsapp.security.SecurityViewModel
import com.example.goodsapp.ui.AppViewModelProvider
import com.example.goodsapp.ui.navigation.InventoryNavHost

private fun openFileIntent() = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
    addCategory(Intent.CATEGORY_OPENABLE)
    type = "application/json"
}

/**
 * Top level composable that represents screens for the application.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InventoryApp(navController: NavHostController = rememberNavController(),
                 securityManager: SecurityViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    InventoryNavHost(navController = navController, securityManager = securityManager)
}

/**
 * App bar to display title and conditionally display the back navigation.
 */
@Composable
fun InventoryTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    isMainScreen: Boolean = false,
    isSettingsScreen: Boolean = false,
    isEntryScreen: Boolean = false,
    onSettingsClick: () -> Unit = {},
    viewModel: SecurityViewModel,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
    intentResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>? = null
) {
    var displayMenu by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = {when(isSettingsScreen){
                    true -> {
                        viewModel.putEncryptedSettings()
                        navigateUp()
                    }
                    false -> navigateUp()
                }}) {
                    Icon(
                        imageVector = Filled.ArrowBack,
                        contentDescription = stringResource(string.back_button)
                    )
                }
            }
        },
        actions = {
            if (isMainScreen) {
                IconButton(onClick = {
                    onSettingsClick()
                }) {
                    Icon(
                        imageVector = Filled.Settings,
                        contentDescription = "App settings"
                    )
                }
            }
            if (isEntryScreen) {
                IconButton(onClick = {
                    displayMenu = true
                }) {
                    Icon(
                        imageVector = Filled.Menu,
                        contentDescription = "Options"
                    )
                }
                DropdownMenu(
                    expanded = displayMenu,
                    onDismissRequest = { displayMenu = false }
                ) {
                    DropdownMenuItem(onClick = {intentResultLauncher?.launch(openFileIntent())}, text =
                    { Text("Import from file") })
                }
            }
        }
    )
}
