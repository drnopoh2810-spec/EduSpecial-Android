package com.eduspecial.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eduspecial.data.model.SecurityAuditLog
import com.eduspecial.data.model.SecurityEvent
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSecurityScreen(
    onNavigateBack: () -> Unit,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        viewModel.loadSecurityInfo()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إعدادات الأمان") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Two-Factor Authentication Section
            item {
                TwoFactorAuthSection(
                    isEnabled = uiState.twoFactorEnabled,
                    onToggle = viewModel::toggleTwoFactorAuth
                )
            }
            
            // Login Sessions Section
            item {
                LoginSessionsSection(
                    lastLoginAt = uiState.lastLoginAt,
                    onSignOutAllDevices = viewModel::signOutAllDevices
                )
            }
            
            // Security Audit Log Section
            item {
                SecurityAuditSection(
                    auditLogs = uiState.auditLogs,
                    isLoading = uiState.isLoading
                )
            }
            
            // Account Recovery Section
            item {
                AccountRecoverySection(
                    onSetupRecovery = viewModel::setupAccountRecovery
                )
            }
            
            // Error display
            uiState.error?.let { error ->
                item {
                    Card(
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
    }
}

@Composable
private fun TwoFactorAuthSection(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "المصادقة الثنائية",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "طبقة حماية إضافية لحسابك",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle
                )
            }
            
            if (isEnabled) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "المصادقة الثنائية مفعلة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "تفعيل المصادقة الثنائية يحمي حسابك حتى لو تم اختراق كلمة المرور",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LoginSessionsSection(
    lastLoginAt: Long?,
    onSignOutAllDevices: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "جلسات تسجيل الدخول",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Smartphone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "الجهاز الحالي",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    
                    lastLoginAt?.let { timestamp ->
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        Text(
                            text = "آخر دخول: ${dateFormat.format(Date(timestamp))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "نشط",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onSignOutAllDevices,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تسجيل خروج من جميع الأجهزة")
            }
        }
    }
}

@Composable
private fun SecurityAuditSection(
    auditLogs: List<SecurityAuditLog>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "سجل الأمان",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "آخر الأنشطة الأمنية على حسابك",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (auditLogs.isEmpty()) {
                Text(
                    text = "لا توجد أنشطة أمنية مسجلة",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                auditLogs.take(5).forEach { log ->
                    SecurityAuditItem(log = log)
                    if (log != auditLogs.last()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                
                if (auditLogs.size > 5) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { /* Navigate to full audit log */ }
                    ) {
                        Text("عرض جميع الأنشطة")
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityAuditItem(log: SecurityAuditLog) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = when (log.event) {
            SecurityEvent.LOGIN -> Icons.Default.Login
            SecurityEvent.LOGOUT -> Icons.Default.Logout
            SecurityEvent.PASSWORD_CHANGE -> Icons.Default.Lock
            SecurityEvent.EMAIL_CHANGE -> Icons.Default.Email
            SecurityEvent.FAILED_LOGIN -> Icons.Default.Warning
            SecurityEvent.EMAIL_VERIFICATION -> Icons.Default.VerifiedUser
            SecurityEvent.TWO_FACTOR_ENABLED -> Icons.Default.Security
            SecurityEvent.TWO_FACTOR_DISABLED -> Icons.Default.SecurityUpdateWarning
            else -> Icons.Default.Info
        }
        
        val iconColor = if (log.success) {
            when (log.event) {
                SecurityEvent.FAILED_LOGIN -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.primary
            }
        } else {
            MaterialTheme.colorScheme.error
        }
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = log.event.description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            Text(
                text = dateFormat.format(Date(log.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (!log.success) {
            Icon(
                Icons.Default.Error,
                contentDescription = "فشل",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AccountRecoverySection(
    onSetupRecovery: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "استرداد الحساب",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "إعداد طرق بديلة لاسترداد حسابك في حالة فقدان الوصول",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "البريد الإلكتروني للاسترداد",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "غير مُعد",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                TextButton(onClick = onSetupRecovery) {
                    Text("إعداد")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "رقم الهاتف للاسترداد",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "غير مُعد",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                TextButton(onClick = onSetupRecovery) {
                    Text("إعداد")
                }
            }
        }
    }
}