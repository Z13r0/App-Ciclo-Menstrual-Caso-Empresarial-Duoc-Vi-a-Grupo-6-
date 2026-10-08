package com.myciclo.app.navegacion

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myciclo.app.calendario.CalendarioScreen
import com.myciclo.app.contenido.ContenidoScreen
import com.myciclo.app.registro.RegistroScreen

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    // funcion para cambiar de pestaña sin ir apilando pantallas
    fun irA(ruta: String) {
        navController.navigate(ruta) {
            popUpTo(Screen.Calendario.route)
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = rutaActual == Screen.Calendario.route,
                    onClick = { irA(Screen.Calendario.route) },
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                    label = { Text("Calendario") }
                )
                NavigationBarItem(
                    selected = rutaActual == Screen.Registro.route,
                    onClick = { irA(Screen.Registro.route) },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    label = { Text("Registrar") }
                )
                NavigationBarItem(
                    selected = rutaActual == Screen.Contenido.route,
                    onClick = { irA(Screen.Contenido.route) },
                    icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    label = { Text("Aprende") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calendario.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Calendario.route) { CalendarioScreen() }
            composable(Screen.Registro.route) { RegistroScreen() }
            composable(Screen.Contenido.route) { ContenidoScreen() }
        }
    }
}
