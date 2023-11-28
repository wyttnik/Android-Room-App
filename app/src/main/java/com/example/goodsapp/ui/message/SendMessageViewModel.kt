package com.example.goodsapp.ui.message

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SendMessageViewModel : ViewModel() {
    var messageState by mutableStateOf(MessageState())
        private set

    fun updateMessageState(name:String, message: String) {
        messageState = MessageState(name, message)
    }


}

data class MessageState(
    val name:String = "",
    val message: String = ""
)