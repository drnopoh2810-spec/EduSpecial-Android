# Auth Test Results Template

Use this template after executing `docs/AUTH_REAL_DEVICE_RUNBOOK.md`.

## Test Metadata

- Tester:
- Date:
- App version (name/code):
- Build source (local/CI artifact):
- Environment (staging/production):
- Device model:
- Android API level:

## Config Verification

- [ ] `firebase.web_client_id` is real (not placeholder)
- [ ] `firebase.android_client_id` is real (not placeholder)
- [ ] `auth.google_signin.web_client_id` is real (not placeholder)
- [ ] `auth.google_signin.android_client_id` is real (not placeholder)
- Notes:

## Flow Results

### A) Email/Password Login
- Result: `[ ] Pass  [ ] Fail`
- Notes:

### B) Google Sign-In
- Result: `[ ] Pass  [ ] Fail`
- Notes:

### C) Password Reset
- Result: `[ ] Pass  [ ] Fail`
- Notes:

### D) Email Verification Refresh
- Result: `[ ] Pass  [ ] Fail`
- Notes:

### E) Guest Mode
- Result: `[ ] Pass  [ ] Fail`
- Notes:

## Negative Scenarios

- Cancel Google flow handled gracefully:
  - `[ ] Pass  [ ] Fail`
- No-network auth failure shows clear message:
  - `[ ] Pass  [ ] Fail`
- Invalid password shows proper error and no auth:
  - `[ ] Pass  [ ] Fail`

## Auth Enforcement Checks

- [ ] Protected API call without token is rejected.
- [ ] Expired/invalid token triggers expected auth handling.
- [ ] No protected endpoint accessible anonymously unless explicitly allowed.
- Notes:

## Attachments

- Screenshots:
- Logcat snippets:
- Backend logs/trace IDs (if any):

## Final Decision

- Authentication readiness:
  - `[ ] Go`
  - `[ ] No-Go`
- Blocking issues (if any):
- Recommended follow-up actions:

## Sign-off

- QA Engineer:
- Backend Engineer:
- Release Manager:
