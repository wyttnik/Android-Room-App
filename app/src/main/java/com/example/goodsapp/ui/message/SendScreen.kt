package com.example.goodsapp.ui.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goodsapp.R

@Composable
fun SendScreen(messageState: MessageState,
               onMessageSend: ()->Unit = {},
               onValueChange: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()){
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp,
                top = 24.dp)) {
            Text(text = "To:", style = TextStyle(fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif)
            )
            Image(painter = painterResource(R.mipmap.logo_avatar),
                contentDescription = null,
                modifier = Modifier.padding(start = 25.dp))
            Text(
                text = messageState.name,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif
                ),
                modifier = Modifier.padding(start = 25.dp)
            )
        }
        Text(text = "Body:",
            style = TextStyle(fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif),
            modifier = Modifier.padding(start = 24.dp, end = 24.dp,
                top = 16.dp))
        TextField(value = messageState.message,
            onValueChange = {onValueChange(messageState.name, it)},
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif
            ),
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth())
        Row{
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onMessageSend,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
            ) {
                Text("SEND")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSendScreen() {
    SendScreen(MessageState("John", "Item Info:\n" +
            "  Item                            Example\n" +
            "  Quantity in stock      1\n" +
            "  Price                           \$1.99\n" +
            "Vendor Info:\n" +
            "  Name                         Example\n" +
            "  Email                          ex@ex.com\n" +
            "  Phone                        +71231231221\n"),
        onValueChange = { _:String, _:String ->  })
}
