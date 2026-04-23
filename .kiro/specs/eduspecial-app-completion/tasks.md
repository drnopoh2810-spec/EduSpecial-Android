# Implementation Tasks — EduSpecial App Completion

## Overview

Tasks are ordered by dependency: data layer first, then domain, then presentation, then polish and tests. Each task maps to one or more requirements from `requirements.md` and design decisions from `design.md`.

---

## Phase 1 — Data Layer Foundation

- [x] 1. Add `BookmarkEntity` and `DailyReviewLogEntity` to Room
  - [x] 1.1 Create `BookmarkEntity` data class in `Entities.kt` with `id`, `itemId`, `itemType`, `createdAt` fields and composite unique index on `(itemId, itemType)`
  - [x] 1.2 Create `DailyReviewLogEntity` data class in `Entities.kt` with `dayEpoch` primary key, `reviewCount`, `archivedCount` fields
  - [x] 1.3 Create `BookmarkDao` interface in `DAOs.kt` with `getAllBookmarks()`, `getBookmarksByType()`, `isBookmarked()`, `insert()`, `delete()`, `deleteOrphans()` methods
  - [x] 1.4 Create `AnalyticsDao` interface in `DAOs.kt` with `upsertLog()`, `incrementLog()`, `getLogsFrom()`, `getLast7Days()` methods
  - [x] 1.5 Add `updateContent()` query to `FlashcardDao` for editing term, definition, category, mediaUrl, mediaType by id
  - [x] 1.6 Add `getCategoryMastery()` query to `FlashcardDao` returning `CategoryMasteryRow` projections
  - [x] 1.7 Add `getDueCount()` query to `FlashcardDao` returning count of flashcards where `nextReviewDate <= now`
  - [x] 1.8 Add `updateQuestion()`, `updateAnswer()`, `acceptAnswer()`, `markQuestionAnswered()` queries to `QADao`
  - [x] 1.9 Bump `EduSpecialDatabase` version to 2, add `BookmarkEntity` and `DailyReviewLogEntity` to `@Database` entities list, expose `bookmarkDao()` and `analyticsDao()` abstract functions
  - [x] 1.10 Write `Migration(1, 2)` that creates `bookmarks` and `daily_review_logs` tables with correct schema, and register it in `Room.databaseBuilder`

---

## Phase 2 — API Layer

- [x] 2. Add PATCH endpoints and new DTOs to `EduSpecialApiService`
  - [x] 2.1 Add `UpdateFlashcardRequest` data class to `Dtos.kt` with `term`, `definition`, `category`, `mediaUrl`, `mediaType` fields
  - [x] 2.2 Add `UpdateQuestionRequest` data class to `Dtos.kt` with `question`, `category` fields
  - [x] 2.3 Add `UpdateAnswerRequest` data class to `Dtos.kt` with `content` field
  - [x] 2.4 Add `UpdateProfileRequest` data class to `Dtos.kt` with optional `displayName` and `avatarUrl` fields
  - [x] 2.5 Add `@PATCH("flashcards/{id}") suspend fun updateFlashcard()` to `EduSpecialApiService`
  - [x] 2.6 Add `@PATCH("questions/{id}") suspend fun updateQuestion()` to `EduSpecialApiService`
  - [x] 2.7 Add `@PATCH("answers/{id}") suspend fun updateAnswer()` to `EduSpecialApiService`
  - [x] 2.8 Add `@PATCH("users/me") suspend fun updateProfile()` to `EduSpecialApiService`

---

## Phase 3 — Domain Models and New Repositories

