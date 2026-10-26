package com.myciclo.app.calendario

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myciclo.app.registro.Nivel
import com.myciclo.app.registro.RegistroEntity
import com.myciclo.app.ui.componentes.AvisoEva
import com.myciclo.app.ui.theme.EvaAltoColor
import com.myciclo.app.ui.theme.EvaMedioColor
import com.myciclo.app.ui.theme.EvaNuloColor
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

    // Día seleccionado para ver detalle
    var seleccionado by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ---------- TARJETA DE RESUMEN DEL CICLO ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (estado.diaCiclo != null) "Día ${estado.diaCiclo} del ciclo" else "Sin registros aún",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                val fase = estado.fase
                if (fase != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = fase.texto,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                val ventana = estado.ventana
                if (ventana != null) {
                    Text(
                        text = "✨ Ventana EVA: ${ventana.inicio.format(formatoCorto)} al ${ventana.fin.format(formatoCorto)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Aún no registras patrón de helecho en este ciclo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                val promedio = estado.promedioCiclo
                if (promedio != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Promedio de ciclo: $promedio días",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // ---------- CONTROLES DE NAVEGACIÓN DE MES ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { mes = mes.minusMonths(1) }) {
                        Text("<", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${meses[mes.monthValue - 1]} ${mes.year}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    TextButton(onClick = { mes = mes.plusMonths(1) }) {
                        Text(">", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Encabezado Días de la semana
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("L", "M", "M", "J", "V", "S", "D").forEach { diaSemana ->
                        Text(
                            text = diaSemana,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Celdas del mes
                val vacios = mes.atDay(1).dayOfWeek.value - 1
                val celdas = List(vacios) { null } + (1..mes.lengthOfMonth()).map { mes.atDay(it) }

                celdas.chunked(7).forEach { semana ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        semana.forEach { dia ->
                            Box(modifier = Modifier.weight(1f)) {
                                if (dia != null) {
                                    CeldaDia(
                                        dia = dia,
                                        nivel = estado.dias[dia],
                                        enVentana = estado.ventana?.contiene(dia) == true,
                                        onClick = { seleccionado = dia }
                                    )
                                }
                            }
                        }
                        repeat(7 - semana.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }

        // ---------- LEYENDA DE COLORES DE EVA ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Leyenda de observaciones EVA:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ItemLeyenda(color = EvaAltoColor, etiqueta = "Alto")
                    ItemLeyenda(color = EvaMedioColor, etiqueta = "Medio")
                    ItemLeyenda(color = EvaNuloColor, etiqueta = "Nulo")
                    ItemLeyenda(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        etiqueta = "Ventana EVA",
                        esCuadro = true
                    )
                }
            }
        }

        AvisoEva()
    }

    // Diálogo de detalle por día
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

@Composable
private fun ItemLeyenda(color: Color, etiqueta: String, esCuadro: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (esCuadro) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color, CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = etiqueta, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun CeldaDia(dia: LocalDate, nivel: Nivel?, enVentana: Boolean, onClick: () -> Unit) {
    val fondo = if (enVentana) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val bordeColor = if (enVentana) MaterialTheme.colorScheme.primary else Color.Transparent

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(fondo)
            .border(if (enVentana) 1.dp else 0.dp, bordeColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dia.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (enVentana) FontWeight.Bold else FontWeight.Normal,
            color = if (enVentana) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
        if (nivel != null) {
            val colorIndicator = when (nivel) {
                Nivel.ALTO -> EvaAltoColor
                Nivel.MEDIO -> EvaMedioColor
                Nivel.NULO -> EvaNuloColor
            }
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(colorIndicator, CircleShape)
            )
        }
    }
}

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
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(dia.format(formato), fontWeight = FontWeight.Bold)
            }
        },
        text = {
            if (registro == null) {
                Text("No hay observaciones registradas para este día.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Observación EVA: " + Nivel.valueOf(registro.nivel).texto,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    if (registro.notas.isNotEmpty()) {
                        Text(
                            text = "Notas: " + registro.notas,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (registro.foto.isNotEmpty()) {
                        val imagen = remember(registro.foto) { BitmapFactory.decodeFile(registro.foto) }
                        if (imagen != null) {
                            Image(
                                bitmap = imagen.asImageBitmap(),
                                contentDescription = "Foto del patrón EVA",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    )
}