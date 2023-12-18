package com.example.goodsapp.security

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.security.crypto.EncryptedFile
import com.example.goodsapp.security.MasterFiles.encryptedSharedPreferences
import java.io.File

class SecurityViewModel(private val application: Application): AndroidViewModel(application) {

//    val advancedSpec = KeyGenParameterSpec.Builder("test_and_del_key",
//        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).apply {
//        setBlockModes(KeyProperties.BLOCK_MODE_GCM)
//        setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
//        setKeySize(256)
//        setUserAuthenticationRequired(true)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            setUserAuthenticationParameters(15, KeyProperties.AUTH_DEVICE_CREDENTIAL
//                    or KeyProperties.AUTH_BIOMETRIC_STRONG)
//            setUnlockedDeviceRequired(true)
//            setIsStrongBoxBacked(true)
//        }
//    }.build()

    var settingsUiState by mutableStateOf(SettingsUiState(SettingsDetails(
        vendorName = encryptedSharedPreferences.getString("vendorName_default", "No name")!!,
        email = encryptedSharedPreferences.getString("email_default", "example@ex.com")!!,
        phone = encryptedSharedPreferences.getString("phone_default", "+71234567890")!!,
        enterDefaults = encryptedSharedPreferences.getBoolean("enter_defaults", false),
        hideInfo = encryptedSharedPreferences.getBoolean("hide_info", false),
        restrictShare = encryptedSharedPreferences.getBoolean("restrict_share", false)),
        true))
        private set

    fun encryptFile(file: File) = EncryptedFile.Builder(
        application,
        file,
        MasterFiles.mainKey,
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