package com.myciclo.app.calendario

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myciclo.app.registro.Nivel
import com.myciclo.app.registro.RegistroEntity
import com.myciclo.app.ui.componentes.AvisoEva
import com.myciclo.app.ui.theme.Gris
import com.myciclo.app.ui.theme.Morado
import com.myciclo.app.ui.theme.MoradoClaro
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val meses = listOf(
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
)

@Composable
fun CalendarioScreen(vm: CalendarioViewModel = viewModel()) {
    val estado by vm.estado.collectAsState()
    var mes by remember { mutableStateOf(YearMonth.now()) }
    val formatoCorto = DateTimeFormatter.ofPattern("dd/MM")
    val formatoLargo = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // dia que toco la usuaria, null significa que no hay dialogo abierto
    var seleccionado by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // resumen arriba
        Text(
            text = if (estado.diaCiclo != null) "Día ${estado.diaCiclo} del ciclo" else "Aún no hay registros",
            style = MaterialTheme.typography.headlineSmall
        )
        val ventana = estado.ventana
        if (ventana != null) {
            Text("Ventana EVA: ${ventana.inicio.format(formatoCorto)} al ${ventana.fin.format(formatoCorto)}")
        } else {
            Text("Todavía no registras helecho en este ciclo")
        }

        // flechas para cambiar de mes
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { mes = mes.minusMonths(1) }) { Text("<") }
            Text(
                text = "${meses[mes.monthValue - 1]} ${mes.year}",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = { mes = mes.plusMonths(1) }) { Text(">") }
        }

        Row {
            listOf("L", "M", "M", "J", "V", "S", "D").forEach {
                Text(it, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            }
        }

        // armamos la lista de dias del mes, con espacios vacios antes del dia 1
        val vacios = mes.atDay(1).dayOfWeek.value - 1
        val celdas = List(vacios) { null } + (1..mes.lengthOfMonth()).map { mes.atDay(it) }

        // la dividimos en semanas de 7
        celdas.chunked(7).forEach { semana ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                semana.forEach { dia ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (dia != null) {
                            // al tocar la celda guardamos el dia y eso abre el dialogo
                            CeldaDia(
                                dia = dia,
                                nivel = estado.dias[dia],
                                enVentana = ventana?.contiene(dia) == true,
                                onClick = { seleccionado = dia }
                            )
                        }
                    }
                }
                // relleno para que la ultima semana no se estire
                repeat(7 - semana.size) { Spacer(Modifier.weight(1f)) }
            }
        }

        Text("Morado: alto · Rosado: medio · Gris: nulo · Fondo: ventana EVA",
            style = MaterialTheme.typography.bodySmall)
        Text("Toca un día para ver lo que registraste",
            style = MaterialTheme.typography.bodySmall)

        AvisoEva()
    }

    // el dialogo se dibuja fuera de la columna para que tape toda la pantalla
    val dia = seleccionado
    if (dia != null) {
        DetalleDia(
            dia = dia,
            registro = estado.detalles[dia],
            formato = formatoLargo,
            onCerrar = { seleccionado = null }
        )
    }
}

// dialogo con lo que la usuaria anoto ese dia
// registro llega null cuando toco un dia que nunca registro
@Composable
fun DetalleDia(
    dia: LocalDate,
    registro: RegistroEntity?,
    formato: DateTimeFormatter,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } },
        title = { Text(dia.format(formato)) },
        text = {
            if (registro == null) {
                Text("Ese día no tiene registro")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // el nivel esta guardado como texto, lo volvemos al enum para mostrar su etiqueta
                    Text("Observación: " + Nivel.valueOf(registro.nivel).texto)

                    // las notas son opcionales, si estan vacias no mostramos la linea
                    if (registro.notas.isNotEmpty()) {
                        Text("Notas: " + registro.notas)
                    }

                    // la foto tambien es opcional y se lee desde la carpeta de la app
                    if (registro.foto.isNotEmpty()) {
                        val imagen = remember(registro.foto) { BitmapFactory.decodeFile(registro.foto) }
                        if (imagen != null) {
                            Image(
                                bitmap = imagen.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CeldaDia(dia: LocalDate, nivel: Nivel?, enVentana: Boolean, onClick: () -> Unit) {
    val fondo = if (enVentana) MoradoClaro else Color.Transparent
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .background(fondo, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(dia.dayOfMonth.toString())
        // puntito con el color del nivel si ese dia tiene registro
        if (nivel != null) {
            val color = when (nivel) {
                Nivel.ALTO -> Morado
                Nivel.MEDIO -> Color(0xFFD889B5)
                Nivel.NULO -> Gris
            }
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        }
    }
}