- [x] 3. Add new domain models and repositories
  - [x] 3.1 Add `BookmarkType` enum (`FLASHCARD`, `QUESTION`), `BookmarkCollection`, `DailyProgress`, `CategoryMastery`, `CategoryMasteryRow` to `Models.kt`
  - [x] 3.2 Extend `PendingSubmissionEntity` type constants to include `FLASHCARD_EDIT`, `QUESTION_EDIT`, `ANSWER_EDIT` (document as constants or sealed class)
  - [x] 3.3 Add `KEY_REMINDER_TIME`, `KEY_DISPLAY_NAME`, `KEY_AVATAR_URL` preference keys and corresponding Flow properties and setters to `UserPreferencesDataStore`
  - [x] 3.4 Create `BookmarkRepository` with `getAllBookmarks()`, `isBookmarked()`, `toggle()`, `removeOrphans()` — purely local, no network calls
  - [x] 3.5 Create `AnalyticsRepository` with `recordReview()`, `getStreak()`, `getLast7Days()`, `getTodayReviewCount()` — streak algorithm: count consecutive days ending today with `reviewCount > 0`
  - [x] 3.6 Add `editFlashcard()` to `FlashcardRepository` — calls `PATCH flashcards/{id}`, on failure writes `FLASHCARD_EDIT` entry to `PendingSubmissionDao`
  - [x] 3.7 Add `editQuestion()`, `editAnswer()`, `acceptAnswer()`, `upvoteAnswer()` to `QARepository`
  - [x] 3.8 Add `updateDisplayName()`, `updateAvatarUrl()` to `AuthRepository` — both call `PATCH users/me` and update `UserPreferencesDataStore`
  - [x] 3.9 Add `BookmarkRepository` and `AnalyticsRepository` providers to `AppModule`
  - [x] 3.10 Add `BookmarkDao` and `AnalyticsDao` providers to `AppModule`

---

## Phase 4 — Use Cases

- [x] 4. Implement all new use cases in `UseCases.kt`
  - [x] 4.1 `EditFlashcardUseCase` — delegates to `FlashcardRepository.editFlashcard()`
  - [x] 4.2 `EditQuestionUseCase` — delegates to `QARepository.editQuestion()`
  - [x] 4.3 `EditAnswerUseCase` — delegates to `QARepository.editAnswer()`
  - [x] 4.4 `AcceptAnswerUseCase` — delegates to `QARepository.acceptAnswer()`, then marks parent question as answered
  - [x] 4.5 `UpvoteAnswerUseCase` — delegates to `QARepository.upvoteAnswer()` with optimistic local update
  - [x] 4.6 `ToggleBookmarkUseCase` — delegates to `BookmarkRepository.toggle()`, returns new boolean state
  - [x] 4.7 `GetBookmarksUseCase` — returns `Flow<BookmarkCollection>` from `BookmarkRepository`
  - [x] 4.8 `RecordReviewUseCase` — delegates to `AnalyticsRepository.recordReview()`
  - [x] 4.9 `GetStudyStreakUseCase` — delegates to `AnalyticsRepository.getStreak()`
  - [x] 4.10 `GetWeeklyProgressUseCase` — delegates to `AnalyticsRepository.getLast7Days()`
  - [x] 4.11 `GetCategoryMasteryUseCase` — delegates to `FlashcardRepository.getCategoryMastery()`
  - [x] 4.12 `UpdateDisplayNameUseCase` — validates length [2,50], delegates to `AuthRepository.updateDisplayName()`
  - [x] 4.13 `UploadAvatarUseCase` — calls `CloudinaryService.uploadMedia(uri, folder="avatars")`, then `AuthRepository.updateAvatarUrl()`
  - [x] 4.14 `ScheduleStudyReminderUseCase` — delegates to `NotificationScheduler.schedule()`

---

## Phase 5 — Background Sync Worker

