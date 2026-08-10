package com.example.gridrates.data.local

import androidx.room.TypeConverter
import com.example.gridrates.data.local.entity.DayType
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromDayType(value: DayType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toDayType(value: String?): DayType? {
        return value?.let { DayType.valueOf(it) }
    }
}
