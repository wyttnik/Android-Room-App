package com.example.goodsapp.security

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.UUID

object MasterFiles {
    lateinit var encryptedSharedPreferences: SharedPreferences
    lateinit var mainKey: MasterKey

    lateinit var dbKey: MasterKey
    lateinit var dbSharedPreferences: SharedPreferences

    fun initialize(app: Application) {
        mainKey = MasterKey.Builder(app).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        encryptedSharedPreferences = EncryptedSharedPreferences.create(
            app,
            "secret_shared_prefs",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        dbKey = MasterKey.Builder(app, "database_key").setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        dbSharedPreferences = EncryptedSharedPreferences.create(
            app,
            "db_prefs",
            dbKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        defaultSettingValues()
    }

    private fun defaultSettingValues() {
//        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
//        val secretKeyEntry = ks.getEntry("database_key", null) as KeyStore.SecretKeyEntry
//        val secretKey = secretKeyEntry.secretKey

        if (encryptedSharedPreferences.contains("vendorName_default")) return

        dbSharedPreferences.edit().putString("db_password",
            UUID.randomUUID().toString()).apply()

        val editor = encryptedSharedPreferences.edit()

        editor.putString("vendorName_default", "Default Name").apply()
        editor.putString("email_default", "example@ex.com").apply()
        editor.putString("phone_default", "+71234567890").apply()
        editor.putBoolean("enter_defaults", false).apply()
        editor.putBoolean("hide_info", false).apply()
        editor.putBoolean("restrict_share", false).apply()
    }

}