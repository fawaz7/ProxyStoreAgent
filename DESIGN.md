# ProxyStore Android Agent - Design Guide

## Design Philosophy: Industrial Cyber-Brutalism

**Aesthetic Thesis:** "Proxy infrastructure deserves technical aesthetics"

This is a **deliberately anti-generic** design system that rejects soft, consumer-friendly SaaS aesthetics in favor of raw, technical brutalism. The visual language communicates infrastructure, networks, and technical precision.

**DFII Score: 13/15** (Excellent - Distinctive, Functional, Intentional, Impactful)

---

## Core Design Principles

### 1. **Zero Tolerance for Softness**

- **NO rounded corners** - Every element is perfectly square (cornerRadius = 0dp)
- **NO soft shadows** - Only hard, offset shadows (8dp offset with no blur)
- **NO gradients** - Flat, solid colors only
- **NO blur effects** - Sharp, crisp edges everywhere

### 2. **Monospace Supremacy**

- All text uses monospace fonts
- Uppercase preferred for headers/labels
- Technical, command-line aesthetic

### 3. **Terminal Color Palette**

- Limited color palette inspired by terminal emulators
- Colors have meaning (amber = warning/accent, green = success, cyan = info, red = error)
- High contrast is mandatory

### 4. **ASCII Art & Technical Decorations**

- Use ASCII characters for decorative elements
- Terminal-style UI chrome (command prompts, brackets)
- Grid overlays and technical patterns

---

## Color Palette

### Primary Colors (from CSS variables)

```kotlin
// colors.xml or Color.kt
object ProxyColors {
    // Background colors
    val BackgroundVoid = Color(0xFF0A0C10)        // Almost black
    val SurfaceDarker = Color(0xFF12151C)         // Dark panels
    val CardDark = Color(0xFF1A1F2E)              // Card backgrounds

    // Text colors
    val TextPrimary = Color(0xFFE5E7EB)           // Main text (light gray)
    val TextSecondary = Color(0xFF9CA3AF)         // Secondary text
    val TextTertiary = Color(0xFF4B5563)          // Disabled/tertiary

    // Terminal accent colors (HIGH SATURATION)
    val TerminalAmber = Color(0xFFFFB000)         // Primary accent - warnings, highlights
    val TerminalGreen = Color(0xFF00FF41)         // Success states, active connections
    val TerminalCyan = Color(0xFF00D9FF)          // Info, secondary highlights
    val TerminalRed = Color(0xFFFF4444)           // Errors, critical states

    // Border/Divider
    val BorderBrutal = Color(0xFF374151)          // Borders for all elements
}
```

### Color Usage Rules

