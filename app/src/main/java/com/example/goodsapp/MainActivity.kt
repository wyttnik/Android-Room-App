package com.example.goodsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.goodsapp.security.MasterFiles
import com.example.goodsapp.ui.theme.InventoryTheme


class MainActivity : ComponentActivity() {

    private lateinit var sharingShortcutsManager: SharingShortcutsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MasterFiles.initialize(application)
        sharingShortcutsManager = SharingShortcutsManager().also {
//            it.removeAllDirectShareTargets(this)
            it.pushDirectShareTargets(this)
        }

        setContent {
            InventoryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InventoryApp()
                }
            }
        }
    }
}
