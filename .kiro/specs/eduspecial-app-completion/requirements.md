# Requirements Document

## Introduction

EduSpecial is an Arabic-first Android application serving as an ABA therapy and special education encyclopedia. The app is approximately 85% complete, with a working authentication flow, flashcard system with spaced-repetition study (SRS), Q&A forum, Algolia-powered search, offline-first Room database, Firebase Auth, Cloudinary media service, and a profile screen. This document specifies the requirements for completing and polishing the remaining 15% — covering media upload wiring, content editing, answer management, avatar upload, background sync, study reminder notifications, analytics, bookmarks, profile customization, and comprehensive UI polish — to bring the app to a world-class, production-ready state.

---

## Glossary

- **App**: The EduSpecial Android application.
- **MediaPicker**: The composable component (`MediaPickerSection`) responsible for selecting and uploading image, video, or audio files.
- **CloudinaryService**: The remote service (`CloudinaryService.kt`) that accepts file uploads and returns a hosted media URL.
- **FlashcardEditor**: The UI dialog and associated ViewModel logic for creating and editing `Flashcard` domain objects.
- **QAEditor**: The UI dialog and associated ViewModel logic for creating and editing `QAQuestion` and `QAAnswer` domain objects.
- **AnswerManager**: The UI and ViewModel logic responsible for displaying, upvoting, and accepting `QAAnswer` objects within a question thread.
- **AvatarUploader**: The UI and ViewModel logic for selecting a profile photo, uploading it via `CloudinaryService`, and persisting the resulting URL to the user profile.
- **SyncWorker**: The `CoroutineWorker` (`SyncWorker.kt`) that runs in the background to push pending offline submissions and pull incremental server updates.
- **PendingSubmissionDao**: The Room DAO that stores locally-created content awaiting server synchronisation.
- **NotificationScheduler**: The component responsible for scheduling and cancelling daily study reminder notifications using `WorkManager` and `AlarmManager`.
- **AnalyticsDashboard**: The UI section on the Home screen that displays study streak, weekly progress chart, and per-category mastery breakdown.
- **BookmarkManager**: The repository and DAO layer that persists and retrieves bookmarked `Flashcard` and `QAQuestion` items.
- **ProfileEditor**: The UI and ViewModel logic for editing the authenticated user's display name and avatar.
- **OnboardingFlow**: The multi-step introductory screen sequence shown to first-time users, gated by the `KEY_ONBOARDING_DONE` DataStore key.
- **StudyCard**: The animated flip card composable displayed during a study session in `StudyScreen`.
- **SkeletonLoader**: A shimmer-effect placeholder composable shown while list data is loading, replacing circular progress indicators.
- **UserPreferencesDataStore**: The DataStore-backed preferences store (`UserPreferencesDataStore.kt`) holding user settings including `KEY_ONBOARDING_DONE`, `KEY_STUDY_NOTIFICATIONS`, `KEY_DAILY_GOAL`, and `KEY_LAST_SYNC`.
- **EduSpecialApiService**: The Retrofit interface (`EduSpecialApiService.kt`) defining all remote API endpoints.
- **Authenticated User**: A user who has completed sign-in via Firebase Auth and whose Firebase ID token is attached to API requests.
- **Content Author**: The `Authenticated User` whose `contributorId` matches the `contributor` field of a given `Flashcard`, `QAQuestion`, or `QAAnswer`.

---

## Requirements

### Requirement 1: Media Upload Integration

**User Story:** As a contributor, I want to attach an image, video, or audio file when creating a flashcard, so that learners have rich multimedia context for each term.

#### Acceptance Criteria

