package com.example.gridrates.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.gridrates.GridRatesApplication
import com.example.gridrates.ui.home.HomeScreen
import com.example.gridrates.ui.home.HomeViewModel
import com.example.gridrates.ui.onboarding.PlanSelectionScreen
import com.example.gridrates.ui.onboarding.PlanViewModel
import com.example.gridrates.ui.onboarding.ProviderSelectionScreen
import com.example.gridrates.ui.onboarding.ProviderViewModel

@Composable
fun GridRatesNavigation(
    startRoute: GridRatesNavKey,
    application: GridRatesApplication
) {
    val navigationState = rememberNavigationState(
        startRoute = startRoute,
        topLevelRoutes = setOf(GridRatesNavKey.Home, GridRatesNavKey.OnboardingProvider)
    )

    val navigator = remember { Navigator(navigationState) }

    val entryProvider = entryProvider<NavKey> {
        entry<GridRatesNavKey.OnboardingProvider> {
            val viewModel: ProviderViewModel = viewModel(
                factory = ProviderViewModel.Factory(application.repository)
            )
            ProviderSelectionScreen(
                viewModel = viewModel,
                onProviderSelected = { providerId ->
                    navigator.navigate(GridRatesNavKey.OnboardingPlan(providerId))
                },
                onBack = if (navigationState.topLevelRoute != startRoute) {
                    { navigator.goBack() }
                } else null
            )
        }

        entry<GridRatesNavKey.OnboardingPlan> { key ->
            val viewModel: PlanViewModel = viewModel(
                factory = PlanViewModel.Factory(key.providerId, application.repository)
            )
            PlanSelectionScreen(
                viewModel = viewModel,
                onPlanSelected = { planId ->
                    viewModel.saveSelection(planId) {
                        navigator.navigate(GridRatesNavKey.Home)
                    }
                },
                onBack = { navigator.goBack() }
            )
        }

        entry<GridRatesNavKey.Home> {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(application.repository)
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSettings = {
                    navigator.navigate(GridRatesNavKey.OnboardingProvider)
                }
            )
        }
    }

    NavDisplay(
        entries = navigationState.toEntries(entryProvider),
        onBack = { navigator.goBack() }
    )
}
