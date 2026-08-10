package com.example.gridrates.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface GridRatesNavKey : NavKey {
    @Serializable
    data object Root : GridRatesNavKey

    @Serializable
    data object OnboardingProvider : GridRatesNavKey

    @Serializable
    data class OnboardingPlan(val providerId: String) : GridRatesNavKey

    @Serializable
    data object Home : GridRatesNavKey
}
