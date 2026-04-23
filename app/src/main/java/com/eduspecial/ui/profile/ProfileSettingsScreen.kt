package com.eduspecial.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eduspecial.data.model.UserPreferences
import com.eduspecial.data.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    viewModel: ProfileSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إعدادات الملف الشخصي") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            uiState.userProfile?.let { profile ->
                ProfileInfoSection(
                    profile = profile,
                    onUpdateProfile = viewModel::updateProfile
                )
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                PreferencesSection(
                    preferences = profile.preferences,
                    onUpdatePreferences = viewModel::updatePreferences
                )
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                SecuritySection(
                    onChangePassword = { showPasswordDialog = true },
                    onNavigateToSecurity = onNavigateToSecurity
                )
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                DangerZoneSection(
                    onDeleteAccount = { showDeleteAccountDialog = true }
                )
            }
            
            // Loading state
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Error display
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
    
    // Password change dialog
    if (showPasswordDialog) {
        PasswordChangeDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { currentPassword, newPassword ->
                viewModel.changePassword(currentPassword, newPassword)
                showPasswordDialog = false
            }
        )
    }
    
    // Delete account confirmation dialog
    if (showDeleteAccountDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteAccountDialog = false },
            onConfirm = {
                viewModel.deleteAccount()
                showDeleteAccountDialog = false
            }
        )
    }
}

@Composable
private fun ProfileInfoSection(
    profile: UserProfile,
    onUpdateProfile: (Map<String, Any>) -> Unit
) {
    var displayName by remember { mutableStateOf(profile.displayName) }
    var bio by remember { mutableStateOf(profile.bio ?: "") }
    var isEditing by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المعلومات الشخصية",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(
                    onClick = {
                        if (isEditing) {
                            onUpdateProfile(mapOf(
                                "displayName" to displayName,
                                "bio" to bio
                            ))
                        }
                        isEditing = !isEditing
                    }
                ) {
                    Text(if (isEditing) "حفظ" else "تعديل")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isEditing) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("الاسم المعروض") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("نبذة شخصية (اختياري)") },
                    leadingIcon = {
                        Icon(Icons.Default.Info, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    placeholder = { Text("اكتب نبذة قصيرة عنك...") }
                )
            } else {
                ProfileInfoItem(
                    icon = Icons.Default.Person,
                    label = "الاسم المعروض",
                    value = profile.displayName
                )
                
                ProfileInfoItem(
                    icon = Icons.Default.Email,
                    label = "البريد الإلكتروني",
                    value = profile.email,
                    trailing = {
                        if (profile.emailVerified) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "محقق",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "غير محقق",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                
                if (profile.bio?.isNotBlank() == true) {
                    ProfileInfoItem(
                        icon = Icons.Default.Info,
                        label = "النبذة الشخصية",
                        value = profile.bio
                    )
                }
                
                ProfileInfoItem(
                    icon = Icons.Default.Star,
                    label = "النقاط",
                    value = "${profile.points} نقطة"
                )
                
                ProfileInfoItem(
                    icon = Icons.Default.Create,
                    label = "المساهمات",
                    value = "${profile.contributionCount} مساهمة"
                )
            }
        }
    }
}

