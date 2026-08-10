package com.example.gridrates.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gridrates.data.repository.RateRepository
import kotlinx.coroutines.launch

class PlanViewModel(
    private val providerId: String,
    private val repository: RateRepository
) : ViewModel() {
    val plans = repository.getPlansForProvider(providerId)

    fun saveSelection(planId: String, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.saveSelection(providerId, planId)
            onSaved()
        }
    }

    class Factory(
        private val providerId: String,
        private val repository: RateRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlanViewModel(providerId, repository) as T
        }
    }
}
