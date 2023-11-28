package com.example.goodsapp.ui.item

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodsapp.InventoryTopAppBar
import com.example.goodsapp.R
import com.example.goodsapp.data.CreationType
import com.example.goodsapp.security.SecurityViewModel
import com.example.goodsapp.security.SettingsDetails
import com.example.goodsapp.security.SettingsUiState
import com.example.goodsapp.ui.AppViewModelProvider
import com.example.goodsapp.ui.navigation.NavigationDestination
import com.example.goodsapp.ui.theme.InventoryTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.util.Currency
import java.util.Locale

object ItemEntryDestination : NavigationDestination {
    override val route = "item_entry"
    override val titleRes = R.string.item_entry_title
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ItemEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SecurityViewModel
) {
    var entryCheck by rememberSaveable{ mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val resultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts
        .StartActivityForResult()){result->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also {uri ->
                val cacheFile = File(context.cacheDir, "temp.json")

                cacheFile.outputStream().use {output->
                    context.contentResolver.openFileDescriptor(uri,"r")?.use {descriptor->
                        FileInputStream(descriptor.fileDescriptor).use { input->
                            input.copyTo(output)
                            input.close()
                        }
                    }
                    output.close()
                }

                val encryptedFile = settingsViewModel.encryptFile(cacheFile)
                encryptedFile.openFileInput().use {input->
                    val receivedDetails = Json.decodeFromString<ItemDetails>(input.bufferedReader().readText())
                    receivedDetails.type = CreationType.FILE
                    viewModel.updateUiState(
                        receivedDetails
                    )
                    input.close()
                }

                cacheFile.delete()
            }
        }
    }
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                viewModel = settingsViewModel,
                isEntryScreen = true,
                intentResultLauncher = resultLauncher
            )
        }
    ) { innerPadding ->
        ItemEntryBody(
            itemUiState = viewModel.itemUiState.apply {
                if(!entryCheck && settingsViewModel.settingsUiState.settingsDetails.enterDefaults) {
                    entryCheck = true
                    val settingsDetails = settingsViewModel.settingsUiState.settingsDetails
                    viewModel.updateUiState(ItemDetails(
                        vendorName = settingsDetails.vendorName,
                        email = settingsDetails.email,
                        phone = settingsDetails.phone
                    ))
                }
            },
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    navigateBack()
                }
            },
            validators = mapOf("priceValidator" to viewModel::priceValidator,
                "quantityValidator" to viewModel::quantityValidator,
                "nameValidator" to viewModel::nameValidator,
                "emailValidator" to viewModel::emailValidator,
                "phoneValidator" to viewModel::phoneValidator),
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            settingsUiState = settingsViewModel.settingsUiState
        )
    }
}

@Composable
fun ItemEntryBody(
    itemUiState: ItemUiState,
    onItemValueChange: (ItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    validators: Map<String, (String) -> Boolean>,
    settingsUiState: SettingsUiState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        ItemInputForm(
            itemDetails = itemUiState.itemDetails,
            onValueChange = onItemValueChange,
            validators = validators,
            modifier = Modifier.fillMaxWidth(),
            settings = settingsUiState.settingsDetails
        )
        Button(
            onClick = onSaveClick,
            enabled = itemUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}


@Composable
fun ItemInputForm(
    itemDetails: ItemDetails,
    modifier: Modifier = Modifier,
    onValueChange: (ItemDetails) -> Unit = {},
    enabled: Boolean = true,
    validators: Map<String, (String) -> Boolean>,
    settings: SettingsDetails
) {
    var quantityError by rememberSaveable{ mutableStateOf(false) }
    var priceError by rememberSaveable{ mutableStateOf(false) }
    var itemNameError by rememberSaveable{ mutableStateOf(false) }
    var vendorNameError by rememberSaveable{ mutableStateOf(false) }
    var emailError by rememberSaveable{ mutableStateOf(false) }
    var phoneError by rememberSaveable{ mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = itemDetails.name,
            onValueChange = {
                onValueChange(itemDetails.copy(name = it))
                itemNameError = it.isNotBlank() && !validators["nameValidator"]!!.invoke(it)},
            label = { Text(stringResource(R.string.item_name_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = itemNameError,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            supportingText = {
                if(itemNameError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid item name",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        OutlinedTextField(
            value = itemDetails.price,
            onValueChange = {
                onValueChange(itemDetails.copy(price = it))
                priceError = it.isNotBlank() && !validators["priceValidator"]!!.invoke(it)},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.item_price_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = priceError,
            leadingIcon = { Text(Currency.getInstance(Locale.getDefault()).symbol) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            supportingText = {
                if(priceError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid price",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        OutlinedTextField(
            value = itemDetails.quantity,
            onValueChange = {
                onValueChange(itemDetails.copy(quantity = it))
                quantityError = it.isNotBlank() && !validators["quantityValidator"]!!.invoke(it)},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.quantity_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = quantityError,
            singleLine = true,
            supportingText = {
                if(quantityError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid quantity",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        OutlinedTextField(
            value = itemDetails.vendorName,
            onValueChange = {
                onValueChange(itemDetails.copy(vendorName = it))
                vendorNameError = it.isNotBlank() && !validators["nameValidator"]!!.invoke(it)},
            label = { Text(stringResource(R.string.vendor_name_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = vendorNameError,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            supportingText = {
                if(vendorNameError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid vendor name",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        OutlinedTextField(
            value = itemDetails.email,
            onValueChange = {
                onValueChange(itemDetails.copy(email = it))
                emailError = it.isNotBlank() && !validators["emailValidator"]!!.invoke(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            label = { Text(stringResource(R.string.vendor_email_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = emailError,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            supportingText = {
                if(emailError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid email",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        OutlinedTextField(
            value = itemDetails.phone,
            onValueChange = {
                onValueChange(itemDetails.copy(phone = it))
                phoneError = it.isNotBlank() && !validators["phoneValidator"]!!.invoke(it)},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            label = { Text(stringResource(R.string.vendor_phone_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = phoneError,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            supportingText = {
                if(phoneError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid phone",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemEntryScreenPreview() {
    InventoryTheme {
        ItemEntryBody(itemUiState = ItemUiState(
            ItemDetails(
                name = "Item name", price = "10.00", quantity = "5"
            )
        ), onItemValueChange = {}, onSaveClick = {},
            validators = mapOf("priceValidator" to {false},
                "quantityValidator" to {false},
                "nameValidator" to {false},
                "emailValidator" to {false},
                "phoneValidator" to {false}),
            settingsUiState = SettingsUiState())
    }
}