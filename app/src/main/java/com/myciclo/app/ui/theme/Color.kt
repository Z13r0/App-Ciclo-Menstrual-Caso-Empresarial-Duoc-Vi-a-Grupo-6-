package com.myciclo.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Paleta de Colores de myCiclo (Material 3)
// Estética: Cálida, Empática y Moderna
// ==========================================

// Primarios: Rosa Malva Profundo & Magenta Elegante
val RosaPrimario = Color(0xFFB83B5E)
val RosaPrimarioContainer = Color(0xFFFFD8E4)
val OnRosaPrimarioContainer = Color(0xFF3D0017)

// Secundarios: Coral Cálido & Melocotón Suave
val CoralSecundario = Color(0xFFE06A55)
val CoralSecundarioContainer = Color(0xFFFFDAD3)
val OnCoralSecundarioContainer = Color(0xFF3B0903)

// Terciarios: Verde Menta / Salvia (Salud y ventana fértil EVA)
val VerdeFertil = Color(0xFF4A7C59)
val VerdeFertilContainer = Color(0xFFCCE8D2)
val OnVerdeFertilContainer = Color(0xFF072110)

// Fondos y Superficies (Modo Claro)
val FondoFresco = Color(0xFFFDF7F9)
val SuperficieTarjeta = Color(0xFFFFFFFF)
val SuperficieVariante = Color(0xFFF4ECEF)
val TextoOscuro = Color(0xFF22191C)
val TextoSecundario = Color(0xFF745B64)

// Modo Oscuro (Dark Theme)
val RosaPrimarioDark = Color(0xFFFFB1C8)
val RosaContainerDark = Color(0xFF7D223F)
val CoralSecundarioDark = Color(0xFFFFB4A6)
val CoralContainerDark = Color(0xFF672317)
val VerdeFertilDark = Color(0xFFB1D1B7)
val VerdeContainerDark = Color(0xFF334E39)
val FondoOscuro = Color(0xFF191114)
val SuperficieOscura = Color(0xFF22191C)
val SuperficieVarianteOscura = Color(0xFF372B30)

// Aliases de compatibilidad con código existente del grupo
val Morado = RosaPrimario
val MoradoClaro = RosaPrimarioContainer
val Verde = VerdeFertil
val Gris = TextoSecundario

// Niveles EVA para el Calendario
val EvaNuloColor = Color(0xFFBDBDBD)     // Nulo / Sin cristalización
val EvaBajoColor = Color(0xFFFFB74D)     // Bajo / Cristalización inicial
val EvaMedioColor = Color(0xFFF06292)    // Medio / Cristalización moderada
val EvaAltoColor = Color(0xFFAB47BC)     // Alto / Patrón de helecho completo