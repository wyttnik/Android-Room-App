package com.example.goodsapp.ui.contacts

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goodsapp.R
import com.example.goodsapp.SelectContactActivity
import com.example.goodsapp.data.Contact

@Composable
fun ContactList(activity: ComponentActivity) {
    Scaffold {
        Column(modifier = Modifier.padding(it).fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
                items(items = Contact.contacts, key = { it.id }) { item ->
                    ContactItem(item = item, modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .clickable {
                            val data = Intent()
                            data.putExtra(Contact.id, item.id-1)
                            activity.setResult(ComponentActivity.RESULT_OK, data)
                            activity.finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactItem(
    item: Contact, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(R.mipmap.logo_avatar), contentDescription = null)
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 25.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactListPreviw() {
    ContactList(SelectContactActivity())
}