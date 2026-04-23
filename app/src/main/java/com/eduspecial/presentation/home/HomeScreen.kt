package com.eduspecial.presentation.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eduspecial.presentation.common.ChartSkeleton
import com.eduspecial.presentation.common.StatCardSkeleton
import com.eduspecial.presentation.navigation.Screen
import com.eduspecial.presentation.theme.EduBlue
import com.eduspecial.presentation.theme.EduTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    innerPadding: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val weeklyProgress by viewModel.weeklyProgress.collectAsState()
    val categoryMastery by viewModel.categoryMastery.collectAsState()
    val todayReviewed by viewModel.todayReviewed.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()

    // derivedStateOf: only recomposes when the computed value actually changes
    val hasData by remember { derivedStateOf { stats.totalFlashcards > 0 } }
    val goalProgress by remember { derivedStateOf {
        if (dailyGoal > 0) (todayReviewed.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f
    }}

    // Pull-to-refresh
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.loadAnalytics()
            kotlinx.coroutines.delay(1000)
            isRefreshing = false
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { isRefreshing = true },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            // No top padding here — the hero header handles status bar insets itself
            contentPadding = PaddingValues(
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            )
        ) {
            item {
                // Hero Header — consumes status bar inset so the gradient extends edge-to-edge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(EduBlue, EduTeal)))
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp)
                ) {
                    Column {
                        Text(
                            text = "EduSpecial",
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "موسوعة ABA والتعليم الخاص",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Search Bar — subtle white card, no heavy border
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate(Screen.Search.route) },
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "بحث",
                                    tint = EduBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    "ابحث عن مصطلح أو مفهوم...",
                                    color = Color(0xFF9E9E9E),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            item {
                Crossfade(
                    targetState = isLoading,
                    animationSpec = tween(300),
                    label = "stats_crossfade"
                ) { loading ->
                    if (loading) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCardSkeleton(Modifier.weight(1f))
                            StatCardSkeleton(Modifier.weight(1f))
                            StatCardSkeleton(Modifier.weight(1f))
                        }
                    } else {
                        StatsRow(stats)
                    }
                }
            }

            item {
                Text(
                    text = "إجراءات سريعة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        QuickActionCard(
                            icon = Icons.Default.School,
                            label = "ابدأ المراجعة",
                            color = EduBlue
                        ) { navController.navigate(Screen.Study.route) }
                    }
                    item {
                        QuickActionCard(
                            icon = Icons.Default.Add,
                            label = "أضف مصطلحاً",
                            color = EduTeal
                        ) { navController.navigate(Screen.Flashcards.route) }
                    }
                    item {
                        QuickActionCard(
                            icon = Icons.Default.QuestionAnswer,
                            label = "اطرح سؤالاً",
                            color = Color(0xFF7B1FA2)
                        ) { navController.navigate(Screen.QA.route) }
                    }
                    item {
                        QuickActionCard(
                            icon = Icons.Default.Bookmark,
                            label = "المحفوظات",
                            color = Color(0xFF1565C0)
                        ) { navController.navigate(Screen.Bookmarks.route) }
                    }
                    item {
                        QuickActionCard(
                            icon = Icons.Default.EmojiEvents,
                            label = "المتصدرون",
                            color = Color(0xFFFF8F00)
                        ) { navController.navigate(Screen.Leaderboard.route) }
                    }
                    item {
                        QuickActionCard(
                            icon = Icons.Default.Archive,
                            label = "المتقنة",
                            color = Color(0xFF388E3C)
                        ) { navController.navigate(Screen.Flashcards.route) }
                    }
                }
            }

            // Analytics Dashboard
            item {
                Spacer(Modifier.height(8.dp))
                Crossfade(
                    targetState = isLoading,
                    animationSpec = tween(300),
                    label = "analytics_crossfade"
                ) { loading ->
                    if (loading) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "إحصائياتك",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            ChartSkeleton()
                        }
                    } else {
                        AnalyticsDashboard(
                            streak = streak,
                            weeklyProgress = weeklyProgress,
                            categoryMastery = categoryMastery,
                            todayReviewed = todayReviewed,
                            dailyGoal = dailyGoal,
                            isLoading = false
                        )
                    }
                }
            }

            item {
                Text(
                    text = "تصفح حسب الفئة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            items(HomeCategoryItem.allCategories) { cat ->
                CategoryListItem(category = cat) {
                    navController.navigate(Screen.Flashcards.route)
                }
            }
        }
    }
}

@Composable
private fun StatsRow(stats: HomeStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(modifier = Modifier.weight(1f), label = "إجمالي المصطلحات", value = stats.totalFlashcards.toString())
        StatCard(modifier = Modifier.weight(1f), label = "المتقنة", value = stats.mastered.toString())
        StatCard(modifier = Modifier.weight(1f), label = "للمراجعة", value = stats.toReview.toString())
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.10f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun CategoryListItem(category: HomeCategoryItem, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = "تصفح فئة ${category.name}",
                onClick = onClick
            )
            .padding(horizontal = 8.dp)
            .semantics { contentDescription = "${category.name}: ${category.description}" },
        headlineContent = { Text(category.name, fontWeight = FontWeight.Medium) },
        supportingContent = {
            Text(category.description, style = MaterialTheme.typography.bodySmall)
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(category.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    category.icon,
                    contentDescription = null, // described by parent semantics
                    tint = category.color
                )
            }
        },
        trailingContent = {
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}
