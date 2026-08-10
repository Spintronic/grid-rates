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
            UtilityProvider("coned", "Consolidated Edison (Con Edison)", "New York"),
            UtilityProvider("peco", "PECO Energy", "Pennsylvania"),
            UtilityProvider("comed", "Commonwealth Edison (ComEd)", "Illinois"),
            UtilityProvider("aep_ohio", "AEP Ohio", "Ohio"),
            UtilityProvider("gapower", "Georgia Power", "Georgia"),
            UtilityProvider("duke", "Duke Energy", "North Carolina"),
            UtilityProvider("dte", "DTE Energy", "Michigan")
        )
        dao.insertProviders(providers)

        val plans = listOf(
            RatePlan("CA_PGE_EV2A", "pge", "Home Charging EV2-A", "Whole-home TOU", "Time-of-Use for whole-home including EV charging"),
            RatePlan("CA_PGE_EVB", "pge", "Electric Vehicle Rate Plan EV-B", "EV-only", "Requires second dedicated meter for EV"),
            RatePlan("TX_TXU_FREENIGHTS", "txu", "Free Nights & Solar Days", "TOU", "Free energy during night hours"),
            RatePlan("FL_FPL_RTR1", "fpl", "Residential Time-of-Use Rider (RTR-1)", "Whole-home TOU", "Standard residential TOU option"),
            RatePlan("FL_FPL_EVOLUTION", "fpl", "FPL EVolution Home", "Managed EV charging", "Includes dedicated L2 charger and off-peak rate"),
            RatePlan("NY_CONED_SMARTCHARGE", "coned", "SmartCharge New York", "Off-peak Incentive", "Rebate for off-peak charging"),
            RatePlan("PA_PECO_TOU", "peco", "Residential Time-of-Use Pricing", "Whole-home TOU", "Best for usage outside 2-6pm weekdays"),
            RatePlan("IL_COMED_DTOD", "comed", "Delivery Time-of-Day (DTOD)", "Delivery TOU", "TOU pricing for delivery charges"),
            RatePlan("IL_COMED_HOURLY", "comed", "Hourly Pricing", "Real-time", "Supply price varies every hour"),
            RatePlan("OH_AEPOHIO_PEV", "aep_ohio", "Plug-In Electric Vehicle (PEV) Tariff", "TOU", "Separately-metered or whole-home TOU"),
            RatePlan("GA_GAPOWER_TOUPEV12", "gapower", "Time of Use - PEV", "EV-only", "Requires second dedicated meter"),
            RatePlan("NC_DUKE_RSTOU", "duke", "Residential Service TOU", "Whole-home TOU", "Standard Duke Energy TOU option"),
            RatePlan("MI_DTE_TOD", "dte", "Time of Day 3 p.m.-7 p.m.", "Whole-home TOU", "Default DTE residential rate")
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

        // PA_PECO_TOU
        addWeekdaySchedule(schedules, "PA_PECO_TOU", LocalTime.MIDNIGHT, LocalTime.of(6, 0), 0.076, "Off-Peak")
        addWeekdaySchedule(schedules, "PA_PECO_TOU", LocalTime.of(6, 0), LocalTime.of(14, 0), 0.076, "Off-Peak")
        addWeekdaySchedule(schedules, "PA_PECO_TOU", LocalTime.of(14, 0), LocalTime.of(18, 0), 0.32, "Peak")
        addWeekdaySchedule(schedules, "PA_PECO_TOU", LocalTime.of(18, 0), LocalTime.MAX, 0.076, "Off-Peak")
        addWeekendSchedule(schedules, "PA_PECO_TOU", LocalTime.MIDNIGHT, LocalTime.MAX, 0.076, "Off-Peak")

        // GA_GAPOWER_TOUPEV12
        addDailySchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.MIDNIGHT, LocalTime.of(7, 0), 0.02, "Super Off-Peak")
        addWeekdaySchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.of(7, 0), LocalTime.of(14, 0), 0.07, "Off-Peak")
        addWeekdaySchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.of(14, 0), LocalTime.of(19, 0), 0.25, "On-Peak")
        addWeekdaySchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.of(19, 0), LocalTime.of(23, 0), 0.07, "Off-Peak")
        addDailySchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.of(23, 0), LocalTime.MAX, 0.02, "Super Off-Peak")
        addWeekendSchedule(schedules, "GA_GAPOWER_TOUPEV12", LocalTime.of(7, 0), LocalTime.of(23, 0), 0.07, "Off-Peak")

        // MI_DTE_TOD
        addDailySchedule(schedules, "MI_DTE_TOD", LocalTime.MIDNIGHT, LocalTime.of(15, 0), 0.18, "Off-Peak")
        addWeekdaySchedule(schedules, "MI_DTE_TOD", LocalTime.of(15, 0), LocalTime.of(19, 0), 0.24, "On-Peak")
        addDailySchedule(schedules, "MI_DTE_TOD", LocalTime.of(19, 0), LocalTime.MAX, 0.18, "Off-Peak")

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