- [x] 5. Implement `SyncWorker` and `StudyReminderWorker`
  - [x] 5.1 Implement `SyncWorker.doWork()`: fetch all `PendingSubmissionEntity` rows, attempt each submission via `EduSpecialApiService`, delete on success, increment `retryCount` on retryable failure, delete at `retryCount >= 5`
  - [x] 5.2 After processing pending items, call `syncFlashcards(since = lastSync)` and `syncQuestions(since = lastSync)`, upsert results into Room, update `KEY_LAST_SYNC`
  - [x] 5.3 Return `Result.retry()` for attempts 1–3 on unrecoverable error, `Result.failure()` on attempt 4+
  - [x] 5.4 Add `triggerImmediateSync()` companion function to `SyncWorker` for pull-to-refresh
  - [x] 5.5 Create `NotificationScheduler` singleton: `schedule(enabled, reminderTimeMillis)` enqueues/cancels `OneTimeWorkRequest` for `StudyReminderWorker` with calculated initial delay
  - [x] 5.6 Create `StudyReminderWorker`: check `KEY_STUDY_NOTIFICATIONS`, check daily goal completion, get due card count, fire notification via `NotificationCompat.Builder` with deep-link `PendingIntent` to Study screen
  - [x] 5.7 Register `study_reminder_channel` (`NotificationChannel`) in `EduSpecialApp.onCreate()` for API 26+
  - [x] 5.8 Add `POST_NOTIFICATIONS` permission to `AndroidManifest.xml` and request it at runtime on Android 13+ when user enables notifications toggle
  - [x] 5.9 Add `NotificationScheduler` provider to `AppModule`

---

## Phase 6 — ViewModel Updates

- [x] 6. Update existing ViewModels and create new ones
  - [x] 6.1 Create `OnboardingViewModel`: `currentPage: StateFlow<Int>`, `nextPage()`, `skip()`, `complete()` (sets `KEY_ONBOARDING_DONE = true`)
  - [x] 6.2 Create `BookmarksViewModel`: `flashcardBookmarks`, `questionBookmarks`, `selectedTab`, `selectTab()` — driven by `GetBookmarksUseCase`
  - [x] 6.3 Update `HomeViewModel`: add `streak`, `weeklyProgress`, `categoryMastery`, `todayReviewed`, `isLoading` StateFlows; load all on `init`
  - [x] 6.4 Update `FlashcardsViewModel`: add `editFlashcard()`, `toggleBookmark()`, `deleteFlashcard()` with undo support, `isLoading`, `currentUserId`, `bookmarkedIds: StateFlow<Set<String>>`
  - [x] 6.5 Update `QAViewModel`: add `editQuestion()`, `editAnswer()`, `acceptAnswer()`, `upvoteAnswer()`, `toggleBookmark()`, `expandedQuestionId: StateFlow<String?>`, `isLoading`
  - [x] 6.6 Update `StudyViewModel`: call `RecordReviewUseCase` after each `processReview()` call; add `exoPlayer` lifecycle management with `DisposableEffect`; add `isLoading`
  - [x] 6.7 Update `ProfileViewModel`: add `updateDisplayName()`, `uploadAvatar()`, `isEditingName`, `isUploadingAvatar`, `avatarUrl`, `displayName` StateFlows; wire `ScheduleStudyReminderUseCase` to notifications toggle

---

## Phase 7 — Navigation Updates

- [x] 7. Update navigation to add Onboarding and Bookmarks routes
  - [x] 7.1 Add `Screen.Onboarding` and `Screen.Bookmarks` to the `Screen` sealed class in `Navigation.kt`
  - [x] 7.2 Add `Screen.Onboarding.route` to `noBottomBarRoutes`
  - [x] 7.3 Update `EduSpecialNavHost` startup logic: after Firebase user check, read `KEY_ONBOARDING_DONE` from `UserPreferencesDataStore`; if false, start at `Onboarding`; if true, start at `Home`
  - [x] 7.4 Add `composable(Screen.Onboarding.route)` and `composable(Screen.Bookmarks.route)` to `NavHost`

---

## Phase 8 — Media Upload Integration

- [x] 8. Wire `MediaPickerSection` into `AddFlashcardDialog`
  - [x] 8.1 Add `MediaPickerSection` composable below the definition field inside `AddFlashcardDialog` in `FlashcardsScreen.kt`
  - [x] 8.2 Connect `MediaUploadViewModel` to the dialog: observe `uiState.uploadedUrl` and `uiState.uploadedMediaType` via `LaunchedEffect`
  - [x] 8.3 Pass `mediaUrl` and `mediaType` from `MediaUploadViewModel` state into `FlashcardsViewModel.createFlashcard()` on submit
  - [x] 8.4 Show upload progress indicator (0–100%) inside `MediaPickerSection` while `uiState.isUploading == true`
  - [x] 8.5 Show media preview with a clear button when `uiState.uploadedUrl != null`; on clear, reset `MediaUploadViewModel` state and set `mediaUrl = null`, `mediaType = NONE`
  - [x] 8.6 Show Arabic error message and retry button inside `MediaPickerSection` when `uiState.error != null`
  - [x] 8.7 Add audio picker button to `MediaPickerSection` alongside existing image and video buttons

