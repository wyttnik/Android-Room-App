package com.example.goodsapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.goodsapp.data.Contact
import com.example.goodsapp.ui.message.SendMessageViewModel
import com.example.goodsapp.ui.message.SendScreen

/**
 * Provides the UI for sharing a text with a [Contact].
 */
class SendMessageActivity: ComponentActivity() {

    /**
     * The ID of the contact to share the text with.
     */
    private var contactId: Int = 0

    private val viewModel: SendMessageViewModel by viewModels()

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK){
            val data = it.data
            val selectedContact = data?.getIntExtra(Contact.id, Contact.invalidId) ?: -1
            viewModel.updateMessageState(
                Contact.byId(selectedContact).name,
                viewModel.messageState.message)
        }
        else finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SendScreen(viewModel.messageState, ::send, viewModel::updateMessageState)
        }

        val handled = handleIntent(intent)
        if (!handled) {
            finish()
            return
        }

        if (contactId == Contact.invalidId) {
            selectContact()
        }
    }

    /**
     * Handles the passed [Intent]. This method can only handle intents for sharing a plain
     * text. [textToShare] and [contactId] are modified accordingly.
     *
     * @param intent The [Intent].
     * @return true if the [intent] is handled properly.
     */
    private fun handleIntent(intent: Intent): Boolean {
        if (Intent.ACTION_SEND == intent.action && "text/plain" == intent.type) {
            val textToShare: String?
            if (intent.hasExtra(Contact.id)) {
                textToShare = "Item Info:\n" +
                        "  Item                            Example\n" +
                        "  Quantity in stock      1\n" +
                        "  Price                           \$1.99\n" +
                        "Vendor Info:\n" +
                        "  Name                         Example\n" +
                        "  Email                          ex@ex.com\n" +
                        "  Phone                        +71231231221\n"
                contactId = intent.getIntExtra(Contact.id, Contact.invalidId)
            }
            else{
                textToShare = intent.getStringExtra(Intent.EXTRA_TEXT)
                // The intent comes from Direct share
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
                    intent.hasExtra(Intent.EXTRA_SHORTCUT_ID)) {
                    val shortcutId = intent.getStringExtra(Intent.EXTRA_SHORTCUT_ID)
                    contactId = Integer.valueOf(shortcutId!!)
                } else {
                    // The text was shared and the user chose our app
                    contactId = Contact.invalidId
                }
            }

            val contactName = when(contactId) {
                Contact.invalidId -> ""
                else -> {
                    Contact.byId(contactId).name}
            }

            viewModel.updateMessageState(contactName, textToShare ?: "")
            return true
        }
        return false
    }

    /**
     * Delegates selection of a {@Contact} to [SelectContactActivity].
     */
    private fun selectContact() {
        val intent = Intent(this, SelectContactActivity::class.java)
        intent.action = SelectContactActivity.ACTION_SELECT_CONTACT
        resultLauncher.launch(intent)
    }

    private fun send() {
        Toast.makeText(this,
            getString(R.string.message_sent, "Item Details",
                viewModel.messageState.name),
            Toast.LENGTH_SHORT).show()
        finish()
    }
}