1. WHEN the `FlashcardEditor` dialog is open, THE `MediaPicker` SHALL be rendered as a section within the dialog, below the definition field.
2. WHEN a user selects an image file via the `MediaPicker`, THE `CloudinaryService` SHALL upload the file and return a non-empty HTTPS URL within 30 seconds.
3. WHEN a user selects a video file via the `MediaPicker`, THE `CloudinaryService` SHALL upload the file and return a non-empty HTTPS URL within 60 seconds.
4. WHEN the `CloudinaryService` upload is in progress, THE `MediaPicker` SHALL display an upload progress indicator showing the percentage complete from 0 to 100.
5. WHEN the `CloudinaryService` upload completes successfully, THE `FlashcardEditor` SHALL attach the returned URL and the corresponding `MediaType` to the flashcard being created.
6. WHEN the `CloudinaryService` upload fails, THE `MediaPicker` SHALL display an Arabic error message and SHALL allow the user to retry the upload without closing the dialog.
7. WHEN a media file has been selected and uploaded, THE `MediaPicker` SHALL display a preview of the selected media and a clear button to remove it.
8. WHEN the user taps the clear button on the media preview, THE `FlashcardEditor` SHALL set `mediaUrl` to null and `mediaType` to `MediaType.NONE`.
9. WHEN the `FlashcardEditor` is submitted with a valid `mediaUrl`, THE `SubmitFlashcardUseCase` SHALL include the `mediaUrl` and `mediaType` in the `CreateFlashcardRequest` sent to `EduSpecialApiService`.
10. WHERE audio attachment is supported, THE `MediaPicker` SHALL provide a dedicated audio picker button in addition to the image and video buttons.

---

### Requirement 2: Flashcard Content Editing

**User Story:** As a contributor, I want to edit my own flashcards after creation, so that I can correct mistakes or improve definitions over time.

#### Acceptance Criteria

1. WHEN a `Flashcard` is displayed in the flashcard list and the `Authenticated User` is the `Content Author`, THE `FlashcardEditor` SHALL display an edit icon button on the flashcard item.
2. WHEN the edit icon is tapped, THE `FlashcardEditor` SHALL open a pre-populated edit dialog containing the existing term, definition, category, and media attachment.
3. WHEN the user modifies the term field in the edit dialog, THE `FlashcardEditor` SHALL perform a duplicate check against existing flashcards, excluding the flashcard being edited.
4. WHEN the user submits the edit dialog with a non-empty term and non-empty definition, THE `EduSpecialApiService` SHALL send a PATCH or PUT request to update the flashcard on the server.
5. WHEN the server update succeeds, THE `FlashcardEditor` SHALL update the corresponding `FlashcardEntity` in the Room database and dismiss the dialog.
6. WHEN the server update fails due to a network error, THE `FlashcardEditor` SHALL store the edit in the `PendingSubmissionDao` for later synchronisation and SHALL dismiss the dialog with a success indication.
7. IF the `Authenticated User` is not the `Content Author`, THEN THE `FlashcardEditor` SHALL not display the edit icon for that flashcard.

---

### Requirement 3: Q&A Content Editing

**User Story:** As a contributor, I want to edit my own questions and answers after posting, so that I can improve clarity or fix errors.

#### Acceptance Criteria

1. WHEN a `QAQuestion` is displayed and the `Authenticated User` is the `Content Author`, THE `QAEditor` SHALL display an edit icon on the question card.
2. WHEN the question edit icon is tapped, THE `QAEditor` SHALL open a pre-populated dialog containing the existing question text and category.
3. WHEN the user submits the question edit with non-empty question text, THE `EduSpecialApiService` SHALL send a PATCH request to update the question on the server.
4. WHEN a `QAAnswer` is displayed and the `Authenticated User` is the `Content Author`, THE `QAEditor` SHALL display an edit icon on the answer item.
5. WHEN the answer edit icon is tapped, THE `QAEditor` SHALL open a pre-populated dialog containing the existing answer content.
6. WHEN the user submits the answer edit with non-empty content, THE `EduSpecialApiService` SHALL send a PATCH request to update the answer on the server.
7. WHEN any Q&A edit succeeds on the server, THE `QAEditor` SHALL update the corresponding entity in the Room database and dismiss the dialog.
8. IF the `Authenticated User` is not the `Content Author`, THEN THE `QAEditor` SHALL not display edit icons for that question or answer.

