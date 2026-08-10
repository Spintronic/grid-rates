package com.example.gridrates.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gridrates.data.repository.RateRepository

class ProviderViewModel(
    private val repository: RateRepository
) : ViewModel() {
    val providers = repository.allProviders

    class Factory(
        private val repository: RateRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProviderViewModel(repository) as T
        }
    }
}
