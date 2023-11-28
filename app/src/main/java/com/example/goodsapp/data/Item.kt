package com.example.goodsapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CreationType{
    MANUAL, FILE
}

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val quantity: Int,
    val vendorName: String,
    val email: String,
    val phone: String,
    val type: CreationType
)