---

### Requirement 4: Answer Management

**User Story:** As a question author, I want to accept the best answer and upvote helpful answers, so that the community can identify the most useful responses.

#### Acceptance Criteria

1. WHEN a `QAQuestion` is expanded to show its answers and the `Authenticated User` is the `Content Author` of the question, THE `AnswerManager` SHALL display an "accept answer" button on each non-accepted answer.
2. WHEN the "accept answer" button is tapped, THE `EduSpecialApiService` SHALL call `POST answers/{id}/accept` for the selected answer.
3. WHEN the accept call succeeds, THE `AnswerManager` SHALL mark the accepted answer with a visual indicator (green checkmark) and SHALL remove the "accept answer" button from all other answers in that question.
4. WHEN a `QAQuestion` is expanded, THE `AnswerManager` SHALL display the accepted answer at the top of the answer list, above non-accepted answers.
5. WHEN a `QAQuestion` is displayed in the list, THE `AnswerManager` SHALL display the answer count and a button to expand or collapse the answer thread inline.
6. WHEN the answer thread is expanded, THE `AnswerManager` SHALL display each answer with its content, contributor name, upvote count, and an upvote button.
7. WHEN the upvote button on an answer is tapped, THE `EduSpecialApiService` SHALL call `POST answers/{id}/upvote` and THE `AnswerManager` SHALL increment the displayed upvote count by 1.
8. WHEN a `QAQuestion` has at least one accepted answer, THE `AnswerManager` SHALL display a green "answered" badge on the question card in the list.

---

### Requirement 5: Avatar Upload

**User Story:** As a registered user, I want to set a profile photo, so that my identity is visually represented in the community.

#### Acceptance Criteria

1. WHEN the Profile screen is displayed and the `Authenticated User` has no `avatarUrl`, THE `AvatarUploader` SHALL display the user's initials in a circular placeholder.
2. WHEN the Profile screen is displayed and the `Authenticated User` has a non-empty `avatarUrl`, THE `AvatarUploader` SHALL display the image from that URL in a circular 96dp avatar using Coil.
3. WHEN the user taps the avatar area, THE `AvatarUploader` SHALL open the system image picker.
4. WHEN the user selects an image from the system picker, THE `CloudinaryService` SHALL upload the image to the `avatars` folder and return a non-empty HTTPS URL.
5. WHEN the `CloudinaryService` upload completes, THE `AvatarUploader` SHALL call `EduSpecialApiService` to update the user profile with the new `avatarUrl`.
6. WHEN the profile update succeeds, THE `AvatarUploader` SHALL immediately display the new avatar image in the Profile screen without requiring a restart.
7. WHEN the `CloudinaryService` upload fails, THE `AvatarUploader` SHALL display an Arabic error snackbar and SHALL retain the previous avatar state.
8. WHILE the avatar upload is in progress, THE `AvatarUploader` SHALL display a circular progress indicator overlaid on the avatar area.

---

### Requirement 6: Background Sync Worker

**User Story:** As a user who creates content while offline, I want my submissions to be automatically synced to the server when connectivity is restored, so that my contributions are not lost.

#### Acceptance Criteria

