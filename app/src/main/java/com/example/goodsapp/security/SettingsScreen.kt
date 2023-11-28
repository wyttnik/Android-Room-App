package com.example.goodsapp.security

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.goodsapp.InventoryTopAppBar
import com.example.goodsapp.R
import com.example.goodsapp.ui.navigation.NavigationDestination

object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: SecurityViewModel
) {
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                viewModel = viewModel,
                isSettingsScreen = true
            )
        }
    ) { innerPadding ->
        SettingsBody(
            settingsUiState = viewModel.settingsUiState,
            onSettingValueChange = viewModel::updateSettingsUiState,
            onSaveClicks = mapOf(
                "saveEnterDefaults" to viewModel::saveEnterDefaults,
                "saveHideInfo" to viewModel::saveHideInfo,
                "saveRestrictShare" to viewModel::saveRestrictShare
            ),
            validators = mapOf(
                "nameValidator" to viewModel::nameValidator,
                "emailValidator" to viewModel::emailValidator,
                "phoneValidator" to viewModel::phoneValidator
            ),
            onSaveClick = viewModel::saveInputs,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            navigateBack = navigateBack,
            updateUiState = viewModel::putEncryptedSettings
        )
    }
}

@Composable
fun SettingsBody(
    settingsUiState: SettingsUiState,
    onSettingValueChange: (SettingsDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSaveClicks: Map<String, () -> Unit>,
    validators: Map<String, (String) -> Boolean>,
    navigateBack: () -> Unit,
    updateUiState: () -> Unit
) {
    val localContext = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        InputsForm(
            settingsDetails = settingsUiState.settingsDetails,
            onValueChange = onSettingValueChange,
            validators = validators,
            modifier = Modifier.fillMaxWidth()
        )
        BackHandler(onBack = {
            updateUiState()
            navigateBack()
        })
        Button(
            onClick = {
                onSaveClick()
                Toast.makeText(localContext, "Default values were saved!", Toast.LENGTH_SHORT).show()
            },
            enabled = settingsUiState.areSettingsValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save default values")
        }
        Divider()
        SwitchForm(
            settingsDetails = settingsUiState.settingsDetails,
            onValueChange = onSettingValueChange,
            onSaveClicks = onSaveClicks,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun InputsForm(settingsDetails: SettingsDetails,
                 modifier: Modifier = Modifier,
                 onValueChange: (SettingsDetails) -> Unit = {},
                 validators: Map<String, (String) -> Boolean>
){
    var vendorNameError by rememberSaveable{ mutableStateOf(false) }
    var emailError by rememberSaveable{ mutableStateOf(false) }
    var phoneError by rememberSaveable{ mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        TextField(
            value = settingsDetails.vendorName,
            onValueChange = {
                onValueChange(settingsDetails.copy(vendorName = it))
                vendorNameError = it.isNotBlank() && !validators["nameValidator"]!!.invoke(it)
            },
            label = { Text(stringResource(R.string.vendor_name_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = vendorNameError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (vendorNameError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid vendor name",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        TextField(
            value = settingsDetails.email,
            onValueChange = {
                onValueChange(settingsDetails.copy(email = it))
                emailError = it.isNotBlank() && !validators["emailValidator"]!!.invoke(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            label = { Text(stringResource(R.string.vendor_email_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = emailError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (emailError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid email",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        TextField(
            value = settingsDetails.phone,
            onValueChange = {
                onValueChange(settingsDetails.copy(phone = it))
                phoneError = it.isNotBlank() && !validators["phoneValidator"]!!.invoke(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            label = { Text(stringResource(R.string.vendor_phone_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = phoneError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (phoneError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid phone",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}

@Composable
fun SwitchForm(
    settingsDetails: SettingsDetails,
    modifier: Modifier = Modifier,
    onValueChange: (SettingsDetails) -> Unit = {},
    onSaveClicks: Map<String, () -> Unit>
){
    Column(
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically){
            Text(text = "Enter defaults?", style = TextStyle(fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif)
            )
            Spacer(Modifier.weight(1f))
            Switch(
                checked = settingsDetails.enterDefaults,
                onCheckedChange = {
                    onValueChange(settingsDetails.copy(enterDefaults = it))
                    onSaveClicks["saveEnterDefaults"]!!.invoke()
                }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically){
            Text(text = "Hide vendor info?", style = TextStyle(fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif)
            )
            Spacer(Modifier.weight(1f))
            Switch(
                checked = settingsDetails.hideInfo,
                onCheckedChange = {
                    onValueChange(settingsDetails.copy(hideInfo = it))
                    onSaveClicks["saveHideInfo"]!!.invoke()
                }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically){
            Text(text = "Restrict info share?", style = TextStyle(fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif)
            )
            Spacer(Modifier.weight(1f))
            Switch(
                checked = settingsDetails.restrictShare,
                onCheckedChange = {
                    onValueChange(settingsDetails.copy(restrictShare = it))
                    onSaveClicks["saveRestrictShare"]!!.invoke()
                }
            )
        }
    }
}