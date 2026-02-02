package com.ghzawi.proxystoreagent.ui.theme

import androidx.compose.ui.unit.dp

/**
 * ProxyStore Spacing System
 * Consistent 4dp grid system for brutal, technical layouts
 */
object ProxySpacing {
    val xs = 4.dp      // Extra small - tight spacing
    val sm = 8.dp      // Small - compact spacing
    val md = 16.dp     // Medium - standard spacing
    val lg = 24.dp     // Large - section spacing
    val xl = 32.dp     // Extra large - major section spacing
    val xxl = 48.dp    // XXL - screen-level spacing
}

/**
 * Border widths - always 2dp for brutal aesthetic
 */
object ProxyBorders {
    val standard = 2.dp    // Standard border thickness
    val thin = 1.dp        // Thin borders (only for status chips)
}

/**
 * Shadow offsets - hard shadows with 8dp offset, no blur
 */
object ProxyShadows {
    val offsetX = 8.dp
    val offsetY = 8.dp
}
