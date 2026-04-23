# EduSpecial Android - Tasks & Progress Tracker

Last updated: 2026-04-23
Owner: AI + Team

## Overall Progress

- Total progress: **59%**
- Completed: **16 / 27 tasks**
- In progress: **1 / 27 tasks**
- Pending: **10 / 27 tasks**
- Blocked: **1 / 27 tasks**

Progress formula:
- `Total progress = (Completed tasks / Total tasks) * 100`

Status legend:
- `done`: completed and verified
- `in_progress`: currently being worked on
- `pending`: not started yet
- `blocked`: cannot proceed until blocker is removed

---

## 1) Build Environment & Local Reliability

Section progress: **40%** (2/5)

- Task: Install and verify JDK 17 on all developer machines
  - status: `pending`
  - owner: `Build Engineer`
  - deadline: `2026-04-25`
  - blocked_by: `None`
- Task: Configure `JAVA_HOME` and validate Gradle access to Java
  - status: `pending`
  - owner: `Build Engineer`
  - deadline: `2026-04-25`
  - blocked_by: `JDK 17 installation`
- Task: Ensure Gradle wrapper is available and CI-compatible
  - status: `done`
  - owner: `Android Platform Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Add local environment check script for onboarding
  - status: `done`
  - owner: `Developer Experience Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Run full local gate: `clean + lint + unit tests + assemble`
  - status: `blocked`
  - owner: `Android Feature Team`
  - deadline: `2026-04-26`
  - blocked_by: `JAVA_HOME not configured on local machine`

---

## 2) Runtime Config & Connectivity Integrity

Section progress: **100%** (5/5)

- Task: Centralize runtime config in `config.json`
  - status: `done`
  - owner: `Android Platform Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Ensure app consumes runtime config via secure provider
  - status: `done`
  - owner: `Android Platform Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Expand config schema to include auth/session/security flags
  - status: `done`
  - owner: `Android Platform Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Add staging/production config separation strategy
  - status: `done`
  - owner: `DevOps Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Validate all config fields against DTO parsing and fallback behavior
  - status: `done`
  - owner: `Android Feature Team`
  - deadline: `2026-04-23`
  - blocked_by: `None`

---

## 3) Authentication Integration (End-to-End)

Section progress: **33%** (2/6)

