package com.example.goodsapp.security

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.File

class SecurityViewModel(private val application: Application): AndroidViewModel(application) {
    private val mainKey = MasterKey.Builder(application).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        application,
        "secret_shared_prefs",
        mainKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    init {
        defaultSettingValues()
    }

    var settingsUiState by mutableStateOf(SettingsUiState(SettingsDetails(
        vendorName = encryptedSharedPreferences.getString("vendorName_default", "No name")!!,
        email = encryptedSharedPreferences.getString("email_default", "example@ex.com")!!,
        phone = encryptedSharedPreferences.getString("phone_default", "+71234567890")!!,
        enterDefaults = encryptedSharedPreferences.getBoolean("enter_defaults", false),
        hideInfo = encryptedSharedPreferences.getBoolean("hide_info", false),
        restrictShare = encryptedSharedPreferences.getBoolean("restrict_share", false)),
        true))
        private set

    private fun defaultSettingValues() {
        if (encryptedSharedPreferences.contains("vendorName_default")) return

        val editor = encryptedSharedPreferences.edit()

        editor.putString("vendorName_default", "Default Name").apply()
        editor.putString("email_default", "example@ex.com").apply()
        editor.putString("phone_default", "+71234567890").apply()
        editor.putBoolean("enter_defaults", false).apply()
        editor.putBoolean("hide_info", false).apply()
        editor.putBoolean("restrict_share", false).apply()
    }

    fun encryptFile(file: File) = EncryptedFile.Builder(
        application,
        file,
        mainKey,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()

    fun updateSettingsUiState(settingsDetails: SettingsDetails) {
        settingsUiState =
            SettingsUiState(SettingsDetails(
                vendorName = settingsDetails.vendorName,
                email = settingsDetails.email,
                phone = settingsDetails.phone,
                enterDefaults = settingsDetails.enterDefaults,
                hideInfo = settingsDetails.hideInfo,
                restrictShare = settingsDetails.restrictShare),
                validateInput(settingsDetails)
            )
    }

    private fun validateInput(uiState: SettingsDetails = settingsUiState.settingsDetails): Boolean {
        return with(uiState) {
            nameValidator(vendorName) && emailValidator(email) && phoneValidator(phone)
        }
    }

    fun saveInputs() {
        encryptedSharedPreferences.edit().putString("vendorName_default",settingsUiState.settingsDetails.vendorName).apply()
        encryptedSharedPreferences.edit().putString("email_default",settingsUiState.settingsDetails.email).apply()
        encryptedSharedPreferences.edit().putString("phone_default",settingsUiState.settingsDetails.phone).apply()
    }

    fun saveEnterDefaults() {
        encryptedSharedPreferences.edit().putBoolean("enter_defaults", settingsUiState.settingsDetails.enterDefaults).apply()
    }

    fun saveHideInfo() {
        encryptedSharedPreferences.edit().putBoolean("hide_info", settingsUiState.settingsDetails.hideInfo).apply()
    }

    fun saveRestrictShare() {
        encryptedSharedPreferences.edit().putBoolean("restrict_share", settingsUiState.settingsDetails.restrictShare).apply()
    }

    fun putEncryptedSettings() {
        settingsUiState = SettingsUiState(SettingsDetails(
            vendorName = encryptedSharedPreferences.getString("vendorName_default", "No name")!!,
            email = encryptedSharedPreferences.getString("email_default", "example@ex.com")!!,
            phone = encryptedSharedPreferences.getString("phone_default", "+71234567890")!!,
            enterDefaults = encryptedSharedPreferences.getBoolean("enter_defaults", false),
            hideInfo = encryptedSharedPreferences.getBoolean("hide_info", false),
            restrictShare = encryptedSharedPreferences.getBoolean("restrict_share", false)),
            true
        )
    }

    fun nameValidator(name: String) = name.length > 2

    fun emailValidator(email: String) = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun phoneValidator(phone:String) = Regex("\\+\\d{11}").matches(phone)
}

data class SettingsDetails(
    val vendorName: String = "",
    val email: String = "",
    val phone: String = "",
    val enterDefaults:Boolean = false,
    val hideInfo:Boolean = false,
    val restrictShare:Boolean = false
)


data class SettingsUiState(
    val settingsDetails: SettingsDetails = SettingsDetails(),
    val areSettingsValid: Boolean = false
)