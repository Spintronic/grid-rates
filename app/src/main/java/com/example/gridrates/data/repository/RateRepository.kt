package com.example.gridrates.data.repository

import com.example.gridrates.data.local.UserPreferences
import com.example.gridrates.data.local.dao.RateDao
import com.example.gridrates.data.local.entity.DayType
import com.example.gridrates.data.local.entity.RatePlan
import com.example.gridrates.data.local.entity.RateSchedule
import com.example.gridrates.data.local.entity.UtilityProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.Month

class RateRepository(
    private val rateDao: RateDao,
    private val userPreferences: UserPreferences
) {
    val allProviders: Flow<List<UtilityProvider>> = rateDao.getAllProviders()

    fun getPlansForProvider(providerId: String): Flow<List<RatePlan>> = 
        rateDao.getPlansForProvider(providerId)

    val selectedProviderId: Flow<String?> = userPreferences.providerId
    val selectedPlanId: Flow<String?> = userPreferences.planId

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPlan: Flow<RatePlan?> = selectedPlanId.flatMapLatest { planId ->
        if (planId != null) {
            rateDao.getPlanById(planId)
        } else {
            flow { emit(null) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedProvider: Flow<UtilityProvider?> = selectedProviderId.flatMapLatest { providerId ->
        if (providerId != null) {
            rateDao.getProviderById(providerId)
        } else {
            flow { emit(null) }
        }
    }

    fun getSchedulesForPlan(planId: String): Flow<List<RateSchedule>> =
        rateDao.getSchedulesForPlan(planId)

    suspend fun saveSelection(providerId: String, planId: String) {
        userPreferences.saveSelection(providerId, planId)
    }

    val currentTimeFlow: Flow<LocalDateTime> = flow {
        while (true) {
            emit(LocalDateTime.now())
            delay(60_000)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentActiveRate: Flow<RateSchedule?> = combine(
        selectedPlanId,
        currentTimeFlow
    ) { planId, now ->
        planId to now
    }.distinctUntilChanged().flatMapLatest { (planId, now) ->
        if (planId != null) {
            val dayType = if (now.dayOfWeek == DayOfWeek.SATURDAY || now.dayOfWeek == DayOfWeek.SUNDAY) {
                DayType.WEEKEND
            } else {
                DayType.WEEKDAY
            }
            val activeSchedule = rateDao.getActiveSchedule(planId, dayType, now.toLocalTime())
            if (planId in setOf("GA_GAPOWER_TOUPEV12", "GA_GAPOWER_NIGHTS_WEEKENDS")) {
                flow {
                    val schedules = rateDao.getSchedulesForPlan(planId).first()
                    val effectiveSchedule = schedules.firstOrNull { it.dayType == dayType && it.startTime <= now.toLocalTime() && it.endTime > now.toLocalTime() }
                    emit(resolveScheduleForSeasonalRules(planId, effectiveSchedule, now, schedules))
                }
            } else {
                activeSchedule
            }
        } else {
            flow { emit(null) }
        }
    }

    companion object {
        internal fun resolveScheduleForSeasonalRules(
            planId: String,
            candidate: RateSchedule?,
            now: LocalDateTime,
            matchingSchedules: List<RateSchedule>
        ): RateSchedule? {
            if (planId !in setOf("GA_GAPOWER_TOUPEV12", "GA_GAPOWER_NIGHTS_WEEKENDS")) return candidate

            val isSummer = now.month in setOf(Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER)
            val isOnPeak = candidate?.label == "On-Peak"
            val isOffPeak = candidate?.label == "Off-Peak"

            return when {
                isSummer && isOnPeak -> candidate
                isSummer && isOffPeak -> candidate
                !isSummer && isOnPeak -> matchingSchedules.firstOrNull { it.label == "Off-Peak" && it.dayType == candidate?.dayType && it.startTime == candidate.startTime && it.endTime == candidate.endTime }
                else -> candidate
            }
        }
    }
}
