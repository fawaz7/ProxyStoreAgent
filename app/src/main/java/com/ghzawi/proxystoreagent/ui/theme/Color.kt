package com.ghzawi.proxystoreagent.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ProxyStore Brutal Color Palette
 * Industrial Cyber-Brutalism Design System
 * 
 * Philosophy: Terminal-inspired, high contrast, technical aesthetics
 * NO soft colors, NO pastels, HIGH saturation
 */

// Background Colors - Deep, Technical Blacks
val BackgroundVoid = Color(0xFF0A0C10)        // Almost black - main background
val SurfaceDarker = Color(0xFF12151C)         // Dark panels
val CardDark = Color(0xFF1A1F2E)              // Card backgrounds

// Text Colors - High Contrast
val TextPrimary = Color(0xFFE5E7EB)           // Main text (light gray)
val TextSecondary = Color(0xFF9CA3AF)         // Secondary text
val TextTertiary = Color(0xFF4B5563)          // Disabled/tertiary

// Terminal Accent Colors (HIGH SATURATION - Deliberately Bright)
val TerminalAmber = Color(0xFFFFB000)         // Primary accent - warnings, highlights, CTAs
val TerminalGreen = Color(0xFF00FF41)         // Success states, active connections
val TerminalCyan = Color(0xFF00D9FF)          // Info, secondary highlights
val TerminalRed = Color(0xFFFF4444)           // Errors, critical states, disconnect

// Border/Divider - Visible, Technical
val BorderBrutal = Color(0xFF374151)          // Borders for all elements (2dp thickness)

// Overlay Colors
val OverlayBlack = Color(0xB3000000)          // 70% opacity for overlays

// Deprecated - keeping for migration compatibility
@Deprecated("Use TerminalAmber instead", ReplaceWith("TerminalAmber"))
val Primary = TerminalAmber
@Deprecated("Use SurfaceDarker instead", ReplaceWith("SurfaceDarker"))
val SurfaceDark = SurfaceDarker
@Deprecated("Use BorderBrutal instead", ReplaceWith("BorderBrutal"))
val BorderDark = BorderBrutal
@Deprecated("Use TerminalGreen instead", ReplaceWith("TerminalGreen"))
val Success = TerminalGreen
@Deprecated("Use TerminalRed instead", ReplaceWith("TerminalRed"))
val Error = TerminalRed
@Deprecated("Use TerminalAmber instead", ReplaceWith("TerminalAmber"))
val Warning = TerminalAmber
@Deprecated("Use CardDark with no opacity", ReplaceWith("CardDark"))
val CardBackground = CardDark
