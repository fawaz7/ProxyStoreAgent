package com.ghzawi.proxystoreagent.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ghzawi.proxystoreagent.R

/**
 * ProxyStore Brutal Typography System
 * 
 * MONOSPACE SUPREMACY - Technical, command-line aesthetic
 * - All text uses monospace fonts (JetBrains Mono preferred, Roboto Mono fallback)
 * - Uppercase preferred for headers/labels
 * - High contrast, technical precision
 * 
 * Note: Download JetBrains Mono fonts from https://www.jetbrains.com/lp/mono/
 * Place .ttf files in res/font/ directory named:
 * - jetbrains_mono_regular.ttf
 * - jetbrains_mono_bold.ttf
 * - jetbrains_mono_black.ttf
 * 
 * Fallback: System monospace (Roboto Mono on Android)
 */

// Use system monospace as fallback (JetBrains Mono fonts not yet installed)
// TODO: Add JetBrains Mono font files - see FONTS.md
val JetBrainsMonoFontFamily = FontFamily.Monospace

// ProxyStore Brutal Typography
val Typography = Typography(
    // Display Large - UPPERCASE headers, massive stats (28sp, Black weight)
    displayLarge = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.1.sp,  // Wider tracking for uppercase
        color = TextPrimary
    ),
    
    // Headline Large - Major headings (24sp, Black)
    headlineLarge = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.08.sp,
        color = TextPrimary
    ),

    // Headline Medium - Screen headers with ">" prefix (18sp, Black, UPPERCASE)
    headlineMedium = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.08.sp,
        color = TerminalAmber  // Terminal amber for headers
    ),
    
    // Headline Small - Card headers (16sp, Black)
    headlineSmall = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.06.sp,
        color = TextPrimary
    ),

    // Body Large - Main body text (16sp, Normal)
    bodyLarge = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),

    // Body Medium - Standard text (14sp, Normal)
    bodyMedium = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    
    // Body Small - Helper text, comments with "//" (12sp, Normal)
    bodySmall = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
        color = TextSecondary
    ),

    // Label Large - Button text in [BRACKETS] (14sp, Black, UPPERCASE)
    labelLarge = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = TextPrimary
    ),
    
    // Label Medium - Form labels (12sp, Black, UPPERCASE)
    labelMedium = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
        color = TextTertiary
    ),
    
    // Label Small - Tiny labels, status text (11sp, Bold, UPPERCASE)
    labelSmall = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.08.sp,
        color = TextTertiary
    ),

    // Title Large - Large values/stats (32sp, Bold)
    titleLarge = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        color = TerminalAmber  // Use accent colors for important values
    ),
    
    // Title Medium - Medium stats (20sp, Bold)
    titleMedium = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    
    // Title Small - Small stats (16sp, Bold)
    titleSmall = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    )
)
