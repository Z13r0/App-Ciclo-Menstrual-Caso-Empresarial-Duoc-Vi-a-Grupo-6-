package com.myciclo.app.registro

import android.Manifest
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myciclo.app.ui.componentes.AvisoEva
import java.time.LocalDate
import kotlinx.coroutines.delay

/**
 * Pantalla de registro diario de observaciones EVA.
 * - Formulario para el día actual (o edición de un día anterior).
 * - Lista de historial con opción de editar y borrar.
 * - Foto opcional del patrón (cámara o galería) guardada solo dentro de la app.
 */
@Composable
fun RegistroScreen(vm: RegistroViewModel = viewModel()) {
    val context = LocalContext.current
    val registros by vm.registros.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    // Estado del formulario
    var fecha by remember { mutableStateOf(LocalDate.now().toString()) }
    var inicioPeriodo by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf<Nivel?>(null) }
    var notas by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf(false) }

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

    // Cargar un registro existente en el formulario para editarlo
    fun cargarParaEditar(reg: RegistroEntity) {
        fecha = reg.fecha
        inicioPeriodo = reg.inicioPeriodo
        nivel = Nivel.valueOf(reg.nivel)
        notas = reg.notas
        foto = reg.foto
        editando = true
    }

    fun limpiarFormulario() {
        fecha = LocalDate.now().toString()
        inicioPeriodo = ""
        nivel = null
        notas = ""
        foto = ""
        editando = false
    }

    val puedeGuardar = nivel != null && inicioPeriodo.isNotBlank()

    // Limpiar el mensaje después de unos segundos
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            delay(2500)
            vm.limpiarMensaje()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            if (editando) "Editar registro" else "Registrar observación EVA",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(8.dp))

        // ---------- FORMULARIO ----------
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (dd/mm/yyyy)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !editando // no se cambia la fecha al editar
            )

            OutlinedTextField(
                value = inicioPeriodo,
                onValueChange = { inicioPeriodo = it },
                label = { Text("Inicio del período (dd/mm/yyyy)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("¿Qué observaste en EVA?")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Nivel.values().forEach { opcion ->
                    FilterChip(
                        selected = nivel == opcion,
                        onClick = { nivel = opcion },
                        label = { Text(opcion.texto) }
                    )
                }
            }

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Síntomas o notas (opcional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Foto del patrón (opcional)")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { permisoCamara.launch(Manifest.permission.CAMERA) }) {
                    Text("Cámara")
                }
                OutlinedButton(onClick = { galeria.launch("image/*") }) {
                    Text("Galería")
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
                            .height(160.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                TextButton(onClick = { foto = "" }) { Text("Quitar foto") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        vm.guardar(fecha, inicioPeriodo, nivel!!, notas, foto)
                        limpiarFormulario()
                    },
                    enabled = puedeGuardar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (editando) "Actualizar" else "Guardar")
                }
                if (editando) {
                    OutlinedButton(onClick = { limpiarFormulario() }) {
                        Text("Cancelar")
                    }
                }
            }

            if (mensaje.isNotEmpty()) {
                Text(mensaje, color = MaterialTheme.colorScheme.primary)
            }

            AvisoEva()
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // ---------- HISTORIAL ----------
        Text("Historial", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (registros.isEmpty()) {
            Text(
                "Aún no hay registros. Guarda tu primera observación.",
                style = MaterialTheme.typography.bodyMedium
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
                        onBorrar = { vm.borrar(reg.fecha) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardRegistro(
    registro: RegistroEntity,
    onEditar: () -> Unit,
    onBorrar: () -> Unit
) {
    var confirmarBorrado by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(registro.fecha, style = MaterialTheme.typography.titleSmall)
                Text(
                    Nivel.valueOf(registro.nivel).texto,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (registro.notas.isNotBlank()) {
                    Text(
                        registro.notas,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = { confirmarBorrado = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar")
            }
        }
    }

    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text("¿Borrar registro?") },
            text = {
                Text("Se eliminará el registro del ${registro.fecha}. Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = {
                    onBorrar()
                    confirmarBorrado = false
                }) { Text("Borrar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") }
            }
        )
    }
}