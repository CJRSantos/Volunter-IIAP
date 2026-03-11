package com.gdcj.voluntariadoiiap.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryWhite,
    primaryContainer = PrimaryGreen,
    onPrimaryContainer = OnPrimaryWhite,
    secondary = SecondaryGreen,
    onSecondary = OnSecondaryWhite,
    secondaryContainer = SecondaryContainerGreen,
    onSecondaryContainer = OnSecondaryContainerGreen,
    tertiary = TertiaryGreen,
    onTertiary = OnTertiaryWhite,
    tertiaryContainer = TertiaryContainerGreen,
    onTertiaryContainer = OnTertiaryContainerGreen,
    background = Color(0xFF1A1C19),
    surface = Color(0xFF1A1C19),
    onBackground = Color(0xFFE2E3DD),
    onSurface = Color(0xFFE2E3DD),
    surfaceVariant = Color(0xFF424940),
    onSurfaceVariant = Color(0xFFC2C8BC),
    outline = Color(0xFF8C9388)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryWhite,
    primaryContainer = PrimaryContainerGreen,
    onPrimaryContainer = OnPrimaryContainerGreen,
    secondary = SecondaryGreen,
    onSecondary = OnSecondaryWhite,
    secondaryContainer = SecondaryContainerGreen,
    onSecondaryContainer = OnSecondaryContainerGreen,
    tertiary = TertiaryGreen,
    onTertiary = OnTertiaryWhite,
    tertiaryContainer = TertiaryContainerGreen,
    onTertiaryContainer = OnTertiaryContainerGreen,
    background = Color(0xFFFCFDF6),
    surface = Color(0xFFFCFDF6),
    onBackground = Color(0xFF1A1C19),
    onSurface = Color(0xFF1A1C19),
    surfaceVariant = Color(0xFFDEE5D9),
    onSurfaceVariant = Color(0xFF424940),
    outline = Color(0xFF72796F)
)

@Composable
fun VOLUNTARIADOIIAPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Hacemos la barra de estado transparente para que se integre con el diseño edge-to-edge
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
