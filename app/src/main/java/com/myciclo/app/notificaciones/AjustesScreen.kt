package com.myciclo.app.notificaciones

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.myciclo.app.ui.componentes.AvisoEva
import java.util.Locale

/**
 * Pantalla de Configuración de Notificaciones y Recordatorio Diario EVA.
 * Permite activar/desactivar la alarma diaria, cambiar la hora/minuto con TimePicker,
 * solicitar permisos en Android 13+ y sincronizar con AlarmManager.
 */
@Composable
fun AjustesScreen() {
    val context = LocalContext.current
    val prefs = remember { AjustesPreferences(context) }

    var activadas by remember { mutableStateOf(prefs.notificacionesActivadas) }
    var hora by remember { mutableIntStateOf(prefs.horaRecordatorio) }
    var minuto by remember { mutableIntStateOf(prefs.minutoRecordatorio) }
    var estadoMensaje by remember { mutableStateOf("") }

    // Permission launcher for Android 13+
    val permisoNotificaciones = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            activadas = true
            prefs.notificacionesActivadas = true
            NotificacionHelper.sincronizarNotificacion(context)
            estadoMensaje = "Permiso concedido. Recordatorio activado."
        } else {
            activadas = false
            prefs.notificacionesActivadas = false
            NotificacionHelper.cancelarRecordatorio(context)
            estadoMensaje = "Sin permiso de notificación no se enviarán alarmas."
        }
    }

    fun guardarYSincronizar(nuevaActivacion: Boolean, nuevaHora: Int, nuevoMinuto: Int) {
        prefs.notificacionesActivadas = nuevaActivacion
        prefs.horaRecordatorio = nuevaHora
        prefs.minutoRecordatorio = nuevoMinuto

        activadas = nuevaActivacion
        hora = nuevaHora
        minuto = nuevoMinuto

        estadoMensaje = if (nuevaActivacion) {
            NotificacionHelper.sincronizarNotificacion(context)
            val horaFormateada = String.format(Locale.getDefault(), "%02d:%02d", nuevaHora, nuevoMinuto)
            "Recordatorio diario programado a las $horaFormateada"
        } else {
            NotificacionHelper.cancelarRecordatorio(context)
            "Recordatorio diario desactivado"
        }
    }

    fun abrirTimePicker() {
        TimePickerDialog(
            context,
            { _, h, m ->
                guardarYSincronizar(activadas, h, m)
            },
            hora,
            minuto,
            true
        ).show()
    }

    val horaTexto = String.format(Locale.getDefault(), "%02d:%02d hrs", hora, minuto)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ajustes de Notificación",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // SWITCH ACTIVACIÓN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Recordatorio Diario EVA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Alerta local diaria para registrar la observación de la saliva",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = activadas,
                        onCheckedChange = { activado ->
                            if (activado && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val tienePermiso = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED

                                if (!tienePermiso) {
                                    permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    return@Switch
                                }
                            }
                            guardarYSincronizar(activado, hora, minuto)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                HorizontalDivider()

                // SELECTOR DE HORA (TIME PICKER)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = activadas) { abrirTimePicker() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = if (activadas) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Hora del recordatorio",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (activadas) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (activadas) "Toca para cambiar la hora" else "Activa la notificación para configurar",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { abrirTimePicker() },
                        enabled = activadas
                    ) {
                        Text(horaTexto, fontWeight = FontWeight.Bold)
                    }
                }

                if (estadoMensaje.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = estadoMensaje,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        AvisoEva()
    }
}
