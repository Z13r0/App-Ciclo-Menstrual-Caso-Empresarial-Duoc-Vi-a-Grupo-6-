package com.myciclo.app.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    // estado del formulario
    var fecha by remember { mutableStateOf(LocalDate.now().format(formato)) }
    var inicioPeriodo by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf<Nivel?>(null) }
    var notas by remember { mutableStateOf("") }

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

        // TODO integrante 1: agregar foto del patron con la camara (ver guia 13)

        Button(
            onClick = {
                // guardamos las fechas en formato yyyy-MM-dd
                vm.guardar(fechaOk.toString(), inicioOk.toString(), nivel!!, notas)
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