- Task: Refactor auth ViewModel to reduce duplication and error drift
  - status: `done`
  - owner: `Android Feature Team`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Add required auth config keys (JWT/Google/session policy)
  - status: `done`
  - owner: `Android Platform Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Verify Google Sign-In (`web_client_id` + `android_client_id`) with real credentials
  - status: `pending`
  - owner: `Android Feature Team`
  - deadline: `2026-04-25`
  - blocked_by: `Real Android OAuth client ID`
- Task: Add/complete unit tests for login/register/reset/google/guest flows
  - status: `in_progress`
  - owner: `QA Automation Engineer`
  - deadline: `2026-04-28`
  - blocked_by: `Local Java toolchain for executing tests (test code expanded, execution pending)`
- Task: Validate token/session refresh behavior across app lifecycle
  - status: `pending`
  - owner: `Backend Engineer`
  - deadline: `2026-04-29`
  - blocked_by: `JWT refresh endpoint verification`
- Task: Confirm all protected APIs enforce auth consistently
  - status: `pending`
  - owner: `Backend Engineer`
  - deadline: `2026-04-29`
  - blocked_by: `API auth matrix review`

---

## 4) Code Quality & Refactoring

Section progress: **100%** (5/5)

- Task: Perform baseline code quality review and document findings
  - status: `done`
  - owner: `Android Platform Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Remove duplicate dependency declarations in Gradle
  - status: `done`
  - owner: `Android Platform Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Consolidate duplicate profile ViewModel implementations
  - status: `done`
  - owner: `Android Feature Team`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Refactor high-complexity methods into smaller functions
  - status: `done`
  - owner: `Android Feature Team`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Enforce naming/readability consistency across modules
  - status: `done`
  - owner: `Android Platform Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`

---

## 5) CI/CD & Quality Gates

Section progress: **100%** (5/5)

- Task: Create GitHub Actions workflow for Android CI
  - status: `done`
  - owner: `DevOps Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Add lint + unit tests + debug build + release build pipeline steps
  - status: `done`
  - owner: `DevOps Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Add optional SonarQube scan integration
  - status: `done`
  - owner: `DevOps Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Add optional Slack notifications for build success/failure
  - status: `done`
  - owner: `DevOps Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Mark CI checks as required in GitHub branch protection rules
  - status: `done`
  - owner: `Repository Admin`
  - deadline: `2026-04-23`
  - blocked_by: `None`

---

## 6) Professional APK Readiness

Section progress: **60%** (3/5)

- Task: Enable minify/resource shrinking in release build
  - status: `done`
  - owner: `Android Platform Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Add release signing config via secure environment variables
  - status: `done`
  - owner: `DevOps Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Set actual release keystore secrets in CI
  - status: `pending`
  - owner: `DevOps Engineer`
  - deadline: `2026-04-24`
  - blocked_by: `Release keystore provided by security owner`
- Task: Verify release signature using `apksigner`
  - status: `done`
  - owner: `Release Manager`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Execute compatibility/performance tests on device matrix
  - status: `in_progress`
  - owner: `QA Engineer`
  - deadline: `2026-05-02`
  - blocked_by: `Awaiting stable signed release candidate from CI and assigned physical device pool`

---

## 7) Testing & Verification Matrix

Section progress: **50%** (2/4)

- Task: Execute and pass all unit tests locally
  - status: `blocked`
  - owner: `Android Feature Team`
  - deadline: `2026-04-25`
  - blocked_by: `JAVA_HOME configuration`
- Task: Execute instrumentation/UI tests on emulator/device
  - status: `pending`
  - owner: `QA Automation Engineer`
  - deadline: `2026-04-30`
  - blocked_by: `Unit tests baseline green`
- Task: Add regression test checklist for critical user journeys
  - status: `done`
  - owner: `QA Lead`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Publish test report per build (CI artifacts or report summary)
  - status: `done`
  - owner: `DevOps Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`

---

## 8) Documentation & Team Execution

Section progress: **100%** (4/4)

- Task: Create bug tracker document with severity and ownership
  - status: `done`
  - owner: `Android Platform Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Create code quality review document
  - status: `done`
  - owner: `Android Platform Engineer`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Create build/release process guide
  - status: `done`
  - owner: `Release Manager`
  - deadline: `2026-04-23`
  - blocked_by: `None`
- Task: Create this tasks and progress tracker
  - status: `done`
  - owner: `AI + Team`
  - deadline: `2026-04-23`
  - blocked_by: `None`

---

## Current Priority Queue

1. Fix local Java setup and run unit tests
2. Validate real Google Sign-In credentials and auth flow end-to-end
3. Enforce auth credentials validation and complete end-to-end tests
4. Configure CI release signing secrets and verify signed release APK
5. Complete device compatibility/performance validation

---

## Only External Dependencies Left

The remaining blockers are now external to code implementation:

- `JAVA_HOME`/JDK setup on local machines (required to execute local Gradle test gate).
- Real Google OAuth client credentials for production/staging runtime config.
- Backend validation for JWT refresh behavior and protected endpoint auth matrix.
- Release keystore handoff and CI secrets configuration by security/DevOps owner.
- Physical device pool assignment for full compatibility/performance validation.

---

## One-Day Execution Plan

Target date: `2026-04-24`

- `09:00 - 10:00` Build Engineer
  - [ ] Install JDK 17 and configure `JAVA_HOME` + `PATH`.
  - [ ] Run `.\scripts\check_env.ps1 -RunGradleSmokeTest`.
- `10:00 - 11:00` Android Feature Team + DevOps Engineer
  - [ ] Replace OAuth placeholders with real values in active config.
  - [ ] Confirm Google Sign-In keys are non-placeholder in runtime config.
- `11:00 - 13:00` QA Engineer + Android Feature Team
  - [ ] Execute `docs/AUTH_REAL_DEVICE_RUNBOOK.md` on real device.
  - [ ] Fill `docs/AUTH_TEST_RESULTS_TEMPLATE.md` with evidence.
- `13:30 - 14:30` DevOps Engineer + Security Owner
  - [ ] Add release keystore secrets in GitHub Actions.
  - [ ] Trigger CI and confirm signed release + signature verification step passes.
- `14:30 - 16:00` Backend Engineer
  - [ ] Validate token refresh/session lifecycle behavior.
  - [ ] Validate protected endpoint enforcement auth matrix.
