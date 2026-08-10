package com.example.gridrates.ui.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(val state: NavigationState) {
    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            // This is a top level route, just switch to it.
            state.topLevelRoute = route
            // Reset the stack of the target route if it's the start route (Home)
            // or if we want to ensure we start from the base.
            // In our case, if we go to Home, we should probably reset Onboarding stack.
            if (route == GridRatesNavKey.Home) {
                state.backStacks[GridRatesNavKey.OnboardingProvider]?.let { stack ->
                    while (stack.size > 1) {
                        stack.removeLastOrNull()
                    }
                }
            }
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute) {
            if (state.topLevelRoute != state.startRoute) {
                state.topLevelRoute = state.startRoute
            }
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
