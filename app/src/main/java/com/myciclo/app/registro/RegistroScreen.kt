package com.myciclo.app.registro

import android.Manifest
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")

// revisa que el texto sea una fecha valida tipo 08/10/2026, si no devuelve null
fun leerFecha(texto: String): LocalDate? {
    return try {
        LocalDate.parse(texto, formato)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun RegistroScreen(vm: RegistroViewModel = viewModel()) {
    val mensaje by vm.mensaje.collectAsState()

    // el context lo pedimos una vez y lo usan la camara, la galeria y el fileprovider
    val context = LocalContext.current

    // estado del formulario
    var fecha by remember { mutableStateOf(LocalDate.now().format(formato)) }
    var inicioPeriodo by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf<Nivel?>(null) }
    var notas by remember { mutableStateOf("") }

    // ruta de la foto del patron, queda vacia mientras no haya ninguna
    var foto by remember { mutableStateOf("") }

    // archivo donde la camara va a escribir, lo guardamos para leerlo cuando vuelva
    var archivoCamara by remember { mutableStateOf<File?>(null) }

    // launcher de la camara, devuelve true solo si la usuaria acepto la foto
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada ->
        if (tomada) {
            foto = archivoCamara?.absolutePath ?: ""
        }
    }

    // launcher del permiso de camara, recien cuando lo dan abrimos la camara
    val permisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { dado ->
        if (dado) {
            // creamos el archivo antes de abrir la camara porque hay que pasarle el uri
            val archivo = Fotos.archivoNuevo(context)
            archivoCamara = archivo
            camara.launch(Fotos.uriDe(context, archivo))
        }
    }

    // launcher de la galeria, lo que elija lo copiamos a la carpeta de la app
    val galeria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val copia = Fotos.copiarDesdeGaleria(context, uri)
            if (copia != null) {
                foto = copia.absolutePath
            }
        }
    }

    // validaciones
    val fechaOk = leerFecha(fecha)
    val inicioOk = leerFecha(inicioPeriodo)
    val errorFecha = fecha.isNotEmpty() && fechaOk == null
    val errorInicio = inicioPeriodo.isNotEmpty() &&
        (inicioOk == null || (fechaOk != null && inicioOk.isAfter(fechaOk)))
    val puedeGuardar = fechaOk != null && inicioOk != null && !errorInicio && nivel != null

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar observación", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha (dd/mm/aaaa)") },
            isError = errorFecha,
            supportingText = { if (errorFecha) Text("Fecha inválida") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = inicioPeriodo,
            onValueChange = { inicioPeriodo = it },
            label = { Text("Inicio de tu último periodo (dd/mm/aaaa)") },
            isError = errorInicio,
            supportingText = { if (errorInicio) Text("Fecha inválida o posterior al registro") },
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
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        // foto del patron de la guia 13, es opcional igual que las notas
        Text("Foto del patrón (opcional)")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // primero pedimos el permiso, la camara se abre en el callback de arriba
            OutlinedButton(onClick = { permisoCamara.launch(Manifest.permission.CAMERA) }) {
                Text("Cámara")
            }
            // la galeria no necesita permiso porque la usuaria elige el archivo
            OutlinedButton(onClick = { galeria.launch("image/*") }) {
                Text("Galería")
            }
        }

        // si ya hay foto la mostramos chica para confirmar que quedo bien
        if (foto.isNotEmpty()) {
            // remember con la ruta como llave: solo lee el archivo cuando cambia la foto
            val imagen = remember(foto) { BitmapFactory.decodeFile(foto) }
            if (imagen != null) {
                Image(
                    bitmap = imagen.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            // por si se equivoco de foto, solo borramos la ruta del formulario
            TextButton(onClick = { foto = "" }) { Text("Quitar foto") }
        }

        Button(
            onClick = {
                // guardamos las fechas en formato yyyy-MM-dd y la ruta de la foto
                vm.guardar(fechaOk.toString(), inicioOk.toString(), nivel!!, notas, foto)
            },
            enabled = puedeGuardar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        if (mensaje.isNotEmpty()) {
            Text(mensaje, color = MaterialTheme.colorScheme.primary)
        }
    }
}