1. WHEN the App starts, THE `SyncWorker` SHALL be scheduled as a periodic `WorkManager` task with a repeat interval of 15 minutes, requiring network connectivity.
2. WHEN the `SyncWorker` executes, THE `SyncWorker` SHALL retrieve all entries from `PendingSubmissionDao` and attempt to submit each one to `EduSpecialApiService`.
3. WHEN a pending submission is successfully sent to `EduSpecialApiService`, THE `SyncWorker` SHALL delete the corresponding entry from `PendingSubmissionDao`.
4. WHEN a pending submission fails with a retryable error (HTTP 5xx or network timeout), THE `SyncWorker` SHALL increment the `retryCount` for that entry in `PendingSubmissionDao`.
5. WHEN a `PendingSubmissionEntity` has a `retryCount` of 5 or greater, THE `SyncWorker` SHALL delete the entry from `PendingSubmissionDao` and SHALL log the failure.
6. WHEN all pending submissions are processed, THE `SyncWorker` SHALL call `EduSpecialApiService.syncFlashcards(since = lastSyncTimestamp)` and `EduSpecialApiService.syncQuestions(since = lastSyncTimestamp)` to pull incremental updates.
7. WHEN the incremental pull succeeds, THE `SyncWorker` SHALL upsert the returned items into the Room database and SHALL update `KEY_LAST_SYNC` in `UserPreferencesDataStore`.
8. WHEN the `SyncWorker` fails with an unrecoverable error, THE `SyncWorker` SHALL return `Result.retry()` for the first 3 attempts and `Result.failure()` thereafter.
9. IF the device has no network connectivity when the `SyncWorker` is triggered, THEN THE `SyncWorker` SHALL not execute and WorkManager SHALL reschedule the task for the next available window.
10. FOR ALL sequences of offline flashcard creations followed by a sync, THE `SyncWorker` SHALL produce a Room database state that is equivalent to creating those flashcards directly online (idempotent sync property).

---

### Requirement 7: Study Reminder Notifications

**User Story:** As a learner, I want to receive a daily notification reminding me to study, so that I maintain a consistent review habit.

#### Acceptance Criteria

1. WHEN the user enables the "إشعارات المراجعة" toggle in the Profile screen, THE `NotificationScheduler` SHALL schedule a daily notification at the time stored in `UserPreferencesDataStore` (defaulting to 08:00 local time).
2. WHEN the user disables the "إشعارات المراجعة" toggle, THE `NotificationScheduler` SHALL cancel all pending study reminder notifications.
3. WHEN the daily notification fires, THE App SHALL display a notification with an Arabic title "حان وقت المراجعة!" and a body showing the number of flashcards due for review.
4. WHEN the user taps the notification, THE App SHALL open directly to the Study screen.
5. WHEN the user has completed the daily goal before the notification fires, THE `NotificationScheduler` SHALL suppress the notification for that day.
6. WHEN the App is first installed and the user completes onboarding, THE `NotificationScheduler` SHALL request the `POST_NOTIFICATIONS` permission on Android 13 and above before scheduling any notification.
7. IF the `POST_NOTIFICATIONS` permission is denied, THEN THE `NotificationScheduler` SHALL set the `KEY_STUDY_NOTIFICATIONS` preference to false and SHALL display an explanatory snackbar.
8. WHILE the App is in the foreground, THE `NotificationScheduler` SHALL not display the study reminder notification.

---

### Requirement 8: Analytics and Progress Dashboard

**User Story:** As a learner, I want to see my study streak, weekly progress, and category mastery, so that I can track my learning journey and stay motivated.

#### Acceptance Criteria

1. THE `AnalyticsDashboard` SHALL display the current study streak as the number of consecutive calendar days on which the user completed at least one SRS review.
2. WHEN the user completes at least one SRS review on a given calendar day, THE `AnalyticsDashboard` SHALL increment the streak counter for that day.
3. WHEN the user does not complete any SRS review on a calendar day, THE `AnalyticsDashboard` SHALL reset the streak counter to 0 the following day.
4. THE `AnalyticsDashboard` SHALL display a bar chart showing the number of flashcards reviewed on each of the past 7 calendar days.
5. WHEN the bar chart data is loading, THE `AnalyticsDashboard` SHALL display a `SkeletonLoader` placeholder for the chart area.
6. THE `AnalyticsDashboard` SHALL display a per-category mastery breakdown showing, for each `FlashcardCategory`, the count of `ARCHIVED` flashcards divided by the total flashcard count in that category, expressed as a percentage.
7. THE `AnalyticsDashboard` SHALL display a daily goal progress bar showing the number of cards reviewed today versus the `KEY_DAILY_GOAL` value from `UserPreferencesDataStore`.
8. WHEN the daily goal is reached, THE `AnalyticsDashboard` SHALL display a congratulatory message and a filled progress bar.
9. FOR ALL sequences of SRS review events, THE streak calculation SHALL satisfy the invariant: streak equals the length of the longest suffix of consecutive days ending on today that each contain at least one review.

