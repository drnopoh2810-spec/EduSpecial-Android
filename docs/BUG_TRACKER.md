# EduSpecial Android - Current Bug Tracker

Last updated: 2026-04-23

## Critical Bugs

1. **Release APK signed with debug key fallback**
   - **Impact:** Production APK can be tampered with and does not meet release security requirements.
   - **Area:** `app/build.gradle.kts`
   - **Status:** Mitigated by environment-based release signing support; still needs real keystore secrets on CI.
   - **Owner:** DevOps Engineer
   - **Verification:** Run `./gradlew assembleRelease` with release signing env vars and confirm APK signature with `apksigner verify --print-certs`.

2. **No active CI pipeline in repository**
   - **Impact:** Regressions can reach main branch without lint/test/build validation.
   - **Area:** `.github/workflows/`
   - **Status:** Fixed by adding `android-ci.yml`.
   - **Owner:** DevOps Engineer
   - **Verification:** Open PR and confirm workflow runs `lintDebug`, `testDebugUnitTest`, `assembleDebug`, `assembleRelease`.

## Major Bugs

1. **Duplicate `ProfileViewModel` classes in different packages**
   - **Impact:** Increases maintenance risk and can cause injection/UI wiring confusion.
   - **Area:** `app/src/main/java/com/eduspecial/ui/profile/ProfileViewModel.kt`, `app/src/main/java/com/eduspecial/presentation/profile/ProfileViewModel.kt`
   - **Status:** Open
   - **Owner:** Android Feature Team
   - **Verification:** Merge into one canonical profile ViewModel and validate affected screens with UI + unit tests.

2. **Unit tests cannot be executed locally without Java toolchain setup**
   - **Impact:** Fix verification is blocked on local environment.
   - **Area:** Local build environment (`JAVA_HOME` missing)
   - **Status:** Open (environment setup required)
   - **Owner:** Developer Experience / Build Engineer
   - **Verification:** Configure JDK 17 and run `./gradlew testDebugUnitTest`.

## Minor Bugs

1. **Redundant Firebase Firestore dependency declaration**
   - **Impact:** Dependency drift risk and noisy dependency graph.
   - **Area:** `app/build.gradle.kts`
   - **Status:** Fixed (duplicate declaration removed).
   - **Owner:** Android Platform Engineer
   - **Verification:** `./gradlew :app:dependencies` and confirm no duplicate explicit firestore entry.

2. **Repeated async auth state handling in `AuthViewModel`**
   - **Impact:** Harder readability and higher defect risk when changing auth flows.
   - **Area:** `app/src/main/java/com/eduspecial/presentation/auth/AuthViewModel.kt`
   - **Status:** Fixed by extracting shared auth call helper.
   - **Owner:** Android Feature Team
   - **Verification:** Run existing auth unit tests and confirm no behavior regression.

## Assignment Matrix

- **DevOps Engineer:** CI workflow, release signing secrets, SonarQube integration.
- **Android Feature Team:** Profile module consolidation, auth and profile flow validation.
- **Build Engineer:** Local JDK/Gradle environment reliability and onboarding script.

## Unit Test Verification Log

- `testDebugUnitTest`: **Blocked locally** (missing `JAVA_HOME`).
- CI verification: **Pending first GitHub run** after pushing workflow.