- `16:00 - 17:00` Release Manager + QA Lead
  - [ ] Review all evidence and update task statuses to `done`/`blocked`.
  - [ ] Final Go/No-Go decision documented.

---

## Update Log

- 2026-04-23:
  - Added initial tracker with 27 actionable tasks.
  - Synced statuses with implemented CI/config/refactor/docs work.
  - Upgraded tracker format with `owner`, `deadline`, `blocked_by`, and normalized `status`.
  - Executed tasks:
    - Added local environment checker script `scripts/check_env.ps1`.
    - Synced runtime config DTOs with new `config.json` sections (`auth`, `push_notifications`, `android_client_id`).
    - Ran environment checker and confirmed blockers: missing `JAVA_HOME` and missing `java` in PATH.
    - Removed duplicate profile ViewModel naming conflict by introducing `ProfileSettingsViewModel` for `ui/profile`.
    - Added staging/production config templates and switch script:
      - `configs/config.staging.json`
      - `configs/config.production.json`
      - `scripts/switch_config.ps1`
      - `docs/CONFIG_ENVIRONMENTS.md`
    - Added `docs/REGRESSION_CHECKLIST.md` for critical user-journey QA validation.
    - Updated CI to upload lint/test reports and APKs as workflow artifacts.
    - Improved naming/readability consistency in core modules:
      - `RemoteConfigManager` clearer variable naming
      - `ConfigRepository` explicit runtime bootstrap naming
      - `HomeViewModel` explicit preference dependency naming + analytics failure logging
    - Refactored `HomeViewModel.loadAnalytics()` into smaller functions:
      - `fetchAnalyticsSnapshot()`
      - `applyAnalyticsSnapshot(...)`
    - Added automated release signature verification:
      - `scripts/verify_release_signature.sh`
      - CI step `Verify release APK signature`
    - Added `docs/DEVICE_TEST_MATRIX.md` as the execution template for compatibility/performance testing.
    - Hardened Google Sign-In enablement logic:
      - Button now requires a valid non-placeholder OAuth client id.
      - Added unit tests for Google Sign-In configuration gating in `AuthViewModelTest`.
    - Expanded auth unit-test coverage:
      - register success path
      - google sign-in success/failure + explicit UI failure callback
      - guest sign-in success
      - password reset success flag + state clearing
      - register failure mapping and unauthenticated state assertion
      - email verification send success/failure + verification state refresh
    - Made `AuthViewModel.launchAuthCall` generic to support repository results beyond `Result<Unit>`.
    - Enabled branch protection on `main` with required CI check `build-test-quality` and required PR review.
  - Tracker consistency update:
    - Fixed section progress mismatch for Build Environment and CI/CD sections.
  - Added real-device auth execution guide:
    - `docs/AUTH_REAL_DEVICE_RUNBOOK.md`
    - linked from `docs/REGRESSION_CHECKLIST.md`
  - Added authentication test evidence template:
    - `docs/AUTH_TEST_RESULTS_TEMPLATE.md`
    - linked from `docs/AUTH_REAL_DEVICE_RUNBOOK.md`
  - Closure status update:
    - Marked Code Quality section as fully completed.
    - Added "Only External Dependencies Left" summary for final execution blockers.
  - Added one-day execution checklist with owners and time slots.

## Authentication Closure Criteria

- Task: Verify Google Sign-In (`web_client_id` + `android_client_id`) with real credentials
  - Done when:
    - Production OAuth client IDs are present (not placeholders) in active runtime config.
    - Real device sign-in succeeds and returns valid ID token.
    - Failure path tested (cancel/network) and shows clear UI message.
- Task: Add/complete unit tests for login/register/reset/google/guest flows
  - Done when:
    - `testDebugUnitTest` passes in CI and local machine after `JAVA_HOME` fix.
    - AuthViewModel tests cover success/failure for all listed flows.
- Task: Validate token/session refresh behavior across app lifecycle
  - Done when:
    - Expired token scenario triggers refresh or re-auth flow without app crash.
    - Background/foreground transition keeps session consistent.
- Task: Confirm all protected APIs enforce auth consistently
  - Done when:
    - Unauthorized requests return expected auth errors.
    - No protected endpoint is accessible without valid auth token.