---

### Requirement 9: Bookmarks and Favorites

**User Story:** As a learner, I want to bookmark flashcards and questions for later review, so that I can quickly return to content I find important.

#### Acceptance Criteria

1. WHEN a `Flashcard` is displayed in the flashcard list, THE `BookmarkManager` SHALL display a bookmark icon button on the flashcard item.
2. WHEN the bookmark icon is tapped on an unbookmarked `Flashcard`, THE `BookmarkManager` SHALL persist a bookmark record in the Room database and SHALL update the icon to a filled state.
3. WHEN the bookmark icon is tapped on a bookmarked `Flashcard`, THE `BookmarkManager` SHALL delete the bookmark record from the Room database and SHALL update the icon to an outlined state.
4. WHEN a `QAQuestion` is displayed in the Q&A list, THE `BookmarkManager` SHALL display a bookmark icon button on the question card.
5. WHEN the bookmark icon is tapped on a `QAQuestion`, THE `BookmarkManager` SHALL persist or delete the bookmark record following the same toggle logic as flashcard bookmarks.
6. THE App SHALL provide a "المحفوظات" (Bookmarks) section accessible from the Home screen quick actions or the Profile screen.
7. WHEN the Bookmarks section is opened, THE `BookmarkManager` SHALL display all bookmarked flashcards and questions in a tabbed list, separated by type.
8. WHEN a bookmarked item is deleted from the server by its `Content Author`, THE `BookmarkManager` SHALL remove the corresponding bookmark record from the Room database during the next sync.
9. FOR ALL bookmark toggle operations, THE `BookmarkManager` SHALL satisfy the idempotence property: toggling a bookmark twice returns the item to its original bookmarked state.
10. FOR ALL bookmark toggle operations, THE `BookmarkManager` SHALL satisfy the round-trip property: bookmark then unbookmark results in the item appearing in the unbookmarked state, and unbookmark then bookmark results in the item appearing in the bookmarked state.

---

### Requirement 10: User Profile Customization

**User Story:** As a registered user, I want to edit my display name, so that my profile reflects my preferred identity in the community.

#### Acceptance Criteria

1. WHEN the Profile screen is displayed, THE `ProfileEditor` SHALL display an edit icon adjacent to the display name.
2. WHEN the edit icon is tapped, THE `ProfileEditor` SHALL display an inline text field or dialog pre-populated with the current display name.
3. WHEN the user submits a new display name that is between 2 and 50 characters in length, THE `EduSpecialApiService` SHALL send a PATCH request to update the display name on the server.
4. WHEN the server update succeeds, THE `ProfileEditor` SHALL update the displayed name in the Profile screen and in the `UserPreferencesDataStore` without requiring a restart.
5. WHEN the user submits a display name shorter than 2 characters, THE `ProfileEditor` SHALL display an Arabic validation error message and SHALL not submit the request.
6. WHEN the user submits a display name longer than 50 characters, THE `ProfileEditor` SHALL display an Arabic validation error message and SHALL not submit the request.
7. WHEN the server update fails, THE `ProfileEditor` SHALL display an Arabic error snackbar and SHALL retain the previous display name.
8. FOR ALL valid display name strings (length between 2 and 50 characters), THE `ProfileEditor` SHALL accept the input and submit it without error (round-trip property: save name → read name → same value).

---

### Requirement 11: Onboarding Flow

**User Story:** As a first-time user, I want to see an introductory walkthrough of the app's features, so that I understand how to use EduSpecial effectively.

#### Acceptance Criteria

