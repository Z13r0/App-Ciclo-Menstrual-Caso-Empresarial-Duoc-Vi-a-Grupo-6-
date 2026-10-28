package com.myciclo.app.navegacion

sealed class Screen(val route: String) {
    object Registro : Screen("registro")
    object Calendario : Screen("calendario")
    object Contenido : Screen("contenido")
    object Ajustes : Screen("ajustes")
}