package com.myciclo.app.navegacion

// rutas de la app, una por cada parte del grupo
sealed class Screen(val route: String) {
    object Registro : Screen("registro")
    object Calendario : Screen("calendario")
    object Contenido : Screen("contenido")
}
