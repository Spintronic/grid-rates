package com.example.gridrates.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "utility_providers")
data class UtilityProvider(
    @PrimaryKey val id: String,
    val name: String,
    val region: String
)
