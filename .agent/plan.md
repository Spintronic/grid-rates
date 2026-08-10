# Project Plan

Build a native Android app called Grid Rates that lets a user select their electrical utility provider and rate plan from a local, bundled database, persists that selection across sessions, and then shows a home screen that visualizes "where you are in the day" alongside the currently active electricity rate for their selected plan.

Key Features:
- First-launch onboarding selection for utility provider and rate plan (Room database).
- Persist selection using DataStore.
- Home screen showing current time, current rate ($/kWh), and a daily timeline visualizing rate periods (Peak, Off-Peak, etc.).
- Auto-updates UI as time passes.
- Support for weekday/weekend schedules.
- Ability to change plan/provider from settings/menu.

Tech Stack:
- Kotlin, Jetpack Compose.
- Room (Local database for providers, plans, schedules).
- DataStore (Preferences).
- MVVM Architecture.
- Target SDK: Latest stable, Min SDK: 26.

## Project Brief

# Project Brief: Grid Rates

An Android application designed to help users track and optimize their electricity usage by providing real-time visibility into their utility provider's rate schedules.

## Features
*   **Utility & Plan Onboarding**: A guided setup for users to select their utility provider and rate plan from a bundled local database.
*   **Real-Time Rate Dashboard**: Displays the current electricity price ($/kWh) and identifies the active rate period (Peak, Off-Peak, etc.) at a glance.
*   **Daily Rate Timeline**: A visual 24-hour timeline that shows "where you are in the day" relative to upcoming rate changes and peak periods.
*   **Intelligent Schedule Switching**: Automatically handles transitions between different rate structures for weekdays and weekends, updating the UI in real-time as the day progresses.

## High-Level Technical Stack
*   **Kotlin & Jetpack Compose**: Core language and modern declarative UI framework.
*   **Jetpack Navigation 3**: State-driven navigation for handling app flows and onboarding.
*   **Compose Material Adaptive**: Library for building responsive layouts that adapt to various device form factors.
*   **Room Database**: Local storage for the comprehensive catalog of utility providers, rate plans, and their associated schedules.
*   **Jetpack DataStore**: Persistent storage for user-specific configurations and the selected rate plan.
*   **MVVM Architecture**: Ensures a clean separation of concerns and maintainable codebase using ViewModels, Coroutines, and StateFlow.

## Implementation Steps
**Total Duration:** 30m 24s

### Task_1_DataAndPersistence: Set up the Room database with providers, plans, and schedules, including pre-populated data. Implement DataStore for persisting user selection and a repository to calculate the active rate based on current time.
- **Status:** COMPLETED
- **Updates:** Implemented Room database with pre-populated utility data, DataStore for persistence, and a repository with logic to calculate the active rate dynamically. Base infrastructure (Application class, Manifest) is also set up.
- **Acceptance Criteria:**
  - Room database initialized with bundled data
  - DataStore correctly saves/loads selections
  - Repository logic correctly calculates current rate for weekday/weekend
  - Project builds successfully
- **Duration:** 8m 43s

### Task_2_NavigationAndOnboarding: Set up Navigation 3 and implement the onboarding flow. Create screens for selecting a utility provider and a specific rate plan, ensuring the selection is saved to DataStore and navigates to the Home screen.
- **Status:** COMPLETED
- **Updates:** Integrated Navigation 3 and implemented the onboarding flow (Provider and Plan selection screens). Added a RootViewModel to handle initial routing based on DataStore state. The app now correctly navigates to Home if a plan is already selected. Also enhanced the theme and added an app icon.
- **Acceptance Criteria:**
  - Navigation 3 integrated
  - Provider selection screen functional
  - Plan selection screen functional
  - Successful navigation to Home after selection
- **Duration:** 10m 58s

### Task_3_HomeDashboardAndTimeline: Build the main dashboard using Jetpack Compose and Material Adaptive. Implement a 24-hour timeline visualization that shows rate periods and the user's current position in the day. Add real-time UI updates to reflect time and rate changes.
- **Status:** COMPLETED
- **Updates:** Completed the Home Dashboard and Timeline. The Home screen now displays real-time rate information, provider/plan details, and a 24-hour color-coded timeline visualization. Implemented an adaptive layout for phones and tablets. The UI updates automatically every minute to reflect current time and rate changes.
- **Acceptance Criteria:**
  - Home screen displays current rate and period
  - 24-hour timeline visualization implemented
  - UI updates automatically as time passes
  - Layout is adaptive to different screen sizes
- **Duration:** 3m 11s

### Task_4_SettingsAndFinalVerification: Add a settings screen or menu to allow users to change their provider/plan. Perform final UI refinements and stability checks. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Completed final feature implementation including settings/plan switching and UI refinements. The critic_agent verified application stability, core functionality (onboarding, home screen, plan switching), and responsiveness on phone emulators. The app follows Material 3 Expressive guidelines and handles adaptive layouts.
- **Acceptance Criteria:**
  - Settings screen functional
  - App does not crash
  - All features meet requirements
  - Build pass
- **Duration:** 7m 32s

