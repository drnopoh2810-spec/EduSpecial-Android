# Regression Checklist (Critical Journeys)

Use this checklist before release candidates and after major auth/config changes.

Execution companion for auth validation:
- `docs/AUTH_REAL_DEVICE_RUNBOOK.md`

## 1) Authentication

- [ ] Email/password login succeeds with valid credentials.
- [ ] Login shows proper error for invalid credentials.
- [ ] Register creates account and navigates correctly.
- [ ] Password reset request works and shows success state.
- [ ] Google Sign-In works with configured OAuth clients.
- [ ] Guest mode works (if enabled) without crashing.

## 2) Onboarding and Permissions

- [ ] First launch goes to permissions screen.
- [ ] After permissions, app routes to auth/onboarding correctly.
- [ ] Onboarding completion persists across app restart.

## 3) Study and Flashcards

- [ ] Flashcards list loads without UI glitches.
- [ ] Study flow progresses cards and saves state.
- [ ] Daily goal updates and persists.
- [ ] Bookmarks add/remove works and persists.

## 4) Search and Q&A

- [ ] Search returns results from Algolia/local fallback.
- [ ] No crash when search service is unavailable.
- [ ] Q&A list/search/filter paths work.

## 5) Profile and Settings

- [ ] Profile screen loads avatar/name/stats.
- [ ] Display name update succeeds and UI refreshes.
- [ ] Password change flow validates and handles backend errors.
- [ ] Notifications toggle updates behavior correctly.

## 6) Reliability and Offline

- [ ] App launches with no network (graceful degraded mode).
- [ ] API degraded/offline banners appear when expected.
- [ ] Cached config is used when runtime config fetch fails.

## 7) Build and Quality Gates

- [ ] `lintDebug` passes.
- [ ] `testDebugUnitTest` passes.
- [ ] `assembleDebug` and `assembleRelease` pass.
- [ ] CI artifacts include lint/test reports and APK outputs.