---

## Phase 9 — Flashcard Editing

- [x] 9. Implement flashcard edit dialog and flow
  - [x] 9.1 Add edit `IconButton` to `FlashcardItem` composable, visible only when `currentUserId == card.contributor`
  - [x] 9.2 Create `EditFlashcardDialog` composable pre-populated with existing term, definition, category, and media; reuse `MediaPickerSection` for media replacement
  - [x] 9.3 Wire `EditFlashcardDialog` to `FlashcardsViewModel.editFlashcard()` on submit
  - [x] 9.4 Duplicate check in edit dialog excludes the card being edited (pass `excludeId` to `checkDuplicate`)
  - [x] 9.5 On successful edit, dismiss dialog and update the card in the list via Room Flow

---

## Phase 10 — Q&A Editing and Answer Management

- [x] 10. Implement Q&A editing and inline answer thread
  - [x] 10.1 Add edit `IconButton` to question cards, visible only when `currentUserId == question.contributor`
  - [x] 10.2 Create `EditQuestionDialog` composable pre-populated with question text and category; wire to `QAViewModel.editQuestion()`
  - [x] 10.3 Create `AnswerThreadSection` composable: expandable inline section showing answers with content, contributor, upvote count, upvote button, accept button (for question author), edit button (for answer author)
  - [x] 10.4 Wire upvote button to `QAViewModel.upvoteAnswer()` with optimistic +1 update
  - [x] 10.5 Wire accept button to `QAViewModel.acceptAnswer()`; show green checkmark on accepted answer; move accepted answer to top of list
  - [x] 10.6 Add edit `IconButton` to answer items, visible only when `currentUserId == answer.contributor`; open `EditAnswerDialog` pre-populated with content
  - [x] 10.7 Show green "answered" badge on question cards that have `isAnswered == true`
  - [x] 10.8 Show answer count and expand/collapse toggle on each question card

---

## Phase 11 — Avatar Upload and Display Name Editing

- [x] 11. Implement profile customization
  - [x] 11.1 Create `AvatarSection` composable: show initials if no `avatarUrl`, show `AsyncImage` (Coil) if `avatarUrl` is set, show `CircularProgressIndicator` overlay while `isUploadingAvatar == true`
  - [x] 11.2 Wire avatar tap to system image picker (`ActivityResultContracts.PickVisualMedia`); on selection call `ProfileViewModel.uploadAvatar(uri)`
  - [x] 11.3 On upload success, update `avatarUrl` StateFlow and display new image immediately without restart
  - [x] 11.4 On upload failure, show Arabic error snackbar: "فشل رفع الصورة، حاول مرة أخرى"
  - [x] 11.5 Create `DisplayNameEditor` composable: show name + edit `IconButton` when `isEditingName == false`; show `OutlinedTextField` + confirm/cancel buttons when `isEditingName == true`
  - [x] 11.6 Validate display name length [2, 50] in `UpdateDisplayNameUseCase`; show Arabic error for invalid input
  - [x] 11.7 On successful name update, dismiss editor and update displayed name immediately
  - [x] 11.8 Replace `ProfileScreen` avatar placeholder and name display with `AvatarSection` and `DisplayNameEditor`

---

## Phase 12 — Analytics Dashboard

