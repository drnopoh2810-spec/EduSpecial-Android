package com.eduspecial.presentation.flashcards

import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eduspecial.domain.model.Flashcard
import com.eduspecial.domain.model.FlashcardCategory
import com.eduspecial.domain.model.MediaType
import com.eduspecial.presentation.media.MediaPickerSection
import com.eduspecial.presentation.media.MediaUploadViewModel
import com.eduspecial.presentation.navigation.Screen
import com.eduspecial.presentation.common.LottieEmptyState
import com.eduspecial.presentation.common.FlashcardItemSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(    navController: NavController,
    innerPadding: PaddingValues,
    viewModel: FlashcardsViewModel = hiltViewModel()
) {
    val flashcards by viewModel.flashcards.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()
    val currentUserId = viewModel.currentUserId
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCard by remember { mutableStateOf<Flashcard?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Handle undo snackbar
    val undoFlashcard = uiState.undoFlashcard
    LaunchedEffect(undoFlashcard) {
        if (undoFlashcard != null) {
            val result = snackbarHostState.showSnackbar(
                message = "تم حذف البطاقة",
                actionLabel = "تراجع",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("البطاقات التعليمية", fontWeight = FontWeight.Bold) },
                actions = {
                    // زر الإضافة بارز في الـ TopAppBar دائماً
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.semantics { contentDescription = "إضافة مصطلح جديد" }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Study.route) }) {
                        Icon(Icons.Default.School, contentDescription = "مراجعة")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                        Icon(Icons.Default.Search, contentDescription = "بحث")
                    }
                }
            )
        },
        floatingActionButton = {
            // FAB كبير وبارز دائماً في أسفل الشاشة
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = { Text("إضافة مصطلح", style = MaterialTheme.typography.labelLarge) },
                modifier = Modifier.semantics { contentDescription = "إضافة مصطلح جديد للقاعدة المشتركة" },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0)
    ) { scaffoldPadding ->
        // Pull-to-refresh state
        var isRefreshing by remember { mutableStateOf(false) }
        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                com.eduspecial.utils.SyncWorker.triggerImmediateSync(context)
                kotlinx.coroutines.delay(1500)
                isRefreshing = false
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryFilterLazyRow(
                        selected = uiState.selectedCategory,
                        onCategorySelected = viewModel::filterByCategory
                    )
                    Spacer(Modifier.height(4.dp))
                }

                if (uiState.isLoading) {
                    items(3) { FlashcardItemSkeleton() }
                } else if (flashcards.isEmpty()) {
                    item {
                        LottieEmptyState(
                            message = "لا توجد بطاقات بعد",
                            actionLabel = "أضف أول بطاقة",
                            onAction = { showAddDialog = true }
                        )
                    }
                } else {
                    items(flashcards, key = { it.id }) { card ->
                        // Filter out the card pending undo-delete
                        if (card.id != uiState.undoFlashcard?.id) {
                            SwipeToDismissFlashcardItem(
                                card = card,
                                currentUserId = currentUserId,
                                isBookmarked = card.id in bookmarkedIds,
                                onDelete = { viewModel.deleteFlashcard(card) },
                                onBookmark = { viewModel.toggleBookmark(card.id) },
                                onEdit = { editingCard = card },
                                onSpeak = { viewModel.speakTerm(card.term) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddFlashcardDialog(
            uiState = uiState,
            onTermChange = viewModel::onTermChange,
            onDefinitionChange = viewModel::onDefinitionChange,
            onCategoryChange = viewModel::onCategoryChange,
            onSubmit = { mediaUrl, mediaType ->
                viewModel.submitFlashcardWithMedia(mediaUrl, mediaType)
                // Dialog closes automatically when isSubmitting goes false (see LaunchedEffect below)
            },
            onDismiss = { showAddDialog = false }
        )
        // Close dialog once submission completes successfully (isSubmitting: true → false)
        val wasSubmitting = remember { mutableStateOf(false) }
        LaunchedEffect(uiState.isSubmitting) {
            if (wasSubmitting.value && !uiState.isSubmitting && uiState.error == null) {
                showAddDialog = false
            }
            wasSubmitting.value = uiState.isSubmitting
        }
    }

    editingCard?.let { card ->
        EditFlashcardDialog(
            card = card,
            onSubmit = { term, definition, category, mediaUrl, mediaType ->
                viewModel.editFlashcard(card.id, term, definition, category, mediaUrl, mediaType)
                editingCard = null
            },
            onDismiss = { editingCard = null }
        )
    }
}

@Composable
fun CategoryFilterLazyRow(
    selected: FlashcardCategory?,
    onCategorySelected: (FlashcardCategory?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onCategorySelected(null) },
                label = { Text("الكل") }
            )
        }
        items(FlashcardCategory.values()) { cat ->
            FilterChip(
                selected = selected == cat,
                onClick = { onCategorySelected(cat) },
                label = { Text(categoryArabicName(cat)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissFlashcardItem(
    card: Flashcard,
    currentUserId: String,
    isBookmarked: Boolean,
    onDelete: () -> Unit,
    onBookmark: () -> Unit,
    onEdit: () -> Unit,
    onSpeak: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    if (currentUserId == card.contributor) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDelete()
                    }
                    false
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onBookmark()
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            when (direction) {
                SwipeToDismissBoxValue.EndToStart -> {
                    // Delete background (only for author)
                    if (currentUserId == card.contributor) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "حذف",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    // Bookmark background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Icon(
                            if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "حفظ",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                else -> {}
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = currentUserId == card.contributor
    ) {
        FlashcardItem(
            card = card,
            currentUserId = currentUserId,
            isBookmarked = isBookmarked,
            onBookmark = onBookmark,
            onEdit = onEdit,
            onSpeak = onSpeak
        )
    }
}

@Composable
fun FlashcardItem(
    card: Flashcard,
    currentUserId: String = "",
    isBookmarked: Boolean = false,
    onBookmark: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onSpeak: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "بطاقة: ${card.term}. ${card.definition}"
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = card.term,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        // Speak button next to the term
                        if (onSpeak != null) {
                            IconButton(
                                onClick = onSpeak,
                                modifier = Modifier
                                    .size(28.dp)
                                    .semantics { contentDescription = "استمع لنطق ${card.term}" }
                            ) {
                                Icon(
                                    Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = card.definition,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3
                    )
                }
                Row {
                    if (onBookmark != null) {
                        IconButton(onClick = onBookmark, modifier = Modifier.size(32.dp)) {
                            Icon(
                                if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isBookmarked) "إلغاء الحفظ" else "حفظ",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (onEdit != null && currentUserId == card.contributor) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "تعديل",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // ── Media Preview — compact, doesn't dominate the card layout ──────
            if (card.mediaType != MediaType.NONE && card.mediaUrl != null) {
                var mediaExpanded by remember { mutableStateOf(false) }

                when (card.mediaType) {
                    MediaType.IMAGE -> {
                        // Thumbnail row — tapping expands to full width
                        if (!mediaExpanded) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { mediaExpanded = true }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                coil.compose.AsyncImage(
                                    model = card.mediaUrl,
                                    contentDescription = "صورة مرفقة",
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                                Text(
                                    "صورة مرفقة — اضغط للعرض",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Column {
                                coil.compose.AsyncImage(
                                    model = card.mediaUrl,
                                    contentDescription = "صورة مرفقة",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { mediaExpanded = false },
                                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                )
                                TextButton(
                                    onClick = { mediaExpanded = false },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("إخفاء", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    MediaType.VIDEO -> {
                        // Collapsed: small icon row — expanded: player
                        if (!mediaExpanded) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable { mediaExpanded = true }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    "فيديو مرفق — اضغط للتشغيل",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Column {
                                com.eduspecial.presentation.common.MediaPlayerView(
                                    url = card.mediaUrl,
                                    isAudio = false,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                )
                                TextButton(
                                    onClick = { mediaExpanded = false },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("إخفاء", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    MediaType.AUDIO -> {
                        // Collapsed: small icon row — expanded: audio player
                        if (!mediaExpanded) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable { mediaExpanded = true }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.AudioFile,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    "صوت مرفق — اضغط للاستماع",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Column {
                                com.eduspecial.presentation.common.MediaPlayerView(
                                    url = card.mediaUrl,
                                    isAudio = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                TextButton(
                                    onClick = { mediaExpanded = false },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("إخفاء", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    else -> {}
                }
                Spacer(Modifier.height(4.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text(categoryArabicName(card.category), style = MaterialTheme.typography.labelLarge) }
                )
                AssistChip(
                    onClick = {},
                    label = { Text(reviewStateArabicName(card.reviewState.name)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFlashcardDialog(
    uiState: FlashcardsUiState,
    onTermChange: (String) -> Unit,
    onDefinitionChange: (String) -> Unit,
    onCategoryChange: (FlashcardCategory) -> Unit,
    onSubmit: (mediaUrl: String?, mediaType: MediaType) -> Unit,
    onDismiss: () -> Unit,
    mediaUploadViewModel: MediaUploadViewModel = hiltViewModel()
) {
    var expandedCategory by remember { mutableStateOf(false) }
    var mediaUrl by remember { mutableStateOf<String?>(null) }
    var mediaType by remember { mutableStateOf(MediaType.NONE) }

    val uploadUiState by mediaUploadViewModel.uiState.collectAsState()

    // Audio picker launcher
    val audioPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { mediaUploadViewModel.uploadAudio(it) }
    }

    // Sync local mediaUrl/mediaType whenever upload completes.
    // Key on the full state snapshot so it re-fires even if the same URL is uploaded twice.
    LaunchedEffect(uploadUiState.uploadedUrl, uploadUiState.uploadedMediaType) {
        uploadUiState.uploadedUrl?.let { url ->
            mediaUrl = url
            mediaType = uploadUiState.uploadedMediaType
        }
    }

    // Derived: block submit while upload is in progress
    val isUploadInProgress = uploadUiState.isUploading
    val canSubmit = uiState.newTerm.isNotBlank() &&
            uiState.newDefinition.isNotBlank() &&
            !uiState.isDuplicate &&
            !uiState.isSubmitting &&
            !isUploadInProgress   // ← prevent submit before upload finishes

    AlertDialog(
        onDismissRequest = {
            // Don't dismiss while uploading or submitting
            if (!isUploadInProgress && !uiState.isSubmitting) onDismiss()
        },
        title = { Text("إضافة مصطلح جديد", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.newTerm,
                    onValueChange = onTermChange,
                    label = { Text("المصطلح (بالإنجليزية)") },
                    isError = uiState.isDuplicate,
                    supportingText = {
                        when {
                            uiState.isDuplicate -> Text(
                                "⚠ هذا المصطلح موجود بالفعل!",
                                color = MaterialTheme.colorScheme.error
                            )
                            uiState.isCheckingDuplicate -> Text("جاري التحقق من التكرار...")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.newDefinition,
                    onValueChange = onDefinitionChange,
                    label = { Text("التعريف") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    maxLines = 4
                )
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = it }
                ) {
                    OutlinedTextField(
                        value = categoryArabicName(uiState.newCategory),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("الفئة") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        FlashcardCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(categoryArabicName(cat)) },
                                onClick = { onCategoryChange(cat); expandedCategory = false }
                            )
                        }
                    }
                }

                // Media picker section
                MediaPickerSection(
                    mediaUrl = mediaUrl,
                    mediaType = mediaType,
                    onMediaSelected = { url, type ->
                        mediaUrl = url
                        mediaType = type
                    },
                    onMediaCleared = {
                        mediaUrl = null
                        mediaType = MediaType.NONE
                        mediaUploadViewModel.resetState()
                    },
                    onAudioPick = { audioPicker.launch("audio/*") },
                    viewModel = mediaUploadViewModel
                )

                // Upload error with retry
                uploadUiState.error?.let { error ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { mediaUploadViewModel.clearError() }) {
                            Text("إغلاق", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(mediaUrl, mediaType) },
                enabled = canSubmit
            ) {
                when {
                    isUploadInProgress -> {
                        // Show upload progress in the button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("جاري الرفع ${uploadUiState.uploadProgress}%")
                        }
                    }
                    uiState.isSubmitting -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    else -> Text("إرسال")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isUploadInProgress && !uiState.isSubmitting
            ) { Text("إلغاء") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFlashcardDialog(
    card: Flashcard,
    onSubmit: (term: String, definition: String, category: FlashcardCategory, mediaUrl: String?, mediaType: MediaType) -> Unit,
    onDismiss: () -> Unit,
    mediaUploadViewModel: MediaUploadViewModel = hiltViewModel()
) {
    var term by remember { mutableStateOf(card.term) }
    var definition by remember { mutableStateOf(card.definition) }
    var category by remember { mutableStateOf(card.category) }
    var mediaUrl by remember { mutableStateOf(card.mediaUrl) }
    var mediaType by remember { mutableStateOf(card.mediaType) }
    var expandedCategory by remember { mutableStateOf(false) }

    val uploadUiState by mediaUploadViewModel.uiState.collectAsState()

    val audioPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { mediaUploadViewModel.uploadAudio(it) }
    }

    LaunchedEffect(uploadUiState.uploadedUrl) {
        uploadUiState.uploadedUrl?.let { url ->
            mediaUrl = url
            mediaType = uploadUiState.uploadedMediaType
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعديل المصطلح", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = term,
                    onValueChange = { term = it },
                    label = { Text("المصطلح") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = definition,
                    onValueChange = { definition = it },
                    label = { Text("التعريف") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = it }
                ) {
                    OutlinedTextField(
                        value = categoryArabicName(category),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("الفئة") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        FlashcardCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(categoryArabicName(cat)) },
                                onClick = { category = cat; expandedCategory = false }
                            )
                        }
                    }
                }

                MediaPickerSection(
                    mediaUrl = mediaUrl,
                    mediaType = mediaType,
                    onMediaSelected = { url, type ->
                        mediaUrl = url
                        mediaType = type
                    },
                    onMediaCleared = {
                        mediaUrl = null
                        mediaType = MediaType.NONE
                        mediaUploadViewModel.resetState()
                    },
                    onAudioPick = { audioPicker.launch("audio/*") },
                    viewModel = mediaUploadViewModel
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(term, definition, category, mediaUrl, mediaType) },
                enabled = term.isNotBlank() && definition.isNotBlank()
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

fun categoryArabicName(cat: FlashcardCategory): String = when (cat) {
    FlashcardCategory.ABA_THERAPY -> "تحليل السلوك التطبيقي"
    FlashcardCategory.AUTISM_SPECTRUM -> "طيف التوحد"
    FlashcardCategory.SENSORY_PROCESSING -> "المعالجة الحسية"
    FlashcardCategory.SPEECH_LANGUAGE -> "النطق واللغة"
    FlashcardCategory.OCCUPATIONAL_THERAPY -> "العلاج الوظيفي"
    FlashcardCategory.BEHAVIORAL_INTERVENTION -> "التدخل السلوكي"
    FlashcardCategory.INCLUSIVE_EDUCATION -> "التعليم الشامل"
    FlashcardCategory.DEVELOPMENTAL_DISABILITIES -> "الإعاقات النمائية"
    FlashcardCategory.ASSESSMENT_TOOLS -> "أدوات التقييم"
    FlashcardCategory.FAMILY_SUPPORT -> "دعم الأسرة"
}

fun reviewStateArabicName(state: String): String = when (state) {
    "NEW" -> "جديد"
    "LEARNING" -> "قيد التعلم"
    "REVIEW" -> "للمراجعة"
    "ARCHIVED" -> "متقن"
    else -> state
}
