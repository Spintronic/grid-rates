package com.example.gridrates.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gridrates.data.local.entity.DayType
import com.example.gridrates.data.local.entity.RatePlan
import com.example.gridrates.data.local.entity.RateSchedule
import com.example.gridrates.data.local.entity.UtilityProvider
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

@Dao
interface RateDao {
    @Query("SELECT * FROM utility_providers")
    fun getAllProviders(): Flow<List<UtilityProvider>>

    @Query("SELECT * FROM rate_plans WHERE providerId = :providerId")
    fun getPlansForProvider(providerId: String): Flow<List<RatePlan>>

    @Query("SELECT * FROM rate_schedules WHERE ratePlanId = :planId")
    fun getSchedulesForPlan(planId: String): Flow<List<RateSchedule>>

    @Query("SELECT * FROM utility_providers WHERE id = :id")
    fun getProviderById(id: String): Flow<UtilityProvider?>

    @Query("SELECT * FROM rate_plans WHERE id = :id")
    fun getPlanById(id: String): Flow<RatePlan?>

    @Query("""
        SELECT * FROM rate_schedules 
        WHERE ratePlanId = :planId 
        AND dayType = :dayType 
        AND startTime <= :time 
        AND endTime > :time
        LIMIT 1
    """)
    fun getActiveSchedule(planId: String, dayType: DayType, time: LocalTime): Flow<RateSchedule?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<UtilityProvider>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<RatePlan>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<RateSchedule>)
}
