package com.myciclo.app.contenido

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myciclo.app.notificaciones.NotificacionHelper
import com.myciclo.app.ui.componentes.AvisoEva

/**
 * Pantalla de Contenido Educativo.
 * Presenta guías interactivas con categorías, barra de búsqueda en tiempo real,
 * filtro por categorías con FilterChips, sección de favoritas persistente y modal de notificaciones.
 */
@Composable
fun ContenidoScreen(vm: ContenidoViewModel = viewModel()) {
    val context = LocalContext.current
    val estado by vm.estado.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val categoriaFiltro by vm.categoriaFiltro.collectAsState()
    val favoritasIds by vm.favoritasIds.collectAsState()
    val soloFavoritas by vm.soloFavoritas.collectAsState()

    var mostrarAjustesNotif by remember { mutableStateOf(false) }

    val categoriasEstaticas = listOf("Manual", "Educación", "Preguntas frecuentes")

    // la fila de filtros no cabe a lo ancho del telefono, asi que la dejamos
    // deslizable en horizontal. sin esto el ultimo chip queda aplastado, su texto
    // se parte en muchas lineas y estira la fila tapando las guias
    val scrollFiltros = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Aprende sobre EVA",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { mostrarAjustesNotif = true }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Ajustes de Notificación",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Bar de búsqueda
        OutlinedTextField(
            value = busqueda,
            onValueChange = { vm.actualizarBusqueda(it) },
            placeholder = { Text("Buscar en las guías educativas...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Filtros por Categoría y Sección Favoritas usando FilterChips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollFiltros),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = categoriaFiltro == null && !soloFavoritas,
                onClick = {
                    vm.actualizarCategoria(null)
                    if (soloFavoritas) vm.toggleSoloFavoritas()
                },
                label = { Text("Todas") }
            )
            FilterChip(
                selected = soloFavoritas,
                onClick = { vm.toggleSoloFavoritas() },
                label = { Text("⭐ Favoritas (${favoritasIds.size})") }
            )
            categoriasEstaticas.forEach { cat ->
                FilterChip(
                    selected = categoriaFiltro?.equals(cat, ignoreCase = true) == true && !soloFavoritas,
                    onClick = {
                        val seleccionada = if (categoriaFiltro?.equals(cat, ignoreCase = true) == true) null else cat
                        vm.actualizarCategoria(seleccionada)
                        if (soloFavoritas) vm.toggleSoloFavoritas()
                    },
                    label = { Text(cat) }
                )
            }
        }

        when (val e = estado) {
            is ContenidoEstado.Cargando -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Cargando guías educativas...", style = MaterialTheme.typography.bodyMedium)
                }
            }
            is ContenidoEstado.Listo -> {
                if (e.sinConexion) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sin conexión. Mostrando guías locales de respaldo.",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            TextButton(onClick = { vm.cargar() }) {
                                Text("Reintentar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (e.guias.isEmpty()) {
                    Text(
                        text = if (soloFavoritas)
                            "Aún no tienes guías marcadas como favoritas. Presiona el ícono ⭐ en cualquier guía para guardarla."
                        else
                            "No se encontraron guías que coincidan con la búsqueda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    // weight deja que la lista use todo el alto que sobra, asi se
                    // puede bajar hasta la ultima guia sin que la tape la barra
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(e.guias, key = { it.id }) { guia ->
                            val esFav = favoritasIds.contains(guia.id)
                            TarjetaGuia(
                                guia = guia,
                                esFavorita = esFav,
                                onToggleFavorita = { vm.toggleFavorita(guia.id) }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            AvisoEva()
                        }
                    }
                }
            }
        }
    }

    // Diálogo de Ajustes de Notificación Diario
    if (mostrarAjustesNotif) {
        AlertDialog(
            onDismissRequest = { mostrarAjustesNotif = false },
            title = { Text("Ajustes de Recordatorio Diario") },
            text = {
                Text("El recordatorio diario de observación EVA está configurado por defecto a las 09:00 AM.")
            },
            confirmButton = {
                TextButton(onClick = {
                    NotificacionHelper.sincronizarNotificacion(context)
                    mostrarAjustesNotif = false
                }) { Text("Reactivar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    NotificacionHelper.cancelarRecordatorio(context)
                    mostrarAjustesNotif = false
                }) { Text("Desactivar Alarma") }
            }
        )
    }
}

/**
 * Tarjeta interactiva para cada guía educativa.
 * Muestra categoría, título, botón de favoritos y expande el contenido al presionar.
 */
@Composable
fun TarjetaGuia(
    guia: Guia,
    esFavorita: Boolean = false,
    onToggleFavorita: () -> Unit = {}
) {
    var abierta by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { abierta = !abierta },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = guia.categoria,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFavorita) {
                        Icon(
                            imageVector = if (esFavorita) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Favorita",
                            tint = if (esFavorita) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = if (abierta) "▲" else "▼",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = guia.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(visible = abierta) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    Text(
                        text = guia.contenido,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}
