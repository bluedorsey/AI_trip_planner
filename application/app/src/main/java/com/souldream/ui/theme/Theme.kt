package com.souldream.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF536DFE),
    secondary = Color(0xFF64748B),
    tertiary = Color(0xFF42A5A5),
    background = Color(0xFF0F1117),
    surface = Color(0xFF181A22)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF536DFE),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE8ECFF),
    onPrimaryContainer = Color(0xFF172B70),

    secondary = Color(0xFF64748B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8EDF3),
    onSecondaryContainer = Color(0xFF263445),

    tertiary = Color(0xFF269E9A),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD9F4F0),
    onTertiaryContainer = Color(0xFF064E4A),

    background = Color(0xFFF8F9FC),
    onBackground = Color(0xFF191C24),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C24),

    surfaceVariant = Color(0xFFF0F2F7),
    onSurfaceVariant = Color(0xFF626B7A),

    outline = Color(0xFFD5DAE3),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun SouldreamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}