1. WHEN the App is launched and `KEY_ONBOARDING_DONE` is false in `UserPreferencesDataStore`, THE `OnboardingFlow` SHALL be displayed before the Home screen.
2. THE `OnboardingFlow` SHALL consist of at least 3 pages: an introduction to the flashcard system, an introduction to the SRS study method, and an introduction to the Q&A forum.
3. WHEN the user swipes forward or taps "التالي" on an onboarding page, THE `OnboardingFlow` SHALL advance to the next page.
4. WHEN the user reaches the final onboarding page and taps "ابدأ الآن", THE `OnboardingFlow` SHALL set `KEY_ONBOARDING_DONE` to true in `UserPreferencesDataStore` and SHALL navigate to the Home screen.
5. WHEN the user taps "تخطي" on any onboarding page, THE `OnboardingFlow` SHALL set `KEY_ONBOARDING_DONE` to true and SHALL navigate to the Home screen.
6. WHEN the App is launched and `KEY_ONBOARDING_DONE` is true, THE `OnboardingFlow` SHALL not be displayed and the App SHALL navigate directly to the Home screen or Auth screen as appropriate.
7. THE `OnboardingFlow` SHALL use Lottie animations on each page to illustrate the described feature.
8. WHEN the `OnboardingFlow` is displayed, THE `OnboardingFlow` SHALL display a page indicator showing the current page position.

---

### Requirement 12: UI Polish — Category Filter

**User Story:** As a user browsing flashcards, I want to filter by any of the 10 available categories, so that I can focus on a specific subject area.

#### Acceptance Criteria

1. THE `FlashcardEditor` filter row SHALL display all 10 `FlashcardCategory` values as horizontally scrollable `FilterChip` components in a `LazyRow`.
2. WHEN the filter row contains more chips than fit on screen, THE filter row SHALL be horizontally scrollable without wrapping to a second line.
3. WHEN a `FilterChip` is selected, THE flashcard list SHALL update to show only flashcards matching the selected category within 100 milliseconds.
4. WHEN the "الكل" chip is selected, THE flashcard list SHALL display all flashcards regardless of category.

---

### Requirement 13: UI Polish — Media Playback in Study Screen

**User Story:** As a learner, I want to see or hear the media attached to a flashcard during a study session, so that multimedia context aids my recall.

#### Acceptance Criteria

1. WHEN the `StudyCard` is flipped to the back face and the current `Flashcard` has `mediaType` of `IMAGE`, THE `StudyCard` SHALL display the image from `mediaUrl` using Coil's `AsyncImage` composable.
2. WHEN the `StudyCard` is flipped to the back face and the current `Flashcard` has `mediaType` of `VIDEO`, THE `StudyCard` SHALL display an ExoPlayer video player with playback controls.
3. WHEN the `StudyCard` is flipped to the back face and the current `Flashcard` has `mediaType` of `AUDIO`, THE `StudyCard` SHALL display an audio player with play/pause and seek controls using ExoPlayer.
4. WHEN the user navigates to the next flashcard, THE `StudyCard` SHALL stop any active media playback and release the ExoPlayer instance.
5. WHEN the `StudyCard` is on the front face, THE `StudyCard` SHALL not load or play any media.
6. IF the media URL is unreachable, THEN THE `StudyCard` SHALL display an Arabic error message in place of the media player.

---

### Requirement 14: UI Polish — Empty States with Lottie Animations

**User Story:** As a user viewing an empty list, I want to see an engaging illustration with a helpful message, so that the app feels polished and guides me toward taking action.

#### Acceptance Criteria

1. WHEN the flashcard list is empty, THE App SHALL display a Lottie animation alongside the Arabic message "لا توجد بطاقات بعد" and a call-to-action button to add the first flashcard.
2. WHEN the Q&A question list is empty, THE App SHALL display a Lottie animation alongside the Arabic message "لا توجد أسئلة بعد" and a call-to-action button to ask the first question.
3. WHEN the search results list is empty, THE App SHALL display a Lottie animation alongside the Arabic message "لا توجد نتائج" and a suggestion to try different keywords.
4. WHEN the Bookmarks section is empty, THE App SHALL display a Lottie animation alongside the Arabic message "لا توجد محفوظات بعد".
5. WHEN the study queue is empty (no cards due for review), THE App SHALL display a Lottie animation alongside the Arabic message "أحسنت! لا توجد بطاقات للمراجعة اليوم".
6. THE Lottie animations used for empty states SHALL loop continuously while the empty state is visible.

