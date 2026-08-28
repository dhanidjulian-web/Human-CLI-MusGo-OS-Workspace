package com.agon.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SovereignDark = darkColorScheme(
    primary = SovereignGold,
    onPrimary = MidnightInk,
    primaryContainer = Color(0xFF2A2110),
    onPrimaryContainer = SovereignGoldBright,

    secondary = SovereignEmerald,
    onSecondary = MidnightInk,
    secondaryContainer = Color(0xFF0A2B22),
    onSecondaryContainer = Color(0xFF6EE7B7),

    tertiary = SovereignSapphire,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF0F1E3A),
    onTertiaryContainer = Color(0xFF93C5FD),

    background = MidnightInk,
    onBackground = TextOnMidnight,

    surface = MidnightSurface,
    onSurface = TextOnMidnight,
    surfaceVariant = MidnightCard,
    onSurfaceVariant = TextOnMidnightSecondary,
    surfaceContainer = MidnightRaised,
    surfaceContainerHigh = Color(0xFF1E2A48),

    outline = MidnightBorder,
    outlineVariant = Color(0xFF2A3658),

    error = SovereignCrimson,
    onError = Color.White,
    errorContainer = Color(0xFF3B0F12),
    onErrorContainer = Color(0xFFFCA5A5),

    inverseSurface = ParchmentSurface,
    inverseOnSurface = ParchmentInk,
    inversePrimary = SovereignGoldDark,

    scrim = Color(0xCC000000),
)

private val SovereignLight = lightColorScheme(
    primary = SovereignGoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF5E6B8),
    onPrimaryContainer = Color(0xFF3A2D0A),

    secondary = SovereignEmeraldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFEFE2),
    onSecondaryContainer = Color(0xFF0A2B22),

    tertiary = Color(0xFF1D4ED8),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDBE6FE),
    onTertiaryContainer = Color(0xFF0F1E3A),

    background = ParchmentBg,
    onBackground = ParchmentInk,

    surface = ParchmentSurface,
    onSurface = ParchmentInk,
    surfaceVariant = ParchmentCard,
    onSurfaceVariant = ParchmentTextSecondary,
    surfaceContainer = Color(0xFFF1ECDD),
    surfaceContainerHigh = ParchmentRaised,

    outline = ParchmentBorder,
    outlineVariant = Color(0xFFE6DEC6),

    error = Color(0xFFB91C1C),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF3B0F12),

    inverseSurface = MidnightSurface,
    inverseOnSurface = TextOnMidnight,
    inversePrimary = SovereignGold,
)

@Composable
fun AgonAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) SovereignDark else SovereignLight
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
