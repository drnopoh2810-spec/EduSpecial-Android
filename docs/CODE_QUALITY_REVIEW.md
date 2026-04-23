# Code Quality Review - EduSpecial Android

Last updated: 2026-04-23

## Scope

- Reviewed current Kotlin modules under `app/src/main/java/com/eduspecial`.
- Focused on readability, duplication, complexity, and maintainability.

## Findings

### 1) Code smells and anti-patterns

- **Duplicate domain logic surface:** Two `ProfileViewModel` implementations in different packages increase ambiguity.
- **Repetitive async state updates:** Auth flow had repeated loading/success/failure handling; partially refactored into helper.
- **Silent catch blocks:** Some view models swallow exceptions without telemetry (`HomeViewModel`), reducing debuggability.

### 2) Readability assessment

- **Strengths:** Good use of `StateFlow`, dependency injection, and layered structure (`data/domain/presentation`).
- **Gaps:** Some methods carry too many state transitions inline, and package naming can be clearer for profile-related modules.

### 3) Refactoring completed in this pass

- Extracted shared auth execution helper in `AuthViewModel` to reduce duplicate error/loading handling.
- Removed duplicated Firebase dependency declaration in Gradle.
- Added release-signing configuration strategy with secure environment variables.

### 4) Recommended next refactors

- Merge profile implementations into one ViewModel and one `ProfileUiState`.
- Split analytics loading in `HomeViewModel` into dedicated private functions:
  - `loadStreak()`
  - `loadWeeklyProgress()`
  - `loadCategoryMastery()`
  - `loadTodayReviewCount()`
- Replace broad `catch (Exception)` blocks with targeted error handling and logging.

### 5) Naming and documentation guidance

- Prefer explicit names for intent:
  - `loadAnalytics()` -> `refreshDashboardAnalytics()`
  - `prefs` -> `userPreferencesDataStore`
- Add comments only for non-obvious flow decisions (fallbacks, retries, feature flags).

## Quality Gate Recommendations

- CI required checks:
  - `lintDebug`
  - `testDebugUnitTest`
  - `assembleDebug`
  - `assembleRelease`
- Optional quality checks:
  - SonarQube (enabled when secrets and sonar task exist)
  - Detekt/Ktlint integration in a follow-up PR