---

### Requirement 15: UI Polish — Loading Skeletons

**User Story:** As a user waiting for content to load, I want to see a skeleton placeholder that matches the layout of the content, so that the app feels fast and responsive.

#### Acceptance Criteria

1. WHEN the flashcard list is loading data from the Room database or network, THE App SHALL display at least 3 `SkeletonLoader` card placeholders matching the shape of `FlashcardItem`.
2. WHEN the Q&A question list is loading, THE App SHALL display at least 3 `SkeletonLoader` card placeholders matching the shape of the question card.
3. WHEN the Home screen stats are loading, THE App SHALL display `SkeletonLoader` placeholders for the three stat cards.
4. WHEN the `AnalyticsDashboard` chart is loading, THE App SHALL display a `SkeletonLoader` placeholder for the chart area.
5. WHEN data finishes loading, THE App SHALL replace all `SkeletonLoader` placeholders with the actual content using a crossfade transition of 300 milliseconds.
6. THE `SkeletonLoader` composable SHALL use a shimmer animation cycling between two surface color variants to indicate loading activity.

---

### Requirement 16: UI Polish — Swipe-to-Dismiss and Pull-to-Refresh

**User Story:** As a user managing flashcards, I want to swipe to dismiss items and pull to refresh lists, so that the app interaction feels native and fluid.

#### Acceptance Criteria

1. WHEN the user swipes a `FlashcardItem` horizontally to the left, THE App SHALL reveal a delete confirmation action and, upon confirmation, SHALL delete the flashcard if the `Authenticated User` is the `Content Author`.
2. WHEN the user swipes a `FlashcardItem` horizontally to the right, THE App SHALL reveal a bookmark action that toggles the bookmark state for that flashcard.
3. IF the `Authenticated User` is not the `Content Author` of a `FlashcardItem`, THEN THE App SHALL not reveal the delete action on swipe, but SHALL still reveal the bookmark action.
4. WHEN the user performs a pull-to-refresh gesture on the flashcard list, THE App SHALL trigger `SyncWorker.triggerImmediateSync()` and SHALL display a refresh indicator until the sync completes.
5. WHEN the user performs a pull-to-refresh gesture on the Q&A question list, THE App SHALL reload questions from the server and SHALL display a refresh indicator until loading completes.
6. WHEN the user performs a pull-to-refresh gesture on the Home screen, THE App SHALL reload all stats and SHALL display a refresh indicator until loading completes.
7. WHEN a swipe-to-delete action is confirmed, THE App SHALL display an undo snackbar for 4 seconds allowing the user to cancel the deletion.

---

### Requirement 17: UI Polish — RTL and Accessibility

**User Story:** As an Arabic-speaking user, I want the app to be fully right-to-left and accessible, so that the interface feels natural and is usable with assistive technologies.

#### Acceptance Criteria

1. THE App SHALL set `android:supportsRtl="true"` in `AndroidManifest.xml` and all layouts SHALL render in right-to-left direction when the device locale is Arabic.
2. THE App SHALL apply `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)` at the root composable level to enforce RTL layout for all Compose screens.
3. WHEN a swipe-to-dismiss gesture is used, THE App SHALL reverse the swipe direction for RTL layouts (swipe right to delete, swipe left to bookmark).
4. THE App SHALL provide `contentDescription` values in Arabic for all `Icon` composables that convey meaning and are not accompanied by a visible label.
5. WHEN the system font size is increased via accessibility settings, THE App SHALL scale all text sizes proportionally without truncating content in cards or dialogs.
