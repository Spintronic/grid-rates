package com.example.gridrates.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gridrates.data.repository.RateRepository
import com.example.gridrates.ui.navigation.GridRatesNavKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RootViewModel(
    private val repository: RateRepository
) : ViewModel() {
    val startDestination: StateFlow<GridRatesNavKey?> = repository.selectedPlanId
        .map { planId ->
            if (planId != null) GridRatesNavKey.Home else GridRatesNavKey.OnboardingProvider
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    class Factory(
        private val repository: RateRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RootViewModel(repository) as T
        }
    }
}