- [x] 12. Build analytics dashboard on Home screen
  - [x] 12.1 Create `StreakCard` composable: flame icon + streak count + "يوم متتالي" label
  - [x] 12.2 Create `WeeklyBarChart` composable using `Canvas` API: 7 bars representing last 7 days' review counts, with day labels (Sa, Su, Mo…) in Arabic
  - [x] 12.3 Create `DailyGoalProgressBar` composable: linear progress bar showing `todayReviewed / dailyGoal` with count label
  - [x] 12.4 Create `CategoryMasteryList` composable: one row per `FlashcardCategory` showing category name, mastery percentage, and a thin progress bar
  - [x] 12.5 Compose all four into `AnalyticsDashboard` composable and embed it in `HomeScreen` below the quick actions section
  - [x] 12.6 Show `ChartSkeleton` and `StatCardSkeleton` placeholders while `HomeViewModel.isLoading == true`

---

## Phase 13 — Bookmarks

- [x] 13. Implement bookmarks feature end-to-end
  - [x] 13.1 Add bookmark `IconButton` (outlined/filled toggle) to `FlashcardItem` composable; wire to `FlashcardsViewModel.toggleBookmark()`
  - [x] 13.2 Add bookmark `IconButton` to question cards in `QAScreen`; wire to `QAViewModel.toggleBookmark()`
  - [x] 13.3 Create `BookmarksScreen` composable: `TopAppBar("المحفوظات")`, `TabRow` (بطاقات | أسئلة), `LazyColumn` of bookmarked items, `LottieEmptyState` when empty
  - [x] 13.4 Add "المحفوظات" quick action card to `HomeScreen` that navigates to `Screen.Bookmarks`
  - [x] 13.5 Add "المحفوظات" settings item to `ProfileScreen` that navigates to `Screen.Bookmarks`
  - [x] 13.6 During `SyncWorker` execution, call `BookmarkRepository.removeOrphans()` with IDs of deleted server items

---

## Phase 14 — Onboarding Flow

- [x] 14. Build onboarding screen
  - [x] 14.1 Add three Lottie animation JSON files to `res/raw/`: `lottie_flashcards.json`, `lottie_study.json`, `lottie_qa.json` (use free LottieFiles assets)
  - [x] 14.2 Create `OnboardingPage` composable: `LottieAnimation` + title `Text` + description `Text`
  - [x] 14.3 Create `OnboardingScreen` composable: `HorizontalPager` with 3 `OnboardingPage` items, `PageIndicatorRow` (3 dots), bottom row with "تخطي" TextButton and "التالي"/"ابدأ الآن" Button
  - [x] 14.4 Wire "التالي" to `OnboardingViewModel.nextPage()`, "تخطي" and "ابدأ الآن" to `OnboardingViewModel.complete()` then navigate to `Screen.Home`
  - [x] 14.5 Ensure `OnboardingScreen` is shown only when `KEY_ONBOARDING_DONE == false` (handled in navigation task 7.3)

---

## Phase 15 — UI Polish: Category Filter

- [x] 15. Replace category filter chips with scrollable `LazyRow`
  - [x] 15.1 Create `CategoryFilterLazyRow` composable with "الكل" chip + all 10 `FlashcardCategory` chips as `FilterChip` components in a `LazyRow`
  - [x] 15.2 Replace the existing `FlowRow`/chip implementation in `FlashcardsScreen` with `CategoryFilterLazyRow`
  - [x] 15.3 Verify that selecting a chip updates the flashcard list within 100ms (driven by existing `FlashcardsViewModel` filter state)

---

## Phase 16 — UI Polish: Media Playback in Study Screen

- [x] 16. Add media playback to `StudyScreen` card back face
  - [x] 16.1 Create `MediaPlayerCard` composable: `AsyncImage` for `IMAGE`, `AndroidView(PlayerView)` for `VIDEO`, custom audio controls row for `AUDIO`
  - [x] 16.2 Integrate `MediaPlayerCard` into the back face of `StudyCard` in `StudyScreen.kt`
  - [x] 16.3 Manage `ExoPlayer` lifecycle in `StudyViewModel`: create on session start, release on card advance and session end using `DisposableEffect`
  - [x] 16.4 Show Arabic error message "تعذّر تحميل الوسائط" when ExoPlayer reports a playback error
  - [x] 16.5 Ensure media does not load or play while the card is on the front face

---

## Phase 17 — UI Polish: Empty States

