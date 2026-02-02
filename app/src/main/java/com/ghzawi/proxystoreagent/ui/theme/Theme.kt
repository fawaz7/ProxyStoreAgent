package com.ghzawi.proxystoreagent.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

/**
 * ProxyStore Brutal Color Scheme
 * 
 * Industrial Cyber-Brutalism Design System
 * - Terminal-inspired colors
 * - High contrast, high saturation accents
 * - Zero soft colors, zero gradients
 */
private val ProxyStoreBrutalColorScheme = darkColorScheme(
    // Primary - Terminal Amber for CTAs and highlights
    primary = TerminalAmber,
    onPrimary = BackgroundVoid,
    primaryContainer = TerminalAmber.copy(alpha = 0.2f),
    onPrimaryContainer = TerminalAmber,

    // Secondary - Terminal Cyan for secondary actions
    secondary = TerminalCyan,
    onSecondary = BackgroundVoid,
    secondaryContainer = TerminalCyan.copy(alpha = 0.2f),
    onSecondaryContainer = TerminalCyan,

    // Tertiary - Terminal Green for success states
    tertiary = TerminalGreen,
    onTertiary = BackgroundVoid,
    tertiaryContainer = TerminalGreen.copy(alpha = 0.2f),
    onTertiaryContainer = TerminalGreen,

    // Background - Deep void black
    background = BackgroundVoid,
    onBackground = TextPrimary,

    // Surface - Card dark for elevated surfaces
    surface = CardDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDarker,
    onSurfaceVariant = TextSecondary,

    // Border/Outline - Brutal visible borders
    outline = BorderBrutal,
    outlineVariant = BorderBrutal,

    // Error - Terminal Red for errors and critical actions
    error = TerminalRed,
    onError = BackgroundVoid,
    errorContainer = TerminalRed.copy(alpha = 0.2f),
    onErrorContainer = TerminalRed,

    // Inverse colors
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundVoid,
    inversePrimary = TerminalAmber,
    
    // Surface tint and scrim
    surfaceTint = TerminalAmber,
    scrim = OverlayBlack
)

/**
 * ProxyStore Brutal Shapes
 * 
 * ZERO ROUNDED CORNERS - Everything is perfectly square
 * This is critical to the brutal aesthetic
 */
private val ProxyStoreBrutalShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),  // 0dp radius
    small = RoundedCornerShape(0.dp),        // 0dp radius
    medium = RoundedCornerShape(0.dp),       // 0dp radius
    large = RoundedCornerShape(0.dp),        // 0dp radius
    extraLarge = RoundedCornerShape(0.dp)    // 0dp radius
)

/**
 * ProxyStore Brutal Theme
 * 
 * Main theme composable that applies:
 * - Brutal color scheme (terminal colors)
 * - Brutal shapes (zero rounded corners)
 * - Brutal typography (monospace, uppercase)
 * - System UI configuration (edge-to-edge, status bar)
 * 
 * This theme enforces the Industrial Cyber-Brutalism aesthetic
 */
@Composable
fun ProxyStoreAgentTheme(
    darkTheme: Boolean = true,  // Always dark - this is a technical app
    dynamicColor: Boolean = false,  // NO Material You - we have a distinctive identity
    content: @Composable () -> Unit
) {
    // Force dark color scheme - brutal design is inherently dark
    val colorScheme = ProxyStoreBrutalColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Set status bar to transparent for edge-to-edge
            window.statusBarColor = Color.Transparent.toArgb()
            
            // Set navigation bar to background void
            window.navigationBarColor = BackgroundVoid.toArgb()
            
            // Configure edge-to-edge
            WindowCompat.setDecorFitsSystemWindows(window, false)
            
            // Set light/dark icons (dark icons = false for light bg, true for dark bg)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false  // Light icons on dark bg
                isAppearanceLightNavigationBars = false  // Light icons on dark bg
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ProxyStoreBrutalShapes,
        content = content
    )
}
