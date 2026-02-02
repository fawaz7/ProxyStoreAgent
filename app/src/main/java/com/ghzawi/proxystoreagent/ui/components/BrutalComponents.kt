package com.ghzawi.proxystoreagent.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ghzawi.proxystoreagent.ui.theme.*

/**
 * Brutal Button - Primary action button with square corners and colored borders
 * 
 * Design principles:
 * - NO rounded corners (RectangleShape)
 * - Colored border (2dp)
 * - NO soft shadows/elevation
 * - Button text wrapped in [BRACKETS]
 * - Instant color inversion on press
 * 
 * @param text Button text (will be wrapped in [brackets])
 * @param onClick Click handler
 * @param modifier Modifier
 * @param enabled Whether button is enabled
 * @param borderColor Border color (default: TerminalAmber)
 * @param icon Optional leading icon
 */
@Composable
fun BrutalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = TerminalAmber,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceDarker,
            contentColor = TextPrimary,
            disabledContainerColor = SurfaceDarker.copy(alpha = 0.5f),
            disabledContentColor = TextTertiary
        ),
        shape = RectangleShape,  // CRITICAL: No rounding!
        border = BorderStroke(ProxyBorders.standard, if (enabled) borderColor else BorderBrutal),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ProxySpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "[$text]",  // Wrap in brackets
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black
            )
        }
    }
}

/**
 * Brutal Outlined Button - Secondary action with only border
 */
@Composable
fun BrutalOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = BorderBrutal,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextPrimary,
            disabledContentColor = TextTertiary
        ),
        shape = RectangleShape,
        border = BorderStroke(ProxyBorders.standard, if (enabled) borderColor else BorderBrutal.copy(alpha = 0.5f)),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ProxySpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "[$text]",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black
            )
        }
    }
}

/**
 * Brutal Card - Card component with square corners and visible borders
 * 
 * Design principles:
 * - NO rounded corners
 * - 2dp border
 * - Optional hard shadow (8dp offset, no blur)
 * - Optional colored accent border
 * 
 * @param modifier Modifier
 * @param accentColor Optional accent color for border (overrides default)
 * @param withShadow Whether to add hard offset shadow
 * @param content Card content
 */
@Composable
fun BrutalCard(
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
    withShadow: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (withShadow) {
        modifier.drawBehind {
            // Hard shadow - 8dp offset, no blur
            drawRect(
                color = Color.Black.copy(alpha = 0.7f),
                topLeft = Offset(ProxyShadows.offsetX.toPx(), ProxyShadows.offsetY.toPx()),
                size = size
            )
        }
    } else {
        modifier
    }
    
    Card(
        modifier = cardModifier,
        colors = CardDefaults.cardColors(
            containerColor = CardDark
        ),
        shape = RectangleShape,  // NO rounding!
        border = BorderStroke(ProxyBorders.standard, accentColor ?: BorderBrutal),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)  // No soft shadows
    ) {
        Column(
            modifier = Modifier.padding(ProxySpacing.md),
            content = content
        )
    }
}

/**
 * Brutal Text Field - Input field with square corners and colored borders
 * 
 * Design principles:
 * - NO rounded corners
 * - Monospace font
 * - Colored border on focus (amber)
 * - Dark background
 * 
 * @param value Current text value
 * @param onValueChange Value change callback
 * @param modifier Modifier
 * @param label Optional label text (will be UPPERCASE)
 * @param placeholder Placeholder text
 * @param enabled Whether field is enabled
 * @param singleLine Whether single line input
 * @param textStyle Optional text style override
 */
@Composable
fun BrutalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Column(modifier = modifier) {
        label?.let {
            Text(
                text = it.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = TextTertiary,
                modifier = Modifier.padding(bottom = ProxySpacing.sm)
            )
        }
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            textStyle = textStyle.copy(
                color = TextPrimary,
                fontFamily = textStyle.fontFamily ?: MaterialTheme.typography.bodyMedium.fontFamily
            ),
            shape = RectangleShape,  // NO rounding!
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerminalAmber,
                unfocusedBorderColor = BorderBrutal,
                disabledBorderColor = BorderBrutal.copy(alpha = 0.5f),
                focusedContainerColor = SurfaceDarker,
                unfocusedContainerColor = SurfaceDarker,
                disabledContainerColor = SurfaceDarker.copy(alpha = 0.5f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = TextTertiary,
                cursorColor = TerminalAmber
            ),
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        color = TextTertiary,
                        style = textStyle
                    )
                }
            }
        )
    }
}

/**
 * Brutal Status Indicator - Status chip with colored border and dot
 * 
 * Design principles:
 * - Square shape
 * - Colored border and text
 * - Solid color dot indicator
 * - UPPERCASE text
 * 
 * @param status Status text (e.g., "ONLINE", "OFFLINE", "CONNECTING")
 * @param color Status color
 * @param modifier Modifier
 */
@Composable
fun BrutalStatusIndicator(
    status: String,
    color: Color,
    modifier: Modifier = Modifier,
    showPulse: Boolean = false
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.2f),
        shape = RectangleShape,
        border = BorderStroke(ProxyBorders.thin, color)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = ProxySpacing.sm, vertical = ProxySpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Status dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, RectangleShape)
            )
            
            // Status text
            Text(
                text = status.uppercase(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
        }
    }
}

/**
 * Brutal Divider - 2dp thick divider line
 */
@Composable
fun BrutalDivider(
    modifier: Modifier = Modifier,
    color: Color = BorderBrutal
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = ProxyBorders.standard,
        color = color
    )
}

/**
 * Brutal Screen Header - Terminal-style header with ">" prefix
 * 
 * @param title Screen title (will be UPPERCASE with ">" prefix)
 * @param modifier Modifier
 * @param trailingContent Optional trailing content (e.g., status indicator)
 */
@Composable
fun BrutalScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDarker)
            .border(
                width = ProxyBorders.standard,
                color = BorderBrutal,
                shape = RectangleShape
            )
            .padding(horizontal = ProxySpacing.md, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "> ${title.uppercase()}",
            style = MaterialTheme.typography.headlineMedium,
            color = TerminalAmber
        )
        
        trailingContent?.invoke()
    }
}

/**
 * Brutal Comment Text - Helper text with "//" prefix
 * 
 * @param text Comment text
 * @param modifier Modifier
 * @param color Text color (default: TextSecondary)
 */
@Composable
fun BrutalComment(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextSecondary
) {
    Text(
        text = "// $text",
        style = MaterialTheme.typography.bodySmall,
        color = color,
        modifier = modifier
    )
}
