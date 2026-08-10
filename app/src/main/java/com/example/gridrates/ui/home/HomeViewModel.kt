package com.example.gridrates.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gridrates.data.local.entity.DayType
import com.example.gridrates.data.local.entity.RatePlan
import com.example.gridrates.data.local.entity.RateSchedule
import com.example.gridrates.data.local.entity.UtilityProvider
import com.example.gridrates.data.repository.RateRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDateTime

class HomeViewModel(
    private val repository: RateRepository
) : ViewModel() {

    val selectedProvider: StateFlow<UtilityProvider?> = repository.selectedProvider
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedPlan: StateFlow<RatePlan?> = repository.selectedPlan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentActiveRate: StateFlow<RateSchedule?> = repository.currentActiveRate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentTime: StateFlow<LocalDateTime> = repository.currentTimeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDateTime.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailySchedules: StateFlow<List<RateSchedule>> = combine(
        repository.selectedPlanId,
        currentTime
    ) { planId, now ->
        planId to now
    }.flatMapLatest { (planId, now) ->
        if (planId != null) {
            val dayType = if (now.dayOfWeek == DayOfWeek.SATURDAY || now.dayOfWeek == DayOfWeek.SUNDAY) {
                DayType.WEEKEND
            } else {
                DayType.WEEKDAY
            }
            repository.getSchedulesForPlan(planId).map { schedules ->
                schedules.filter { it.dayType == dayType }
                    .sortedBy { it.startTime }
            }
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(
        private val repository: RateRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
    }
}
