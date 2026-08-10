package com.example.gridrates.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "rate_plans",
    foreignKeys = [
        ForeignKey(
            entity = UtilityProvider::class,
            parentColumns = ["id"],
            childColumns = ["providerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RatePlan(
    @PrimaryKey val id: String,
    val providerId: String,
    val name: String,
    val type: String, // Flat, TOU, etc.
    val description: String
)
