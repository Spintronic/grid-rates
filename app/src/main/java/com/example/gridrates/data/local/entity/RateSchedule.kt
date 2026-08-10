package com.example.gridrates.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    tableName = "rate_schedules",
    foreignKeys = [
        ForeignKey(
            entity = RatePlan::class,
            parentColumns = ["id"],
            childColumns = ["ratePlanId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RateSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ratePlanId: String,
    val dayType: DayType,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val rate: Double,
    val label: String // Peak, Off-Peak, etc.
)

enum class DayType {
    WEEKDAY, WEEKEND
}
