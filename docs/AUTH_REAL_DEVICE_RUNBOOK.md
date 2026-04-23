# Auth Real-Device Runbook

Use this runbook to close authentication tasks with repeatable evidence.

## Preconditions

- A real Android device connected and visible in `adb devices`.
- Active config has real OAuth values (no placeholders):
  - `firebase.web_client_id`
  - `firebase.android_client_id`
  - `auth.google_signin.web_client_id`
  - `auth.google_signin.android_client_id`
- Backend auth endpoints deployed and reachable.

## Build and Install

- Build debug APK:
  - `./gradlew assembleDebug`
- Install:
  - `adb install -r app/build/outputs/apk/debug/app-debug.apk`

## Test Flow A - Email/Password

- Open app and login with valid account.
- Expected:
  - Login succeeds and navigates to home.
  - No crash, no endless loading.
- Negative check:
  - Try invalid password.
  - Expected error message appears and user remains unauthenticated.

## Test Flow B - Google Sign-In

- Tap "متابعة باستخدام Google".
- Complete account selection.
- Expected:
  - Sign-in succeeds and reaches home.
  - UI does not show placeholder/misconfig errors.
- Negative checks:
  - Cancel sign-in flow.
  - Disconnect network and retry.
  - Expected clear failure messages, no crash.

## Test Flow C - Password Reset

- Trigger "نسيت كلمة المرور؟" with valid email.
- Expected:
  - Success state/feedback shown.
  - No error in UI state.

## Test Flow D - Email Verification Refresh

- Send verification email.
- Verify email externally.
- Return to app and run verification refresh path.
- Expected:
  - `isEmailVerified` updates to true.

## Test Flow E - Guest Mode

- Tap guest mode.
- Expected:
  - App opens main experience without auth crash.
  - Protected operations still enforce auth as designed.

## Evidence to Record

- Device model + Android API.
- App version.
- Passed/failed flows.
- Screenshots for any failure.
- Logcat snippet for failures.
- Fill and archive:
  - `docs/AUTH_TEST_RESULTS_TEMPLATE.md`

## Closure Output

Mark these tasks as `done` only after passing results are recorded:
- Verify Google Sign-In (`web_client_id` + `android_client_id`) with real credentials
- Validate token/session refresh behavior across app lifecycle
- Confirm all protected APIs enforce auth consistently