- [x] 17. Replace all placeholder empty states with Lottie animations
  - [x] 17.1 Create `LottieEmptyState` composable: `LottieAnimation` (looping) + message `Text` + optional CTA `Button`
  - [x] 17.2 Replace flashcard list empty state with `LottieEmptyState("لا توجد بطاقات بعد")` + "أضف أول بطاقة" button
  - [x] 17.3 Replace Q&A list empty state with `LottieEmptyState("لا توجد أسئلة بعد")` + "اطرح أول سؤال" button
  - [x] 17.4 Replace search no-results state with `LottieEmptyState("لا توجد نتائج")` + "جرّب كلمات مختلفة" hint
  - [x] 17.5 Add `LottieEmptyState("لا توجد محفوظات بعد")` to `BookmarksScreen` when both tabs are empty
  - [x] 17.6 Replace study queue empty state with `LottieEmptyState("أحسنت! لا توجد بطاقات للمراجعة اليوم")`

---

## Phase 18 — UI Polish: Skeleton Loaders

- [x] 18. Implement shimmer skeleton loaders
  - [x] 18.1 Create `SkeletonLoader` base composable with infinite shimmer animation cycling between `surfaceVariant` and `surface` colors
  - [x] 18.2 Create `FlashcardItemSkeleton` composable matching the shape of `FlashcardItem`
  - [x] 18.3 Create `QuestionCardSkeleton` composable matching the shape of the question card
  - [x] 18.4 Create `StatCardSkeleton` composable matching the shape of the Home stats cards
  - [x] 18.5 Create `ChartSkeleton` composable matching the shape of `WeeklyBarChart`
  - [x] 18.6 Show 3× `FlashcardItemSkeleton` in `FlashcardsScreen` while `FlashcardsViewModel.isLoading == true`
  - [x] 18.7 Show 3× `QuestionCardSkeleton` in `QAScreen` while `QAViewModel.isLoading == true`
  - [x] 18.8 Show `StatCardSkeleton` × 3 and `ChartSkeleton` in `HomeScreen` while `HomeViewModel.isLoading == true`
  - [x] 18.9 Replace all skeletons with actual content using a 300ms crossfade transition when loading completes

---

## Phase 19 — UI Polish: Swipe-to-Dismiss and Pull-to-Refresh

- [x] 19. Add swipe gestures and pull-to-refresh to list screens
  - [x] 19.1 Create `SwipeToDismissFlashcardItem` composable wrapping `FlashcardItem` in `SwipeToDismiss`; `endToStart` reveals delete (red background + trash icon, only for author); `startToEnd` reveals bookmark (blue background + bookmark icon)
  - [x] 19.2 RTL-aware swipe: reverse directions when `LocalLayoutDirection.current == LayoutDirection.Rtl`
  - [x] 19.3 On swipe-to-delete confirmation, call `FlashcardsViewModel.deleteFlashcard()`; show undo `Snackbar` for 4 seconds
  - [x] 19.4 On swipe-to-bookmark, call `FlashcardsViewModel.toggleBookmark()`
  - [x] 19.5 Add `PullToRefreshBox` (Material 3) to `FlashcardsScreen`; on refresh call `SyncWorker.triggerImmediateSync()`
  - [x] 19.6 Add `PullToRefreshBox` to `QAScreen`; on refresh call `QAViewModel.refreshFromServer()`
  - [x] 19.7 Add `PullToRefreshBox` to `HomeScreen`; on refresh reload all stats

---

## Phase 20 — UI Polish: RTL and Accessibility

- [x] 20. Enforce RTL layout and add accessibility attributes
  - [x] 20.1 Add `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)` at the root of `EduSpecialNavHost` to enforce RTL for all screens
  - [x] 20.2 Audit all `Icon` composables across all screens; add Arabic `contentDescription` to every icon that conveys meaning and has no visible label
  - [x] 20.3 Verify `android:supportsRtl="true"` is set in `AndroidManifest.xml` (already present; confirm no regression)
  - [x] 20.4 Test with system font scale 1.5×: ensure no text truncation in `FlashcardItem`, `QuestionCard`, or dialog composables; use `sp` units throughout and avoid fixed-height containers for text

