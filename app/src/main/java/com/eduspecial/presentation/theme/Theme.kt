package com.eduspecial.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ─── Brand Colors ─────────────────────────────────────────────────────────────
val EduBlue        = Color(0xFF1565C0)   // Primary
val EduBlueDark    = Color(0xFF003C8F)
val EduBlueLight   = Color(0xFF5E92F3)
val EduTeal        = Color(0xFF00897B)   // Secondary
val EduTealLight   = Color(0xFF4EBAAA)
val EduAmber       = Color(0xFFFFB300)   // Tertiary / accent
val EduAmberLight  = Color(0xFFFFE54C)
val EduError       = Color(0xFFB00020)
val EduErrorDark   = Color(0xFFCF6679)

// ─── Surface Tokens ───────────────────────────────────────────────────────────
val EduSurface         = Color(0xFFF8F9FA)
val EduSurfaceVariant  = Color(0xFFE8EDF5)
val EduOnSurface       = Color(0xFF1A1C1E)
val EduSurfaceDark     = Color(0xFF1A1C1E)
val EduSurfaceVarDark  = Color(0xFF2C2F33)
val EduOnSurfaceDark   = Color(0xFFE2E2E6)

// ─── Light Scheme ─────────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary            = EduBlue,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFD3E4FF),
    onPrimaryContainer = EduBlueDark,

    secondary            = EduTeal,
    onSecondary          = Color.White,
    secondaryContainer   = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF00251A),

    tertiary            = EduAmber,
    onTertiary          = Color(0xFF1A0F00),
    tertiaryContainer   = EduAmberLight,
    onTertiaryContainer = Color(0xFF3E2000),

    error            = EduError,
    onError          = Color.White,
    errorContainer   = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background   = Color(0xFFFBFBFF),
    onBackground = EduOnSurface,

    surface        = EduSurface,
    onSurface      = EduOnSurface,
    surfaceVariant = EduSurfaceVariant,
    onSurfaceVariant = Color(0xFF44474F),

    outline        = Color(0xFF73777F),
    outlineVariant = Color(0xFFC3C7CF),

    scrim          = Color(0xFF000000),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF1F0F4),
    inversePrimary = EduBlueLight,

    surfaceTint    = EduBlue
)

// ─── Dark Scheme ──────────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = EduBlueLight,
    onPrimary          = EduBlueDark,
    primaryContainer   = Color(0xFF0D47A1),
    onPrimaryContainer = Color(0xFFD3E4FF),

    secondary            = EduTealLight,
    onSecondary          = Color(0xFF00251A),
    secondaryContainer   = EduTeal,
    onSecondaryContainer = Color(0xFFA7F3D0),

    tertiary            = EduAmberLight,
    onTertiary          = Color(0xFF3E2000),
    tertiaryContainer   = Color(0xFF5A3A00),
    onTertiaryContainer = EduAmberLight,

    error            = EduErrorDark,
    onError          = Color(0xFF690005),
    errorContainer   = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background   = Color(0xFF111318),
    onBackground = EduOnSurfaceDark,

    surface        = EduSurfaceDark,
    onSurface      = EduOnSurfaceDark,
    surfaceVariant = EduSurfaceVarDark,
    onSurfaceVariant = Color(0xFFC3C7CF),

    outline        = Color(0xFF8C9198),
    outlineVariant = Color(0xFF44474F),

    scrim          = Color(0xFF000000),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = EduBlue,

    surfaceTint    = EduBlueLight
)

@Composable
fun EduSpecialTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color uses the wallpaper colors on Android 12+ (Material You).
    // Falls back to the brand color scheme on older devices.
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = EduTypography,
        shapes      = EduShapes,
        content     = content
    )
}
