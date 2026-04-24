package com.eduspecial.presentation.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.eduspecial.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    innerPadding: PaddingValues,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut) {
            navController.navigate(Screen.Auth.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Show avatar error snackbar
    LaunchedEffect(uiState.avatarError) {
        uiState.avatarError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearAvatarError()
        }
    }

    var showGoalDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    // Password change feedback
    LaunchedEffect(uiState.passwordSuccess) {
        if (uiState.passwordSuccess) {
            snackbarHostState.showSnackbar("✅ تم تغيير كلمة المرور بنجاح")
            viewModel.clearPasswordState()
            showPasswordDialog = false
        }
    }

    // Avatar image picker
    val avatarPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.uploadAvatar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("الملف الشخصي", fontWeight = FontWeight.Bold) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0)
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Avatar section
            AvatarSection(
                avatarUrl = uiState.avatarUrl,
                displayName = uiState.displayName,
                isUploading = uiState.isUploadingAvatar,
                onTap = {
                    avatarPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            Spacer(Modifier.height(12.dp))

            // Display name editor
            DisplayNameEditor(
                displayName = uiState.displayName,
                isEditing = uiState.isEditingName,
                nameError = uiState.nameError,
                isUpdating = uiState.isUpdatingName,
                onEditStart = viewModel::startEditingName,
                onSubmit = viewModel::updateDisplayName,
                onCancel = viewModel::cancelEditingName
            )

            Text(
                text = uiState.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatChip(Modifier.weight(1f), "المساهمات", uiState.contributionCount.toString())
                StatChip(Modifier.weight(1f), "المتقنة", uiState.masteredCount.toString())
                StatChip(Modifier.weight(1f), "للمراجعة", uiState.reviewCount.toString())
            }

            Spacer(Modifier.height(32.dp))

            // Settings
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.EmojiEvents,
                        title = "لوحة المتصدرين",
                        onClick = { navController.navigate(Screen.Leaderboard.route) }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Bookmark,
                        title = "المحفوظات",
                        onClick = { navController.navigate(Screen.Bookmarks.route) }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.DarkMode,
                        title = "الوضع الليلي",
                        trailing = {
                            Switch(
                                checked = uiState.isDarkMode,
                                onCheckedChange = viewModel::toggleDarkMode
                            )
                        }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "إشعارات المراجعة",
                        trailing = {
                            Switch(
                                checked = uiState.notificationsEnabled,
                                onCheckedChange = viewModel::toggleNotifications
                            )
                        }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.TrackChanges,
                        title = "الهدف اليومي: ${uiState.dailyGoal} بطاقات",
                        onClick = { showGoalDialog = true }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "تغيير كلمة المرور",
                        onClick = { showPasswordDialog = true }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Logout,
                        title = "تسجيل الخروج",
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = viewModel::signOut
                    )
                }
            }
        }
    }

    if (showGoalDialog) {
        DailyGoalDialog(
            currentGoal = uiState.dailyGoal,
            onConfirm = { goal ->
                viewModel.setDailyGoal(goal)
                showGoalDialog = false
            },
            onDismiss = { showGoalDialog = false }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            isLoading = uiState.isChangingPassword,
            error = uiState.passwordError,
            onConfirm = { current, new ->
                viewModel.changePassword(current, new)
            },
            onDismiss = {
                viewModel.clearPasswordState()
                showPasswordDialog = false
            }
        )
    }
}

@Composable
fun AvatarSection(
    avatarUrl: String?,
    displayName: String,
    isUploading: Boolean,
    onTap: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .clickable(onClickLabel = "تغيير صورة الملف الشخصي") { onTap() },
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl != null) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "صورة الملف الشخصي",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = displayName.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Upload progress overlay
        if (isUploading) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                }
            }
        } else {
            // Camera badge — always visible so user knows it's tappable
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .then(
                        Modifier.padding(2.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayNameEditor(
    displayName: String,
    isEditing: Boolean,
    nameError: String?,
    isUpdating: Boolean,
    onEditStart: () -> Unit,
    onSubmit: (String) -> Unit,
    onCancel: () -> Unit
) {
    var editText by remember(isEditing) { mutableStateOf(displayName) }

    if (isEditing) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedTextField(
                value = editText,
                onValueChange = { editText = it },
                label = { Text("الاسم المعروض") },
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onSubmit(editText) },
                    enabled = !isUpdating && editText.isNotBlank()
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("حفظ")
                    }
                }
                TextButton(onClick = onCancel) { Text("إلغاء") }
            }
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onEditStart) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "تعديل الاسم",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatChip(modifier: Modifier, label: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (onClick != null)
        Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = title, onClick = onClick)
            .semantics { contentDescription = title }
    else
        Modifier
            .fillMaxWidth()
            .semantics { contentDescription = title }

    ListItem(
        modifier = modifier,
        headlineContent = { Text(title, color = titleColor) },
        leadingContent = { Icon(icon, contentDescription = null, tint = titleColor.copy(alpha = 0.7f)) },
        trailingContent = trailing
    )
}

@Composable
private fun DailyGoalDialog(
    currentGoal: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var goal by remember { mutableIntStateOf(currentGoal) }
    val options = listOf(5, 10, 15, 20, 30, 50)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("الهدف اليومي", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("اختر عدد البطاقات التي تريد مراجعتها يومياً:")
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { goal = option }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = goal == option,
                            onClick = { goal = option }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("$option بطاقة يومياً")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(goal) }) { Text("حفظ") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
private fun ChangePasswordDialog(
    isLoading: Boolean,
    error: String?,
    onConfirm: (currentPassword: String, newPassword: String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword     by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrent     by remember { mutableStateOf(false) }
    var showNew         by remember { mutableStateOf(false) }
    var showConfirm     by remember { mutableStateOf(false) }

    // Local validation
    val mismatch = confirmPassword.isNotEmpty() && newPassword != confirmPassword
    val tooShort = newPassword.isNotEmpty() && newPassword.length < 6
    val canSubmit = currentPassword.isNotBlank() &&
            newPassword.length >= 6 &&
            newPassword == confirmPassword &&
            !isLoading

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        icon = {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        },
        title = { Text("تغيير كلمة المرور", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Current password
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("كلمة المرور الحالية") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showCurrent)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showCurrent = !showCurrent }) {
                            Icon(
                                if (showCurrent) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showCurrent) "إخفاء" else "إظهار"
                            )
                        }
                    }
                )

                // New password
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("كلمة المرور الجديدة") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = tooShort,
                    supportingText = if (tooShort) {
                        { Text("6 أحرف على الأقل", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    visualTransformation = if (showNew)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showNew = !showNew }) {
                            Icon(
                                if (showNew) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showNew) "إخفاء" else "إظهار"
                            )
                        }
                    }
                )

                // Confirm new password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("تأكيد كلمة المرور الجديدة") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = mismatch,
                    supportingText = if (mismatch) {
                        { Text("كلمتا المرور غير متطابقتين", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    visualTransformation = if (showConfirm)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirm = !showConfirm }) {
                            Icon(
                                if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showConfirm) "إخفاء" else "إظهار"
                            )
                        }
                    }
                )

                // Server error
                if (error != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(currentPassword, newPassword) },
                enabled = canSubmit,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("تغيير")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("إلغاء")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