---

## Phase 21 — Property-Based Tests

- [x] 21. Write Kotest property-based tests for all 12 correctness properties
  - [x] 21.1 Add `io.kotest:kotest-property` dependency to `app/build.gradle.kts` test dependencies
  - [x] 21.2 **P1** — Edit icon authorship (flashcard): `forAll(Arb.string(), Arb.string())` verifying `editIconVisible(card, userId) == (card.contributor == userId)`
  - [x] 21.3 **P2** — Duplicate check excludes self: verify that `checkDuplicate(term, excludeId = card.id)` returns `NotDuplicate` when the only match is the card being edited
  - [x] 21.4 **P3** — Edit icon authorship (Q&A): same pattern as P1 for `QAQuestion` and `QAAnswer`
  - [x] 21.5 **P4** — Accepted answer ordering: `forAll` list of answers with one accepted; verify accepted answer is at index 0 after sorting
  - [x] 21.6 **P5** — Upvote increments count: `forAll(Arb.nonNegativeInt())` initial count N; after upvote, displayed count == N + 1
  - [x] 21.7 **P6** — Idempotent offline sync: `forAll(Arb.list(flashcardArb, 1..20))`; offline create then sync == online create (no duplicates, all fields preserved, `isPendingSync == false`)
  - [x] 21.8 **P7** — Streak invariant: `forAll(Arb.list(Arb.nonNegativeInt(), 0..365))`; computed streak == length of longest suffix of consecutive non-zero days ending today
  - [x] 21.9 **P8** — Category mastery formula: `forAll(Arb.positiveInt(100), Arb.int(0..100))`; `mastery.percentage == archived.toFloat() / total`
  - [x] 21.10 **P9** — Bookmark toggle idempotence and round-trip: `forAll(Arb.boolean())`; toggle twice returns original state; toggle once changes state
  - [x] 21.11 **P10** — Display name rejects invalid lengths: `forAll(Arb.choice(Arb.string(0..1), Arb.string(51..200)))`; `validateDisplayName(name).isError == true`
  - [x] 21.12 **P11** — Display name round-trip: `forAll(Arb.string(2..50))`; `validateDisplayName(name).isValid && result.value == name`
  - [x] 21.13 **P12** — Media URL propagation: `forAll(Arb.string(1..500), Arb.enum<MediaType>())`; `request.mediaUrl == url && request.mediaType == mediaType.name`

---

## Phase 22 — Integration and UI Tests

- [x] 22. Write integration and Compose UI tests
  - [x] 22.1 Write `MigrationTestHelper` test verifying Room migration 1→2 creates `bookmarks` and `daily_review_logs` tables
  - [x] 22.2 Write `MockWebServer` tests for all four PATCH endpoints verifying request shape and response mapping
  - [x] 22.3 Write `TestListenableWorkerBuilder` test for `SyncWorker`: pending items processed, retryCount incremented, deleted at 5
  - [x] 22.4 Write `TestListenableWorkerBuilder` test for `StudyReminderWorker`: notification suppressed when goal met; fired when cards are due
  - [x] 22.5 Write Compose UI test for `OnboardingScreen`: page navigation, skip, complete, page indicator dots
  - [x] 22.6 Write Compose UI test for `FlashcardsScreen`: category filter updates list; swipe-to-delete shows confirmation snackbar; swipe-to-bookmark fills icon
  - [x] 22.7 Write Compose UI test for `StudyScreen`: media player renders on card flip; player releases on card advance
  - [x] 22.8 Write Compose UI test for `ProfileScreen`: avatar tap opens picker; display name validation shows error for short input
  - [x] 22.9 Write Compose UI test for `BookmarksScreen`: tab switching; empty state shown when no bookmarks
  - [x] 22.10 Write Compose UI test for `HomeScreen`: analytics dashboard shows streak card, chart, mastery list; skeleton shown while loading
  - [x] 22.11 Enable `AccessibilityChecks.enable()` in all Compose UI tests to catch missing `contentDescription` values
