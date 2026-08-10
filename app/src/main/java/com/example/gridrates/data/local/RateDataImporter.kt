package com.example.gridrates.data.local

import com.example.gridrates.data.local.dao.RateDao
import com.example.gridrates.data.local.entity.DayType
import com.example.gridrates.data.local.entity.RatePlan
import com.example.gridrates.data.local.entity.RateSchedule
import com.example.gridrates.data.local.entity.UtilityProvider
import java.time.LocalTime

object RateDataImporter {
    suspend fun populate(dao: RateDao) {
        val providers = listOf(
            UtilityProvider("pge", "Pacific Gas & Electric (PG&E)", "California"),
            UtilityProvider("txu", "TXU Energy", "Texas"),
            UtilityProvider("fpl", "Florida Power & Light (FPL)", "Florida"),
            UtilityProvider("gapower", "Georgia Power", "Georgia")
        )
        dao.insertProviders(providers)

        val plans = listOf(
            RatePlan("CA_PGE_EV2A", "pge", "Home Charging EV2-A"),
            RatePlan("CA_PGE_EVB", "pge", "Electric Vehicle Rate Plan EV-B"),
            RatePlan("TX_TXU_FREENIGHTS", "txu", "Free Nights & Solar Days"),
            RatePlan("FL_FPL_RTR1", "fpl", "Residential Time-of-Use Rider (RTR-1)"),
            RatePlan("FL_FPL_EVOLUTION", "fpl", "FPL EVolution Home"),
            RatePlan("GA_GAPOWER_TOUPEV12", "gapower", "Overnight Advantage"),
            RatePlan("GA_GAPOWER_NIGHTS_WEEKENDS", "gapower", "Nights & Weekends")
        )
        dao.insertPlans(plans)

        val schedules = mutableListOf<RateSchedule>()

        // CA_PGE_EV2A
        addDailySchedule(schedules, "CA_PGE_EV2A", LocalTime.MIDNIGHT, LocalTime.of(15, 0), 0.23, "Off-Peak")
        addDailySchedule(schedules, "CA_PGE_EV2A", LocalTime.of(15, 0), LocalTime.of(16, 0), 0.43, "Partial-Peak")
        addDailySchedule(schedules, "CA_PGE_EV2A", LocalTime.of(16, 0), LocalTime.of(21, 0), 0.54, "Peak")
        addDailySchedule(schedules, "CA_PGE_EV2A", LocalTime.of(21, 0), LocalTime.MAX, 0.43, "Partial-Peak")

        // CA_PGE_EVB
        addDailySchedule(schedules, "CA_PGE_EVB", LocalTime.MIDNIGHT, LocalTime.of(7, 0), 0.26, "Off-Peak")
        addDailySchedule(schedules, "CA_PGE_EVB", LocalTime.of(7, 0), LocalTime.of(14, 0), 0.38, "Mid-day")
        addDailySchedule(schedules, "CA_PGE_EVB", LocalTime.of(14, 0), LocalTime.of(23, 0), 0.62, "Peak")
        addDailySchedule(schedules, "CA_PGE_EVB", LocalTime.of(23, 0), LocalTime.MAX, 0.26, "Off-Peak")

        // TX_TXU_FREENIGHTS
        addDailySchedule(schedules, "TX_TXU_FREENIGHTS", LocalTime.MIDNIGHT, LocalTime.of(6, 0), 0.00, "Free/Night")
        addDailySchedule(schedules, "TX_TXU_FREENIGHTS", LocalTime.of(6, 0), LocalTime.of(21, 0), 0.24, "Day")
        addDailySchedule(schedules, "TX_TXU_FREENIGHTS", LocalTime.of(21, 0), LocalTime.MAX, 0.00, "Free/Night")

        // FL_FPL_RTR1 (Summer proxy)
        addDailySchedule(schedules, "FL_FPL_RTR1", LocalTime.MIDNIGHT, LocalTime.of(12, 0), 0.09, "Off-Peak")
        addDailySchedule(schedules, "FL_FPL_RTR1", LocalTime.of(12, 0), LocalTime.of(21, 0), 0.26, "On-Peak")
        addDailySchedule(schedules, "FL_FPL_RTR1", LocalTime.of(21, 0), LocalTime.MAX, 0.09, "Off-Peak")

        // GA_GAPOWER_TOUPEV12
        addDailySchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.MIDNIGHT, LocalTime.of(7, 0), 0.02, "Super Off-Peak")
        addWeekdaySchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.of(7, 0), LocalTime.of(14, 0), 0.10, "Off-Peak")
        addWeekdaySchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.of(14, 0), LocalTime.of(19, 0), 0.30, "On-Peak")
        addWeekdaySchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.of(19, 0), LocalTime.of(23, 0), 0.10, "Off-Peak")
        addDailySchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.of(23, 0), LocalTime.MAX, 0.02, "Super Off-Peak")
        addWeekendSchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.of(7, 0), LocalTime.of(23, 0), 0.10, "Off-Peak")

        // GA_GAPOWER_NIGHTS_WEEKENDS
        addWeekdaySchedule(schedules, "GA_GAPOWER_NIGHTS_WEEKENDS", LocalTime.MIDNIGHT, LocalTime.of(14, 0), 0.078, "Off-Peak")
        addWeekdaySchedule(schedules, "GA_GAPOWER_NIGHTS_WEEKENDS", LocalTime.of(14, 0), LocalTime.of(19, 0), 0.303, "On-Peak")
        addWeekdaySchedule(schedules, "GA_GAPOWER_NIGHTS_WEEKENDS", LocalTime.of(19, 0), LocalTime.MAX, 0.078, "Off-Peak")
        addWeekendSchedule(schedules, "GA_GAPOWER_NIGHTS_WEEKENDS", LocalTime.MIDNIGHT, LocalTime.MAX, 0.078, "Off-Peak")

        dao.insertSchedules(schedules)
    }

    private fun addDailySchedule(schedules: MutableList<RateSchedule>, planId: String, start: LocalTime, end: LocalTime, rate: Double, label: String) {
        addWeekdaySchedule(schedules, planId, start, end, rate, label)
        addWeekendSchedule(schedules, planId, start, end, rate, label)
    }

    private fun addWeekdaySchedule(schedules: MutableList<RateSchedule>, planId: String, start: LocalTime, end: LocalTime, rate: Double, label: String) {
        schedules.add(RateSchedule(ratePlanId = planId, dayType = DayType.WEEKDAY, startTime = start, endTime = end, rate = rate, label = label))
    }

    private fun addWeekendSchedule(schedules: MutableList<RateSchedule>, planId: String, start: LocalTime, end: LocalTime, rate: Double, label: String) {
        schedules.add(RateSchedule(ratePlanId = planId, dayType = DayType.WEEKEND, startTime = start, endTime = end, rate = rate, label = label))
    }
}
