package com.myciclo.app.registro

import android.Manifest
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.app.Application
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myciclo.app.ui.componentes.AvisoEva
import com.myciclo.app.ui.theme.EvaAltoColor
import com.myciclo.app.ui.theme.EvaMedioColor
import com.myciclo.app.ui.theme.EvaNuloColor
import com.myciclo.app.util.FechaUtils
import kotlinx.coroutines.delay

/**
 * Pantalla de registro diario de observaciones EVA.
 * Integrada totalmente con FechaUtils y filtrado/búsqueda con estado en ViewModel.
 */
@Composable
fun RegistroScreen(
    userId: String = "",
    vm: RegistroViewModel = run {
        val context = LocalContext.current
        viewModel(
            key = "RegistroViewModel_$userId",
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RegistroViewModel(context.applicationContext as Application, userId) as T
                }
            }
        )
    }
) {
    val context = LocalContext.current
    val registros by vm.registros.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val filtroNivel by vm.filtroNivel.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    // Estado del formulario
    var fecha by remember { mutableStateOf(FechaUtils.hoyIso()) }
    var inicioPeriodo by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf<Nivel?>(null) }
    var notas by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf(false) }

    // Autocompletar inicio de período si la lista de registros ya existe
    LaunchedEffect(registros) {
        if (inicioPeriodo.isBlank() && registros.isNotEmpty()) {
            inicioPeriodo = registros.first().inicioPeriodo
        }
    }

    // Foto ampliada para diálogo modal
    var fotoAmpliada by remember { mutableStateOf<String?>(null) }

    // Validaciones con FechaUtils
    val fechaValida = FechaUtils.esValida(fecha)
    val inicioValido = FechaUtils.esValida(inicioPeriodo)
    val puedeGuardar = nivel != null && fechaValida && inicioValido

    // Launchers de cámara y galería
    val archivoFoto = remember { mutableStateOf(Fotos.archivoNuevo(context)) }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) foto = archivoFoto.value.absolutePath
    }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val destino = Fotos.archivoNuevo(context)
            context.contentResolver.openInputStream(uri)?.use { input ->
                destino.outputStream().use { output -> input.copyTo(output) }
            }
            foto = destino.absolutePath
        }
    }
    val permisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            archivoFoto.value = Fotos.archivoNuevo(context)
            camara.launch(Fotos.uriDe(context, archivoFoto.value))
        }
    }

    fun cargarParaEditar(reg: RegistroEntity) {
        fecha = reg.fecha
        inicioPeriodo = reg.inicioPeriodo
        nivel = try { Nivel.valueOf(reg.nivel) } catch (_: Exception) { Nivel.NULO }
        notas = reg.notas
        foto = reg.foto
        editando = true
    }

    fun limpiarFormulario() {
        fecha = FechaUtils.hoyIso()
        inicioPeriodo = registros.firstOrNull()?.inicioPeriodo ?: ""
        nivel = null
        notas = ""
        foto = ""
        editando = false
    }

    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            delay(2500)
            vm.limpiarMensaje()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ---------- TARJETA FORMULARIO ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (editando) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (editando) "Editar registro" else "Nueva observación EVA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha de registro (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !editando,
                    isError = fecha.isNotBlank() && !fechaValida,
                    supportingText = {
                        if (fecha.isNotBlank() && !fechaValida) {
                            Text(
                                text = "Formato de fecha inválido (ej: yyyy-MM-dd o dd/MM/yyyy)",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                OutlinedTextField(
                    value = inicioPeriodo,
                    onValueChange = { inicioPeriodo = it },
                    label = { Text("Inicio del período (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = inicioPeriodo.isNotBlank() && !inicioValido,
                    supportingText = {
                        if (inicioPeriodo.isNotBlank() && !inicioValido) {
                            Text(
                                text = "Formato de fecha inválido (ej: yyyy-MM-dd o dd/MM/yyyy)",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                Text(
                    text = "¿Qué patrón observaste en EVA?",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Nivel.entries.forEach { opcion ->
                        val selected = nivel == opcion
                        FilterChip(
                            selected = selected,
                            onClick = { nivel = opcion },
                            label = { Text(opcion.texto) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Síntomas o notas adicionales (opcional)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Fotografía del patrón EVA (opcional)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { permisoCamara.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📷 Cámara")
                    }
                    OutlinedButton(
                        onClick = { galeria.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🖼️ Galería")
                    }
                }

                if (foto.isNotEmpty()) {
                    val imagen = remember(foto) { BitmapFactory.decodeFile(foto) }
                    if (imagen != null) {
                        Image(
                            bitmap = imagen.asImageBitmap(),
                            contentDescription = "Foto del patrón",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { fotoAmpliada = foto },
                            contentScale = ContentScale.Crop
                        )
                    }
                    TextButton(onClick = { foto = "" }) { Text("Quitar fotografía") }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (puedeGuardar) {
                                vm.guardar(fecha, inicioPeriodo, nivel!!, notas, foto)
                                limpiarFormulario()
                            }
                        },
                        enabled = puedeGuardar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (editando) "Actualizar" else "Guardar Registro")
                    }
                    if (editando) {
                        OutlinedButton(onClick = { limpiarFormulario() }) {
                            Text("Cancelar")
                        }
                    }
                }

                if (mensaje.isNotEmpty()) {
                    Text(
                        text = mensaje,
                        color = if (mensaje.startsWith("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                AvisoEva()
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

        // ---------- FILTRADO Y BÚSQUEDA DE HISTORIAL ----------
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Historial de Observaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = busqueda,
                onValueChange = { vm.actualizarBusqueda(it) },
                placeholder = { Text("Buscar en notas...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = filtroNivel == null,
                    onClick = { vm.actualizarFiltroNivel(null) },
                    label = { Text("Todos") }
                )
                Nivel.entries.forEach { n ->
                    FilterChip(
                        selected = filtroNivel == n,
                        onClick = { vm.actualizarFiltroNivel(if (filtroNivel == n) null else n) },
                        label = { Text(n.texto) }
                    )
                }
            }
        }

        if (registros.isEmpty()) {
            Text(
                text = "No se encontraron registros.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(registros, key = { it.fecha }) { reg ->
                    CardRegistro(
                        registro = reg,
                        onEditar = { cargarParaEditar(reg) },
                        onBorrar = { vm.borrar(reg.fecha) },
                        onVerFoto = { path -> fotoAmpliada = path }
                    )
                }
            }
        }
    }

    // ---------- VISOR DE FOTO AMPLIADA EN DIÁLOGO MODAL ----------
    fotoAmpliada?.let { path ->
        val bmp = remember(path) { BitmapFactory.decodeFile(path) }
        Dialog(onDismissRequest = { fotoAmpliada = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fotografía ampliada",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { fotoAmpliada = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }
                    if (bmp != null) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Fotografía en tamaño completo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text("No se pudo cargar la imagen.", style = MaterialTheme.typography.bodyMedium)
                    }
                    TextButton(onClick = { fotoAmpliada = null }) { Text("Cerrar") }
                }
            }
        }
    }
}

@Composable
private fun CardRegistro(
    registro: RegistroEntity,
    onEditar: () -> Unit,
    onBorrar: () -> Unit,
    onVerFoto: (String) -> Unit
) {
    var confirmarBorrado by remember { mutableStateOf(false) }
    val nivelEnum = try { Nivel.valueOf(registro.nivel) } catch (_: Exception) { Nivel.NULO }

    val badgeColor = when (nivelEnum) {
        Nivel.ALTO -> EvaAltoColor
        Nivel.MEDIO -> EvaMedioColor
        Nivel.NULO -> EvaNuloColor
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = registro.fecha,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = nivelEnum.texto,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                if (registro.notas.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = registro.notas,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
            }

            if (registro.foto.isNotBlank()) {
                val thumbnail = remember(registro.foto) { BitmapFactory.decodeFile(registro.foto) }
                if (thumbnail != null) {
                    Image(
                        bitmap = thumbnail.asImageBitmap(),
                        contentDescription = "Ver foto",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onVerFoto(registro.foto) },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { confirmarBorrado = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text("¿Eliminar registro?") },
            text = {
                Text("Se eliminará la observación del ${registro.fecha}. Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = {
                    onBorrar()
                    confirmarBorrado = false
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") }
            }
        )
    }
}
