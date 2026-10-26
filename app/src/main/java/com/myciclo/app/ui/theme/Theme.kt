package com.myciclo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = RosaPrimario,
    onPrimary = Color.White,
    primaryContainer = RosaPrimarioContainer,
    onPrimaryContainer = OnRosaPrimarioContainer,

    secondary = CoralSecundario,
    onSecondary = Color.White,
    secondaryContainer = CoralSecundarioContainer,
    onSecondaryContainer = OnCoralSecundarioContainer,

    tertiary = VerdeFertil,
    onTertiary = Color.White,
    tertiaryContainer = VerdeFertilContainer,
    onTertiaryContainer = OnVerdeFertilContainer,

    background = FondoFresco,
    onBackground = TextoOscuro,
    surface = SuperficieTarjeta,
    onSurface = TextoOscuro,
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = TextoSecundario
)

private val DarkColorScheme = darkColorScheme(
    primary = RosaPrimarioDark,
    onPrimary = Color(0xFF560023),
    primaryContainer = RosaContainerDark,
    onPrimaryContainer = RosaPrimarioContainer,

    secondary = CoralSecundarioDark,
    onSecondary = Color(0xFF56160A),
    secondaryContainer = CoralContainerDark,
    onSecondaryContainer = CoralSecundarioContainer,

    tertiary = VerdeFertilDark,
    onTertiary = Color(0xFF1B3622),
    tertiaryContainer = VerdeContainerDark,
    onTertiaryContainer = VerdeFertilContainer,

    background = FondoOscuro,
    onBackground = Color(0xFFF0E0E5),
    surface = SuperficieOscura,
    onSurface = Color(0xFFF0E0E5),
    surfaceVariant = SuperficieVarianteOscura,
    onSurfaceVariant = Color(0xFFD6C2C9)
)

/**
 * Tema principal Material 3 de myCiclo.
 * Incluye paletas armonizadas para modo Claro (Light) y modo Oscuro (Dark).
 */
@Composable
fun MyCicloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Mantenemos false para preservar la identidad de la marca myCiclo
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}