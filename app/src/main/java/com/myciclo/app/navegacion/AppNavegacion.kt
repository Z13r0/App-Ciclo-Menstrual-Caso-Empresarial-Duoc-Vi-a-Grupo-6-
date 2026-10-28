package com.myciclo.app.navegacion

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myciclo.app.calendario.CalendarioScreen
import com.myciclo.app.contenido.ContenidoScreen
import com.myciclo.app.notificaciones.AjustesScreen
import com.myciclo.app.registro.RegistroScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    // Función para cambiar de pestaña sin apilar pantallas
    fun irA(ruta: String) {
        navController.navigate(ruta) {
            popUpTo(Screen.Calendario.route)
            launchSingleTop = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "myCiclo • Bitácora EVA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "myCiclo Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                actions = {
                    IconButton(
                        onClick = { irA(Screen.Ajustes.route) },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Ajustes de Notificación",
                            tint = if (rutaActual == Screen.Ajustes.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                NavigationBarItem(
                    selected = rutaActual == Screen.Calendario.route,
                    onClick = { irA(Screen.Calendario.route) },
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                    label = { Text("Calendario") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    selected = rutaActual == Screen.Registro.route,
                    onClick = { irA(Screen.Registro.route) },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    label = { Text("Registrar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    selected = rutaActual == Screen.Contenido.route,
                    onClick = { irA(Screen.Contenido.route) },
                    icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    label = { Text("Aprende") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
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
            composable(Screen.Ajustes.route) { AjustesScreen() }
        }
    }
}
