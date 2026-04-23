package com.eduspecial.presentation.qa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eduspecial.domain.model.FlashcardCategory
import com.eduspecial.domain.model.QAAnswer
import com.eduspecial.domain.model.QAQuestion
import com.eduspecial.presentation.common.LottieEmptyState
import com.eduspecial.presentation.common.QuestionCardSkeleton
import com.eduspecial.presentation.flashcards.categoryArabicName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QAScreen(
    navController: NavController,
    innerPadding: PaddingValues,
    viewModel: QAViewModel = hiltViewModel()
) {
    val questions by viewModel.questions.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val expandedQuestionId by viewModel.expandedQuestionId.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedQuestionIds.collectAsState()
    val currentUserId = viewModel.currentUserId

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var answerDialogQuestion by remember { mutableStateOf<QAQuestion?>(null) }
    var editingQuestion by remember { mutableStateOf<QAQuestion?>(null) }
    var editingAnswer by remember { mutableStateOf<QAAnswer?>(null) }

    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.refreshFromServer()
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("الأسئلة والأجوبة", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("اطرح سؤالاً") }
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { scaffoldPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0; viewModel.showAll() }
                    ) {
                        Text("الكل", modifier = Modifier.padding(vertical = 12.dp))
                    }
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1; viewModel.showUnanswered() }
                    ) {
                        Text("بدون إجابة", modifier = Modifier.padding(vertical = 12.dp))
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = innerPadding.calculateBottomPadding() + 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.isLoading) {
                        items(3) { QuestionCardSkeleton() }
                    } else if (questions.isEmpty()) {
                        item {
                            LottieEmptyState(
                                message = "لا توجد أسئلة بعد",
                                actionLabel = "اطرح أول سؤال",
                                onAction = { showAddDialog = true }
                            )
                        }
                    } else {
                        items(questions, key = { it.id }) { question ->
                            QuestionCard(
                                question = question,
                                currentUserId = currentUserId,
                                isBookmarked = question.id in bookmarkedIds,
                                isExpanded = expandedQuestionId == question.id,
                                onUpvote = { viewModel.upvoteQuestion(question.id) },
                                onAnswer = { answerDialogQuestion = question },
                                onToggleExpand = { viewModel.toggleExpanded(question.id) },
                                onBookmark = { viewModel.toggleBookmark(question.id) },
                                onEdit = { editingQuestion = question },
                                onUpvoteAnswer = { answerId -> viewModel.upvoteAnswer(answerId) },
                                onAcceptAnswer = { answerId -> viewModel.acceptAnswer(answerId, question.id) },
                                onEditAnswer = { answer -> editingAnswer = answer }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddQuestionDialog(
            uiState = uiState,
            onQuestionChange = viewModel::onQuestionChange,
            onCategoryChange = viewModel::onCategoryChange,
            onSubmit = { viewModel.submitQuestion(); showAddDialog = false },
            onDismiss = { showAddDialog = false }
        )
    }

    answerDialogQuestion?.let { question ->
        AddAnswerDialog(
            question = question,
            uiState = uiState,
            onAnswerChange = viewModel::onAnswerChange,
            onSubmit = {
                viewModel.submitAnswer(question.id)
                answerDialogQuestion = null
            },
            onDismiss = { answerDialogQuestion = null }
        )
    }

    editingQuestion?.let { question ->
        EditQuestionDialog(
            question = question,
            onSubmit = { text, category ->
                viewModel.editQuestion(question.id, text, category)
                editingQuestion = null
            },
            onDismiss = { editingQuestion = null }
        )
    }

    editingAnswer?.let { answer ->
        EditAnswerDialog(
            answer = answer,
            onSubmit = { content ->
                viewModel.editAnswer(answer.id, content)
                editingAnswer = null
            },
            onDismiss = { editingAnswer = null }
        )
    }
}

@Composable
private fun QuestionCard(
    question: QAQuestion,
    currentUserId: String,
    isBookmarked: Boolean,
    isExpanded: Boolean,
    onUpvote: () -> Unit,
    onAnswer: () -> Unit,
    onToggleExpand: () -> Unit,
    onBookmark: () -> Unit,
    onEdit: () -> Unit,
    onUpvoteAnswer: (String) -> Unit,
    onAcceptAnswer: (String) -> Unit,
    onEditAnswer: (QAAnswer) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Answered badge
                    if (question.isAnswered) {
                        Surface(
                            color = Color(0xFF2E7D32).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "تمت الإجابة",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(2.dp))
                                Text(
                                    "مُجاب",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                        Spacer(Modifier.width(4.dp))
                    }
                    // Bookmark button
                    IconButton(onClick = onBookmark, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isBookmarked) "إلغاء الحفظ" else "حفظ",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    // Edit button (only for author)
                    if (currentUserId == question.contributor) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "تعديل السؤال",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            categoryArabicName(question.category),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = onUpvote,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.ThumbUp, contentDescription = "تصويت", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${question.upvotes}")
                }
                // Answer count + expand toggle
                TextButton(
                    onClick = onToggleExpand,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "إخفاء الإجابات" else "عرض الإجابات",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("${question.answers.size} إجابة")
                }
                TextButton(
                    onClick = onAnswer,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Reply, contentDescription = "إضافة إجابة", modifier = Modifier.size(16.dp))
                }
            }
            if (question.tags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    question.tags.take(3).forEach { tag ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text("#$tag", style = MaterialTheme.typography.labelLarge) }
                        )
                    }
                }
            }

            // Inline answer thread
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                AnswerThreadSection(
                    question = question,
                    answers = question.answers.sortedByDescending { it.isAccepted },
                    currentUserId = currentUserId,
                    onUpvote = onUpvoteAnswer,
                    onAccept = onAcceptAnswer,
                    onEdit = onEditAnswer
                )
            }
        }
    }
}

