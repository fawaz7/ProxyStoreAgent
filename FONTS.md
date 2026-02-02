# Font Installation Instructions

## JetBrains Mono Font

This app uses JetBrains Mono for the brutal, technical aesthetic.

### Download Fonts

1. Visit: https://www.jetbrains.com/lp/mono/
2. Download the font family (TTF files)
3. Extract the following files:
   - `JetBrainsMono-Regular.ttf`
   - `JetBrainsMono-Bold.ttf`
   - `JetBrainsMono-ExtraBold.ttf` (rename to Black)

### Installation

Rename and copy the font files to `app/src/main/res/font/`:

```
app/src/main/res/font/
├── jetbrains_mono_regular.ttf
├── jetbrains_mono_bold.ttf
└── jetbrains_mono_black.ttf
```

**Note:** Font file names must be lowercase with underscores (Android requirement).

### Fallback

If fonts are not installed, the app will automatically fall back to:

- `FontFamily.Monospace` (Roboto Mono on Android)

This still maintains the monospace aesthetic but with less visual impact.

## Current Status

⚠️ **TODO**: Download and add JetBrains Mono fonts to the res/font directory

The app will currently use Roboto Mono as fallback.
