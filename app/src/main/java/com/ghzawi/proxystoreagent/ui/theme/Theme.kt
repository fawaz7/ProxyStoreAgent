package com.ghzawi.proxystoreagent.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * ProxyStore Dark Color Scheme (Primary Theme)
 * Based on ProxyStore brand guidelines with #137FEC primary color
 */
private val ProxyStoreDarkColorScheme = darkColorScheme(
    // Primary brand color
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = TextPrimary,

    // Secondary colors
    secondary = Primary,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceDark,
    onSecondaryContainer = TextSecondary,

    // Tertiary colors for accents
    tertiary = Primary,
    onTertiary = TextPrimary,
    tertiaryContainer = CardDark,
    onTertiaryContainer = TextSecondary,

    // Background colors
    background = BackgroundDark,
    onBackground = TextPrimary,

    // Surface colors
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,

    // Border/Outline
    outline = BorderDark,
    outlineVariant = BorderDark,

    // Error colors
    error = Error,
    onError = TextPrimary,
    errorContainer = Error,
    onErrorContainer = TextPrimary,

    // Other colors
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundDark,
    inversePrimary = Primary,
    surfaceTint = Primary,
    scrim = OverlayBlack
)

/**
 * ProxyStore Light Color Scheme (Optional - for users who prefer light mode)
 * Note: Dark mode is the primary brand theme
 */
private val ProxyStoreLightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E7FF),
    onPrimaryContainer = Color(0xFF001D35),

    secondary = Primary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1E2EC),
    onSecondaryContainer = Color(0xFF1A1C22),

    tertiary = Primary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD9E3),
    onTertiaryContainer = Color(0xFF3E001F),

    background = Color(0xFFFAFCFF),
    onBackground = Color(0xFF1A1C1E),

    surface = Color(0xFFFAFCFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF44464F),

    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC4C6D0),

    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

/**
 * ProxyStore Agent Theme
 *
 * @param darkTheme Whether to use dark theme (default: true - dark mode is primary brand theme)
 * @param dynamicColor Whether to use Material You dynamic colors (default: false - we use branded colors)
 * @param content The composable content to theme
 */
@Composable
fun ProxyStoreAgentTheme(
    darkTheme: Boolean = true, // Dark mode as primary theme
    dynamicColor: Boolean = false, // Disable dynamic color to use ProxyStore brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Only use dynamic color if explicitly enabled and on Android 12+
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else ProxyStoreLightColorScheme
        }

        darkTheme -> ProxyStoreDarkColorScheme
        else -> ProxyStoreLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
