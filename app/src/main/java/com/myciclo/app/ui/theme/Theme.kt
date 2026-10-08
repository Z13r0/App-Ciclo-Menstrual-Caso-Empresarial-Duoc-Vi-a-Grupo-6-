package com.myciclo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val colores = lightColorScheme(
    primary = Morado,
    primaryContainer = MoradoClaro,
    secondary = Verde
)

@Composable
fun MyCicloTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colores,
        content = content
    )
}
