# Implementation Plan - Navigation and Onboarding

Set up Navigation 3 and implement the onboarding flow for provider and plan selection.

## Proposed Changes

### Navigation

#### [NEW] [NavKey.kt](file:///C:/Users/mason/AndroidStudioProjects/GridRates/app/src/main/java/com/example/gridrates/ui/navigation/NavKey.kt)
Defines the serializable navigation keys for the app: `Splash`, `OnboardingProvider`, `OnboardingPlan`, and `Home`.

#### [NEW] [GridRatesNavigation.kt](file:///C:/Users/mason/AndroidStudioProjects/GridRates/app/src/main/java/com/example/gridrates/ui/navigation/GridRatesNavigation.kt)
Implements the `NavDisplay` and navigation logic, including the `NavBackStack` management.

### ViewModels

#### [NEW] [RootViewModel.kt](file:///C:/Users/mason/AndroidStudioProjects/GridRates/app/src/main/java/com/example/gridrates/ui/RootViewModel.kt)
Handles the initial routing logic by checking `UserPreferences`.

#### [NEW] [ProviderViewModel.kt](file:///C:/Users/mason/AndroidStudioProjects/GridRates/app/src/main/java/com/example/gridrates/ui/onboarding/ProviderViewModel.kt)
Manages the state for the utility provider selection screen.

#### [NEW] [PlanViewModel.kt](file:///C:/Users/mason/AndroidStudioProjects/GridRates/app/src/main/java/com/example/gridrates/ui/onboarding/PlanViewModel.kt)
Manages the state for the rate plan selection screen.

### UI Screens

#### [NEW] [ProviderSelectionScreen.kt](file:///C:/Users/mason/AndroidStudioProjects/GridRates/app/src/main/java/com/example/gridrates/ui/onboarding/ProviderSelectionScreen.kt)
Composables for listing and selecting utility providers.

#### [NEW] [PlanSelectionScreen.kt](file:///C:/Users/mason/AndroidStudioProjects/GridRates/app/src/main/java/com/example/gridrates/ui/onboarding/PlanSelectionScreen.kt)
Composables for listing and selecting rate plans.

#### [NEW] [HomeScreen.kt](file:///C:/Users/mason/AndroidStudioProjects/GridRates/app/src/main/java/com/example/gridrates/ui/home/HomeScreen.kt)
A placeholder home screen to navigate to after onboarding.

### MainActivity

#### [MODIFY] [MainActivity.kt](file:///C:/Users/mason/AndroidStudioProjects/GridRates/app/src/main/java/com/example/gridrates/MainActivity.kt)
Integrates `GridRatesNavigation` into the content.

## Verification Plan

### Automated Tests
- Unit tests for `RootViewModel` to verify routing logic.
- Unit tests for `ProviderViewModel` and `PlanViewModel`.

### Manual Verification
- Run the app on an emulator.
- Verify the splash screen redirects to onboarding if no selection is made.
- Verify provider and plan selection saves to DataStore.
- Verify the app launches to the Home screen after selection is saved.