@Composable
private fun PreferencesSection(
    preferences: UserPreferences,
    onUpdatePreferences: (UserPreferences) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "التفضيلات",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "إخفاء" else "إظهار"
                    )
                }
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Language preference
                PreferenceItem(
                    icon = Icons.Default.Language,
                    title = "اللغة",
                    subtitle = if (preferences.language == "ar") "العربية" else "English",
                    onClick = {
                        // Language selection dialog would go here
                    }
                )
                
                // Theme preference
                PreferenceItem(
                    icon = Icons.Default.Palette,
                    title = "المظهر",
                    subtitle = when (preferences.theme) {
                        "light" -> "فاتح"
                        "dark" -> "داكن"
                        else -> "تلقائي"
                    },
                    onClick = {
                        // Theme selection dialog would go here
                    }
                )
                
                // Notifications
                PreferenceSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "الإشعارات",
                    subtitle = "تلقي إشعارات التطبيق",
                    checked = preferences.notificationsEnabled,
                    onCheckedChange = { enabled ->
                        onUpdatePreferences(preferences.copy(notificationsEnabled = enabled))
                    }
                )
                
                // Study reminders
                PreferenceSwitchItem(
                    icon = Icons.Default.Schedule,
                    title = "تذكيرات الدراسة",
                    subtitle = "تذكيرات يومية للمراجعة",
                    checked = preferences.studyRemindersEnabled,
                    onCheckedChange = { enabled ->
                        onUpdatePreferences(preferences.copy(studyRemindersEnabled = enabled))
                    }
                )
                
                // Sound
                PreferenceSwitchItem(
                    icon = Icons.Default.VolumeUp,
                    title = "الصوت",
                    subtitle = "تشغيل الأصوات والتأثيرات",
                    checked = preferences.soundEnabled,
                    onCheckedChange = { enabled ->
                        onUpdatePreferences(preferences.copy(soundEnabled = enabled))
                    }
                )
                
                // Auto TTS
                PreferenceSwitchItem(
                    icon = Icons.Default.RecordVoiceOver,
                    title = "النطق التلقائي",
                    subtitle = "نطق البطاقات تلقائياً",
                    checked = preferences.autoPlayTTS,
                    onCheckedChange = { enabled ->
                        onUpdatePreferences(preferences.copy(autoPlayTTS = enabled))
                    }
                )
            }
        }
    }
}

@Composable
private fun SecuritySection(
    onChangePassword: () -> Unit,
    onNavigateToSecurity: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "الأمان",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PreferenceItem(
                icon = Icons.Default.Lock,
                title = "تغيير كلمة المرور",
                subtitle = "تحديث كلمة مرور حسابك",
                onClick = onChangePassword
            )
            
            PreferenceItem(
                icon = Icons.Default.Security,
                title = "إعدادات الأمان",
                subtitle = "المصادقة الثنائية وسجل الأمان",
                onClick = onNavigateToSecurity
            )
        }
    }
}

@Composable
private fun DangerZoneSection(
    onDeleteAccount: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "منطقة الخطر",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PreferenceItem(
                icon = Icons.Default.DeleteForever,
                title = "حذف الحساب",
                subtitle = "حذف حسابك وجميع بياناتك نهائياً",
                onClick = onDeleteAccount,
                titleColor = MaterialTheme.colorScheme.error,
                subtitleColor = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun ProfileInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        trailing?.invoke()
    }
}

@Composable
private fun PreferenceItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    subtitleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = subtitleColor
            )
        }
        
        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "فتح",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PreferenceSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun PasswordChangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    
    val isValid = currentPassword.isNotBlank() && 
                  newPassword.length >= 6 && 
                  newPassword == confirmPassword
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تغيير كلمة المرور") },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("كلمة المرور الحالية") },
                    visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                            Icon(
                                if (showCurrentPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showCurrentPassword) "إخفاء" else "إظهار"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("كلمة المرور الجديدة") },
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showNewPassword = !showNewPassword }) {
                            Icon(
                                if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showNewPassword) "إخفاء" else "إظهار"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = { Text("6 أحرف على الأقل") }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("تأكيد كلمة المرور") },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showConfirmPassword) "إخفاء" else "إظهار"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = confirmPassword.isNotBlank() && newPassword != confirmPassword,
                    supportingText = {
                        if (confirmPassword.isNotBlank() && newPassword != confirmPassword) {
                            Text("كلمات المرور غير متطابقة")
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(currentPassword, newPassword) },
                enabled = isValid
            ) {
                Text("تغيير")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
private fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("حذف الحساب") },
        text = {
            Text(
                "هل أنت متأكد من رغبتك في حذف حسابك؟\n\n" +
                "سيتم حذف جميع بياناتك نهائياً ولن يمكن استرجاعها:\n" +
                "• البطاقات التعليمية\n" +
                "• الأسئلة والإجابات\n" +
                "• النقاط والإنجازات\n" +
                "• الإعدادات والتفضيلات\n\n" +
                "هذا الإجراء لا يمكن التراجع عنه."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("حذف نهائياً")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}