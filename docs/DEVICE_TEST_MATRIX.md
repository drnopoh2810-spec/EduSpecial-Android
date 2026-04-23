# Device Compatibility and Performance Matrix

Use this document to run and record release-candidate validation across device tiers.

## Test Scope

- Build under test: `app-release.apk` from CI artifact
- Version: `<fill-version-name-and-code>`
- Tester: `<fill-name>`
- Date: `<fill-date>`

## Device Matrix

### Tier A - Low-end (API 26-28)

- Device model: `<fill>`
- Android version/API: `<fill>`
- RAM class: `<fill>`
- Result: `[ ] Pass  [ ] Fail`
- Notes: `<fill>`

### Tier B - Mid-range (API 29-33)

- Device model: `<fill>`
- Android version/API: `<fill>`
- RAM class: `<fill>`
- Result: `[ ] Pass  [ ] Fail`
- Notes: `<fill>`

### Tier C - High-end (API 34-35)

- Device model: `<fill>`
- Android version/API: `<fill>`
- RAM class: `<fill>`
- Result: `[ ] Pass  [ ] Fail`
- Notes: `<fill>`

### Tier D - Tablet

- Device model: `<fill>`
- Android version/API: `<fill>`
- Screen size: `<fill>`
- Result: `[ ] Pass  [ ] Fail`
- Notes: `<fill>`

## Functional Checks (per device)

- [ ] App cold start under 3 seconds (or documented baseline)
- [ ] Login and register flows complete without crash
- [ ] Flashcards list + study session work end-to-end
- [ ] Search and Q&A screens load and respond normally
- [ ] Profile/settings updates persist after app restart
- [ ] Offline mode degrades gracefully
- [ ] Push notifications received (if environment configured)
- [ ] Media upload/playback flow works

## Performance Checks (per device)

- [ ] No ANR during 10-minute smoke session
- [ ] No major frame drops in scroll-heavy screens
- [ ] Memory remains stable during navigation loop
- [ ] Battery drain is within acceptable baseline

## Defect Logging Rules

- Severity:
  - `Critical`: crash/data loss/security issue
  - `Major`: key flow broken but workaround exists
  - `Minor`: UI issue or non-blocking degradation
- Every failure must include:
  - Reproduction steps
  - Device/API
  - Expected vs actual
  - Screenshot/video if possible

## Sign-off

- QA Lead sign-off: `[ ] Approved  [ ] Rejected`
- Release Manager sign-off: `[ ] Approved  [ ] Rejected`
- Final release recommendation: `<Go / No-Go>`
