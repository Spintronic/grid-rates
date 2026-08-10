package com.example.gridrates.data.repository

import com.example.gridrates.data.local.entity.DayType
import com.example.gridrates.data.local.entity.RateSchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime

class RateRepositoryTest {

    @Test
    fun gaPowerOnPeakIsOnlyActiveDuringSummerMonths() {
        val planIds = listOf("GA_GAPOWER_TOUPEV12", "GA_GAPOWER_NIGHTS_WEEKENDS")

        val candidateTemplate = RateSchedule(
            ratePlanId = "",
            dayType = DayType.WEEKDAY,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(19, 0),
            rate = 0.30,
            label = "On-Peak"
        )

        val fallbackTemplate = RateSchedule(
            ratePlanId = "",
            dayType = DayType.WEEKDAY,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(19, 0),
            rate = 0.10,
            label = "Off-Peak"
        )

        val winter = LocalDateTime.of(2025, 1, 15, 15, 0)
        val summer = LocalDateTime.of(2025, 7, 15, 15, 0)

        planIds.forEach { planId ->
            val candidate = candidateTemplate.copy(ratePlanId = planId)
            val fallback = fallbackTemplate.copy(ratePlanId = planId)

            val winterResolved = RateRepository.resolveScheduleForSeasonalRules(
                planId = planId,
                candidate = candidate,
                now = winter,
                matchingSchedules = listOf(candidate, fallback)
            )

            val summerResolved = RateRepository.resolveScheduleForSeasonalRules(
                planId = planId,
                candidate = candidate,
                now = summer,
                matchingSchedules = listOf(candidate, fallback)
            )

            assertEquals("Off-Peak", winterResolved?.label)
            assertEquals("On-Peak", summerResolved?.label)
            assertNotEquals(candidate.rate, winterResolved?.rate)
        }
    }
}
