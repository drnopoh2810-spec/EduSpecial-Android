package com.eduspecial.presentation.flashcards

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eduspecial.domain.model.Flashcard
import com.eduspecial.domain.model.MediaType
import com.eduspecial.domain.model.SRSResult
import com.eduspecial.presentation.common.LottieEmptyState
import com.eduspecial.presentation.common.MediaPlayerView
import com.eduspecial.presentation.theme.EduBlue
import com.eduspecial.presentation.theme.EduTeal
import com.eduspecial.utils.InAppReviewManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    navController: NavController,
    viewModel: StudyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? android.app.Activity

    // Trigger in-app review when session completes with meaningful progress
    val sessionComplete = uiState.currentCard == null && uiState.reviewedThisSession > 0
    LaunchedEffect(sessionComplete) {
        if (sessionComplete && activity != null) {
            InAppReviewManager(activity).requestReview()
        }
    }

    // Stop TTS when leaving the screen
    DisposableEffect(Unit) {
        onDispose { viewModel.stopSpeaking() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("وضع المراجعة", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.stopSpeaking()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    // Card counter — vertically centered with the icon via Row alignment
                    if (uiState.totalCards > 0) {
                        Text(
                            text = "${uiState.currentIndex + 1}/${uiState.totalCards}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 4.dp)
                        )
                    }
                    // TTS toggle button
                    IconButton(
                        onClick = { viewModel.toggleTts() },
                        modifier = Modifier.semantics {
                            contentDescription = if (uiState.ttsEnabled) "إيقاف النطق التلقائي" else "تفعيل النطق التلقائي"
                        }
                    ) {
                        Icon(
                            imageVector = if (uiState.ttsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = null,
                            tint = if (uiState.ttsEnabled)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (uiState.totalCards > 0) {
                LinearProgressIndicator(
                    progress = {
                        if (uiState.totalCards > 0)
                            uiState.currentIndex.toFloat() / uiState.totalCards
                        else 0f
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
                Spacer(Modifier.height(16.dp))
            }

            if (uiState.currentCard != null) {
                FlashcardStudyCard(
                    card = uiState.currentCard!!,
                    isFlipped = uiState.isFlipped,
                    isSpeaking = uiState.isSpeaking,
                    ttsEnabled = uiState.ttsEnabled,
                    onFlip = viewModel::flipCard,
                    onSpeakTerm = viewModel::speakCurrentTerm,
                    onSpeakDefinition = viewModel::speakCurrentDefinition
                )

                Spacer(Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = uiState.isFlipped,
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    SRSActionButtons(
                        onAgain = { viewModel.processReview(SRSResult.Again) },
                        onHard  = { viewModel.processReview(SRSResult.Hard) },
                        onGood  = { viewModel.processReview(SRSResult.Good) },
                        onEasy  = { viewModel.processReview(SRSResult.Easy) }
                    )
                }

                if (!uiState.isFlipped) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "اضغط على البطاقة لإظهار التعريف",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                StudyCompletePlaceholder(
                    mastered = uiState.masteredThisSession,
                    reviewed = uiState.reviewedThisSession,
                    onRestart = viewModel::restartSession
                )
            }
        }
    }
}

// ─── Flashcard Study Card ─────────────────────────────────────────────────────

@Composable
private fun FlashcardStudyCard(
    card: Flashcard,
    isFlipped: Boolean,
    isSpeaking: Boolean,
    ttsEnabled: Boolean,
    onFlip: () -> Unit,
    onSpeakTerm: () -> Unit,
    onSpeakDefinition: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    // Spring-based physics flip
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_flip"
    )

    val showBack = rotation > 90f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onFlip()
            }
            .semantics {
                contentDescription = if (isFlipped)
                    "البطاقة مقلوبة — التعريف: ${card.definition}"
                else
                    "المصطلح: ${card.term} — اضغط لإظهار التعريف"
            },
        contentAlignment = Alignment.Center
    ) {
        if (!showBack) {
            // ── Front Face (Term) ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(EduBlue, EduTeal))),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "المصطلح",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 3.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = card.term,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    AssistChip(
                        onClick = {},
                        label = { Text(categoryArabicName(card.category), color = Color.White) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                    Spacer(Modifier.height(16.dp))

                    // TTS Speaker Button — front face (speaks the term)
                    TtsSpeakerButton(
                        isSpeaking = isSpeaking,
                        ttsEnabled = ttsEnabled,
                        onTap = onSpeakTerm,
                        label = "استمع للنطق",
                        isOnDarkBackground = true
                    )
                }
            }
        } else {
            // ── Back Face (Definition) ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "التعريف",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        letterSpacing = 3.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = card.definition,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Media player (if media attached — TTS is skipped for audio/video cards)
                    if (card.mediaType != MediaType.NONE && card.mediaUrl != null) {
                        Spacer(Modifier.height(12.dp))
                        when (card.mediaType) {
                            MediaType.IMAGE -> {
                                coil.compose.AsyncImage(
                                    model = card.mediaUrl,
                                    contentDescription = "صورة البطاقة",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                )
                            }
                            MediaType.VIDEO -> {
                                MediaPlayerView(
                                    url = card.mediaUrl,
                                    isAudio = false,
                                    modifier = Modifier.fillMaxWidth().height(120.dp)
                                )
                            }
                            MediaType.AUDIO -> {
                                MediaPlayerView(
                                    url = card.mediaUrl,
                                    isAudio = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            else -> {}
                        }
                    }

                    // TTS Speaker Button — back face (speaks the definition)
                    // Only show if no audio/video media (those have their own player)
                    if (card.mediaType == MediaType.NONE || card.mediaType == MediaType.IMAGE) {
                        Spacer(Modifier.height(12.dp))
                        TtsSpeakerButton(
                            isSpeaking = isSpeaking,
                            ttsEnabled = ttsEnabled,
                            onTap = onSpeakDefinition,
                            label = "استمع للتعريف",
                            isOnDarkBackground = false
                        )
                    }
                }
            }
        }
    }
}

// ─── TTS Speaker Button ───────────────────────────────────────────────────────

/**
 * Animated speaker button that pulses while TTS is speaking.
 * Tapping it manually triggers speech.
 */
@Composable
private fun TtsSpeakerButton(
    isSpeaking: Boolean,
    ttsEnabled: Boolean,
    onTap: () -> Unit,
    label: String,
    isOnDarkBackground: Boolean
) {
    // Pulse animation while speaking
    val infiniteTransition = rememberInfiniteTransition(label = "tts_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val iconColor = if (isOnDarkBackground)
        Color.White.copy(alpha = 0.9f)
    else
        MaterialTheme.colorScheme.primary

    val bgColor = if (isOnDarkBackground)
        Color.White.copy(alpha = 0.15f)
    else
        MaterialTheme.colorScheme.primaryContainer

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .scale(if (isSpeaking) pulseScale else 1f)
                .clip(CircleShape)
                .background(bgColor)
                .clickable(onClick = onTap)
                .semantics { contentDescription = label },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.VolumeUp,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isSpeaking) "جاري النطق..." else label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isOnDarkBackground) Color.White.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── SRS Action Buttons ───────────────────────────────────────────────────────

@Composable
private fun SRSActionButtons(
    onAgain: () -> Unit,
    onHard: () -> Unit,
    onGood: () -> Unit,
    onEasy: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    data class SRSButton(
        val label: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val color: Color,
        val a11y: String,
        val onClick: () -> Unit
    )

    val buttons = listOf(
        SRSButton("مجدداً", Icons.Default.Replay,   Color(0xFFD32F2F), "مجدداً — لم أتذكر",        onAgain),
        SRSButton("صعب",   Icons.Default.Warning,   Color(0xFFE65100), "صعب — تذكرت بصعوبة",       onHard),
        SRSButton("جيد",   Icons.Default.ThumbUp,   EduBlue,           "جيد — تذكرت بشكل جيد",     onGood),
        SRSButton("سهل",   Icons.Default.Archive,   Color(0xFF2E7D32), "سهل — أتقنت هذا المصطلح",  onEasy)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "كيف كانت معرفتك بهذا المصطلح؟",
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            buttons.forEach { btn ->
                // Each button gets equal width via weight(1f).
                // Fixed height (72dp) + intrinsicSize ensures the circle/pill
                // never squeezes the text — icon and label share a vertical axis.
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        btn.onClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .semantics { contentDescription = btn.a11y },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = btn.color),
                    // Remove default horizontal padding so the Column can center freely
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = btn.icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = btn.label,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            softWrap = false,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Clip,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ─── Session Complete ─────────────────────────────────────────────────────────

@Composable
private fun StudyCompletePlaceholder(mastered: Int, reviewed: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieEmptyState(
            message = "أحسنت! لا توجد بطاقات للمراجعة اليوم",
            actionLabel = "بدء جلسة جديدة",
            onAction = onRestart
        )
        Spacer(Modifier.height(8.dp))
        Text("راجعت: $reviewed بطاقة", style = MaterialTheme.typography.bodyLarge)
        Text(
            "أتقنت: $mastered بطاقة",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF2E7D32)
        )
    }
}
