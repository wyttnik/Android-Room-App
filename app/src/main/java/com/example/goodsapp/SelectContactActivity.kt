package com.example.goodsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.goodsapp.ui.contacts.ContactList

/**
 * The dialog for selecting a contact to share the text with. This dialog is shown when the user
 * taps on this sample's icon rather than any of the Direct Share contacts.
 */
class SelectContactActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactList(this)
        }

        if (ACTION_SELECT_CONTACT != intent.action) {
            finish()
            return
        }
    }

    companion object {
        /**
         * The action string for Intents.
         */
        const val ACTION_SELECT_CONTACT =
                "com.example.goodsapp.intent.action.SELECT_CONTACT"
    }
}