| Color              | Usage                                                      |
| ------------------ | ---------------------------------------------------------- |
| **Terminal Amber** | Primary CTA buttons, headers, active states, key values    |
| **Terminal Green** | Success messages, "connected" status, confirmation actions |
| **Terminal Cyan**  | Secondary info, metadata, alternative highlights           |
| **Terminal Red**   | Errors, delete actions, critical warnings, disconnect      |
| **Text Primary**   | Body text, main content                                    |
| **Text Secondary** | Labels, helper text, comments (with // prefix)             |

---

## Typography

### Font Stack

**Primary Font:** JetBrains Mono (or Roboto Mono if unavailable)

```xml
<!-- res/font/fonts.xml -->
<font-family xmlns:android="http://schemas.android.com/apk/res/android">
    <font
        android:fontStyle="normal"
        android:fontWeight="400"
        android:font="@font/jetbrains_mono_regular" />
    <font
        android:fontStyle="normal"
        android:fontWeight="700"
        android:font="@font/jetbrains_mono_bold" />
    <font
        android:fontStyle="normal"
        android:fontWeight="900"
        android:font="@font/jetbrains_mono_black" />
</font-family>
```

### Text Styles (Material3 Theme)

```kotlin
val ProxyTypography = Typography(
    // Headers - UPPERCASE, bold, tracking wider
    displayLarge = TextStyle(
        fontFamily = jetBrainsMono,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        letterSpacing = 0.1.em,
        textTransform = TextTransform.Uppercase
    ),

    headlineMedium = TextStyle(
        fontFamily = jetBrainsMono,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        letterSpacing = 0.08.em,
        textTransform = TextTransform.Uppercase
    ),

    // Body text - normal weight
    bodyLarge = TextStyle(
        fontFamily = jetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = jetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),

    // Labels - bold, uppercase
    labelLarge = TextStyle(
        fontFamily = jetBrainsMono,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp,
        letterSpacing = 0.1.em,
        textTransform = TextTransform.Uppercase
    ),

    // Captions/hints - smaller, secondary color
    bodySmall = TextStyle(
        fontFamily = jetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.sp,
        color = ProxyColors.TextSecondary
    )
)
```

### Font Size Guidelines

- **Large values/stats:** 32-40sp (bold/black weight)
- **Headers:** 18-24sp (black weight, uppercase)
- **Body text:** 14-16sp (normal weight)
- **Labels:** 12sp (black weight, uppercase)
- **Captions/comments:** 11-12sp (normal weight, with `//` prefix)

---

## Component Styling

### 1. Buttons

```kotlin
// Primary Action Button
Button(
    modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = ProxyColors.SurfaceDarker,
        contentColor = ProxyColors.TextPrimary
    ),
    shape = RectangleShape, // CRITICAL: No rounding
    border = BorderStroke(2.dp, ProxyColors.TerminalAmber),
    elevation = ButtonDefaults.buttonElevation(
        defaultElevation = 0.dp, // No soft shadows
        pressedElevation = 0.dp
    )
) {
    Text(
        text = "[CONNECT]", // Brackets for button labels
        style = MaterialTheme.typography.labelLarge,
        letterSpacing = 0.1.em
    )
}

// On hover/press: Invert colors (amber background, black text)
```

**Button States:**

- Default: Dark background + colored border + light text
- Hover/Press: Colored background + dark text
- Disabled: 50% opacity

**Button Text Format:** Always wrap in brackets: `[ACTION]`, `[SAVE]`, `[CANCEL]`

### 2. Cards

```kotlin
Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
        containerColor = ProxyColors.CardDark
    ),
    shape = RectangleShape, // No rounding!
    border = BorderStroke(2.dp, ProxyColors.BorderBrutal),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
) {
    // Content with padding
    Column(modifier = Modifier.padding(16.dp)) {
        // Card content
    }
}

// Add brutal shadow via modifier:
modifier = Modifier.drawBehind {
    drawRect(
        color = Color.Black.copy(alpha = 0.7f),
        topLeft = Offset(8f, 8f),
        size = size
    )
}
```

**Card Variants:**

- **Standard:** 2dp border, no shadow
- **Elevated:** 2dp border + 8dp offset shadow (no blur)
- **Accent:** Colored border (amber/cyan/green) instead of gray

### 3. Text Fields / Inputs

```kotlin
OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    modifier = Modifier.fillMaxWidth(),
    textStyle = TextStyle(
        fontFamily = jetBrainsMono,
        fontSize = 14.sp,
        color = ProxyColors.TextPrimary
    ),
    shape = RectangleShape, // No rounding
    colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ProxyColors.TerminalAmber,
        unfocusedBorderColor = ProxyColors.BorderBrutal,
        focusedContainerColor = ProxyColors.SurfaceDarker,
        unfocusedContainerColor = ProxyColors.SurfaceDarker
    )
)
```

### 4. Status Indicators

```kotlin
// Connection status chip
Surface(
    color = ProxyColors.TerminalGreen.copy(alpha = 0.2f),
    shape = RectangleShape,
    border = BorderStroke(1.dp, ProxyColors.TerminalGreen)
) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(ProxyColors.TerminalGreen)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "ONLINE",
            style = MaterialTheme.typography.bodySmall,
            color = ProxyColors.TerminalGreen,
            fontWeight = FontWeight.Bold
        )
    }
}
```

**Status Colors:**

- `ONLINE` / `CONNECTED` → Terminal Green
- `OFFLINE` / `DISCONNECTED` → Terminal Red
- `CONNECTING...` / `PENDING` → Terminal Amber
- `IDLE` → Terminal Cyan

### 5. Dividers

```kotlin
Divider(
    modifier = Modifier.fillMaxWidth(),
    thickness = 2.dp,
    color = ProxyColors.BorderBrutal
)
```

Always use 2dp thickness, never 1dp.

---

## Layout Patterns

### Screen Structure

```
┌─────────────────────────────────────┐
│ > APP_NAME                     [···]│ ← Header (amber accent)
├─────────────────────────────────────┤
│                                     │
│  [Main Content Area]                │
│                                     │
│  ┌───────────────────────────────┐  │
│  │ Card with 2dp border          │  │
│  │                               │  │
│  └───────────────────────────────┘  │
│                                     │
│  // Helper text in comments        │
│                                     │
└─────────────────────────────────────┘
```

### Spacing System

Use consistent 4dp grid system:

```kotlin
object ProxySpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
}
```

### Header Pattern

Every screen should start with terminal-style header:

```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .background(ProxyColors.SurfaceDarker)
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .border(
            width = 2.dp,
            color = ProxyColors.BorderBrutal,
            shape = RectangleShape
        ),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Text(
        text = "> SCREEN_NAME",
        style = MaterialTheme.typography.headlineMedium,
        color = ProxyColors.TerminalAmber
    )
    // Optional: Status or action icons
}
```

---

## Specific UI Components

### Connection Status Display

```
┌─────────────────────────────┐
│ > CONNECTION_STATUS         │ ← Amber header
├─────────────────────────────┤
│                             │
│  ███████████ CONNECTED      │ ← Large, bold, green
│                             │
│  SERVER: proxy.example.com  │
│  UPTIME: 2h 34m 12s         │
│  TRAFFIC: 234.5 MB          │
│                             │
│  // Last ping: 45ms         │ ← Green comment
│                             │
└─────────────────────────────┘
```

### Token Input Screen

```
┌─────────────────────────────┐
│ > DEVICE_PAIRING            │
├─────────────────────────────┤
│                             │
│ PAIRING_TOKEN               │ ← Uppercase label
│ ┌─────────────────────────┐ │
│ │ [Enter 6-digit token]   │ │ ← Squared input
│ └─────────────────────────┘ │
│                             │
│ [PASTE_FROM_CLIPBOARD]      │ ← Amber bordered button
│                             │
│ // Get token from dashboard │ ← Gray comment
│                             │
└─────────────────────────────┘
```

### Stats Display

```
┌─────────────────────────────┐
│ DATA_TRANSFERRED            │ ← Cyan label
│                             │
│ 1.24 GB                     │ ← HUGE (40sp), amber
│                             │
│ EARNINGS: 124 credits       │ ← Regular size, green
│ RATE: 100 cr/GB             │
└─────────────────────────────┘
```

---

## Animations & Interactions

### 1. **Button Press**

- Duration: 100ms
- Effect: Instant color inversion (no scale, no bounce)
- Visual: Border color becomes background, text inverts

### 2. **Status Pulse**

- Connected indicator pulses subtly
- Scale: 1.0 → 1.15 → 1.0
- Duration: 2000ms infinite

### 3. **Data Counter**

- Numbers increment with brief glow effect
- Glow color matches the accent (amber for earnings, cyan for data)

### 4. **Screen Transitions**

- NO slide animations
- Simple fade or instant cut between screens
- Duration: 150ms if fade used

### 5. **Loading States**

- Use ASCII spinner: `[⠋⠙⠹⠸⠼⠴⠦⠧⠇⠏]`
- Or terminal-style: `LOADING...` with dots animating
- NO circular progress indicators

---

## Android-Specific Implementation Notes

### Material3 Theme Setup

```kotlin
@Composable
fun ProxyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = ProxyColors.TerminalAmber,
            onPrimary = ProxyColors.BackgroundVoid,
            secondary = ProxyColors.TerminalCyan,
            background = ProxyColors.BackgroundVoid,
            surface = ProxyColors.CardDark,
            onSurface = ProxyColors.TextPrimary,
            error = ProxyColors.TerminalRed,
            onError = ProxyColors.BackgroundVoid
        ),
        typography = ProxyTypography,
        shapes = Shapes(
            extraSmall = RectangleShape,
            small = RectangleShape,
            medium = RectangleShape,
            large = RectangleShape,
            extraLarge = RectangleShape
        ),
        content = content
    )
}
```

### System UI Configuration

```kotlin
// In MainActivity
WindowCompat.setDecorFitsSystemWindows(window, false)

// Use edge-to-edge with custom insets
window.statusBarColor = Color.Transparent
window.navigationBarColor = ProxyColors.BackgroundVoid.toArgb()

// Light/dark icons
WindowInsetsControllerCompat(window, view).apply {
    isAppearanceLightStatusBars = false // Dark icons on light bg
    isAppearanceLightNavigationBars = false
}
```

### Notification Style

```kotlin
NotificationCompat.Builder(context, CHANNEL_ID)
    .setSmallIcon(R.drawable.ic_proxy_logo) // Square icon
    .setContentTitle("> PROXYSTORE")
    .setContentText("Connected: 2h 34m | 1.2GB transferred")
    .setColor(ProxyColors.TerminalAmber.toArgb())
    .setStyle(NotificationCompat.BigTextStyle()
        .setBigContentTitle("> CONNECTION_STATUS")
        .bigText("ONLINE\nSERVER: proxy.example.com\nTRAFFIC: 1.24 GB\nEARNINGS: 124 credits"))
    .build()
```

---

## ASCII Decorations

Use these characters for visual flair:

```
Corners: ┌ ┐ └ ┘
Borders: ─ │ ├ ┤ ┬ ┴ ┼
Arrows: ▶ ◀ ▲ ▼ → ←
Blocks: █ ▓ ▒ ░
Bullets: • ◆ ■
Symbols: // > $ # [ ] { }
```

**Example Usage:**

```
┌─ DEVICE_INFO ─┐
│ • Platform: Android 14
│ • IP: 192.168.1.100
│ • Uptime: 2h 34m
└───────────────┘

> STATUS: [ONLINE]
▶ Data flowing...
```

---

## Don'ts (Anti-Patterns)

❌ **DO NOT:**

1. Use Material You dynamic colors
2. Add rounded corners anywhere
3. Use soft shadows or elevation
4. Add blur effects or glassmorphism
5. Use serif fonts or standard sans-serif
6. Create smooth, bouncy animations
7. Use gradient backgrounds
8. Add unnecessary icons (prefer text labels)
9. Use pastel or muted colors
10. Create cards without visible borders

---

## Reference Screenshots

Refer to the web app for visual reference:

- Login screen: Terminal-style with ASCII logo
- Dashboard: Stat cards with colored side stripes
- Connection modal: Amber borders, squared design, `[BUTTON]` format
- Settings: Uppercase labels, brutal input fields

---

## Design Validation Checklist

Before calling the design complete, verify:

- [ ] Zero border-radius anywhere in the app
- [ ] All text uses monospace font
- [ ] Terminal color palette used consistently
- [ ] Buttons have `[BRACKET]` format
- [ ] Cards have 2dp borders
- [ ] Status indicators show proper colors
- [ ] Comments prefixed with `//`
- [ ] Headers use `>` prefix
- [ ] All shadows are hard offset (8dp, no blur)
- [ ] No gradients used
- [ ] High contrast maintained (WCAG AA minimum)

---

**Key Philosophy:** This isn't "dark mode Material Design" — it's a completely custom aesthetic that deliberately breaks conventional mobile UI patterns to create a distinctive, technical identity.