@Composable
private fun AnswerThreadSection(
    question: QAQuestion,
    answers: List<QAAnswer>,
    currentUserId: String,
    onUpvote: (String) -> Unit,
    onAccept: (String) -> Unit,
    onEdit: (QAAnswer) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider()
        if (answers.isEmpty()) {
            Text(
                "لا توجد إجابات بعد",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            answers.forEach { answer ->
                AnswerItem(
                    answer = answer,
                    isQuestionAuthor = currentUserId == question.contributor,
                    isAnswerAuthor = currentUserId == answer.contributor,
                    onUpvote = { onUpvote(answer.id) },
                    onAccept = { onAccept(answer.id) },
                    onEdit = { onEdit(answer) }
                )
            }
        }
    }
}

@Composable
private fun AnswerItem(
    answer: QAAnswer,
    isQuestionAuthor: Boolean,
    isAnswerAuthor: Boolean,
    onUpvote: () -> Unit,
    onAccept: () -> Unit,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (answer.isAccepted)
            Color(0xFF2E7D32).copy(alpha = 0.08f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (answer.isAccepted) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "إجابة مقبولة",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    text = answer.contributor,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    // Edit button (only for answer author)
                    if (isAnswerAuthor) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "تعديل الإجابة",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    // Accept button (only for question author, on non-accepted answers)
                    if (isQuestionAuthor && !answer.isAccepted) {
                        IconButton(onClick = onAccept, modifier = Modifier.size(28.dp)) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "قبول الإجابة",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    // Upvote button
                    TextButton(
                        onClick = onUpvote,
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.ThumbUp, contentDescription = "تصويت", modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(2.dp))
                        Text("${answer.upvotes}", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = answer.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EditQuestionDialog(
    question: QAQuestion,
    onSubmit: (String, FlashcardCategory) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(question.question) }
    var category by remember { mutableStateOf(question.category) }
    var expandedCategory by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعديل السؤال", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("السؤال") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
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
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(text, category) },
                enabled = text.isNotBlank()
            ) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
private fun EditAnswerDialog(
    answer: QAAnswer,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var content by remember { mutableStateOf(answer.content) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعديل الإجابة", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("الإجابة") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(content) },
                enabled = content.isNotBlank()
            ) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddQuestionDialog(
    uiState: QAUiState,
    onQuestionChange: (String) -> Unit,
    onCategoryChange: (FlashcardCategory) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    var expandedCategory by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("اطرح سؤالاً", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.newQuestion,
                    onValueChange = onQuestionChange,
                    label = { Text("سؤالك") },
                    isError = uiState.isDuplicate,
                    supportingText = {
                        when {
                            uiState.isDuplicate -> Text(
                                "⚠ سؤال مشابه موجود بالفعل!",
                                color = MaterialTheme.colorScheme.error
                            )
                            uiState.isCheckingDuplicate -> Text("جاري التحقق...")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
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
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = uiState.newQuestion.isNotBlank() &&
                        !uiState.isDuplicate &&
                        !uiState.isSubmitting
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("نشر")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
private fun AddAnswerDialog(
    question: QAQuestion,
    uiState: QAUiState,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة إجابة", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider()
                OutlinedTextField(
                    value = uiState.newAnswer,
                    onValueChange = onAnswerChange,
                    label = { Text("إجابتك") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = uiState.newAnswer.isNotBlank() && !uiState.isSubmitting
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("إرسال")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
