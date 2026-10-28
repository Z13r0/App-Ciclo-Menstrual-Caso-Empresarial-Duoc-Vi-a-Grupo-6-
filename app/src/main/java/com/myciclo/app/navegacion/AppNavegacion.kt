package com.myciclo.app.navegacion

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Close
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
import com.myciclo.app.ui.theme.FondoFresco
import com.myciclo.app.ui.theme.OnRosaPrimarioContainer
import com.myciclo.app.ui.theme.RosaPrimario
import com.myciclo.app.ui.theme.RosaPrimarioContainer
import com.myciclo.app.ui.theme.SuperficieVariante
import com.myciclo.app.ui.theme.TextoSecundario

/**
 * Navegación principal de myCiclo.
 * Estilo alineado con la vista previa: top bar rosa suave, nav inferior clara,
 * indicador malva en la pestaña activa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacion(userId: String, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    fun irA(ruta: String) {
        navController.navigate(ruta) {
            popUpTo(Screen.Calendario.route)
            launchSingleTop = true
        }
    }

    Scaffold(
        containerColor = FondoFresco,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "myCiclo · Bitácora EVA",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnRosaPrimarioContainer
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "myCiclo",
                        tint = RosaPrimario,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { irA(Screen.Ajustes.route) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Ajustes",
                            tint = if (rutaActual == Screen.Ajustes.route) RosaPrimario else OnRosaPrimarioContainer
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cerrar sesión",
                            tint = OnRosaPrimarioContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = RosaPrimarioContainer,
                    titleContentColor = OnRosaPrimarioContainer,
                    actionIconContentColor = OnRosaPrimarioContainer,
                    navigationIconContentColor = RosaPrimario
                )
            )
        },
        bottomBar = {
            if (rutaActual != Screen.Ajustes.route) {
                NavigationBar(
                    containerColor = SuperficieVariante,
                    contentColor = TextoSecundario,
                    tonalElevation = 0.dp
                ) {
                    val itemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RosaPrimario,
                        selectedTextColor = RosaPrimario,
                        indicatorColor = RosaPrimarioContainer,
                        unselectedIconColor = TextoSecundario,
                        unselectedTextColor = TextoSecundario
                    )
                    NavigationBarItem(
                        selected = rutaActual == Screen.Calendario.route,
                        onClick = { irA(Screen.Calendario.route) },
                        icon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                        label = {
                            Text(
                                "Calendario",
                                fontWeight = if (rutaActual == Screen.Calendario.route) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = itemColors
                    )
                    NavigationBarItem(
                        selected = rutaActual == Screen.Registro.route,
                        onClick = { irA(Screen.Registro.route) },
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        label = {
                            Text(
                                "Registrar",
                                fontWeight = if (rutaActual == Screen.Registro.route) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = itemColors
                    )
                    NavigationBarItem(
                        selected = rutaActual == Screen.Contenido.route,
                        onClick = { irA(Screen.Contenido.route) },
                        icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                        label = {
                            Text(
                                "Aprende",
                                fontWeight = if (rutaActual == Screen.Contenido.route) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = itemColors
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calendario.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Calendario.route) { CalendarioScreen(userId) }
            composable(Screen.Registro.route) { RegistroScreen(userId) }
            composable(Screen.Contenido.route) { ContenidoScreen() }
            composable(Screen.Ajustes.route) { AjustesScreen() }
        }
    }
}